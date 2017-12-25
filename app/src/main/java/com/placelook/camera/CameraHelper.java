package com.placelook.camera;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by victor on 05.11.17.
 */

public class CameraHelper {
    private static final String TAG = CameraHelper.class.getSimpleName();
    private String id;
    private CameraDevice device;
    private CameraManager manager;
    private TextureView txTexture;
    private ImageReader reader;
    private int width;
    private int height;
    private Handler handler;
    private CameraCaptureSession session;
    private Queue<Frame> queue;
    private Queue<Frame> encoded;
    private AVCEncoder encoder;

    public CameraHelper(CameraManager manager, String idCamera) {
        this.id = idCamera;
        this.manager = manager;
        this.device = null;
        queue = new ArrayBlockingQueue<Frame>(30, true);
        encoded = new ArrayBlockingQueue<Frame>(30, true);
    }

    public boolean isOpened() {
        return (device != null);
    }

    @SuppressLint("MissingPermission")
    public void open(int width, int height) {
        final String mime = "video/avc";
        this.width = width;
        this.height = height;
        final int frameRate = 30;
        final int iFrame = 5;
        try {
            manager.openCamera(id, cdc, null);
            encoder = new AVCEncoder();
            encoder.addParameter(MediaFormat.KEY_MIME, mime);
            encoder.addParameter(MediaFormat.KEY_WIDTH, width);
            encoder.addParameter(MediaFormat.KEY_HEIGHT, height);
            encoder.addParameter(MediaFormat.KEY_FRAME_RATE, frameRate);
            encoder.addParameter(MediaFormat.KEY_BIT_RATE, Encoder.calcBitRate(frameRate, width, height));
            encoder.addParameter(MediaFormat.KEY_COLOR_FORMAT, Encoder.checkColorFormat(mime));
            encoder.addParameter(MediaFormat.KEY_I_FRAME_INTERVAL, iFrame);
            //encoder.setOnEncodedFrameListener(this);
            if (encoder.open()) encoder.start();
            else throw new IllegalCodecException();
        } catch (IllegalCodecException e) {
            e.printStackTrace();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        encoder.stop();
        encoder.close();
        if (device != null) {
            device.close();
            device = null;
            Log.i(TAG, "Camera closed");
        }
    }

    public byte[] getBytes(Image image) {
        int format = image.getFormat();
        int width = image.getWidth();
        int height = image.getHeight();
        int rowStride, pixelStride;
        byte[] data = null;
        Image.Plane[] planes = image.getPlanes();

        //checkAndroidImageFormat(image);

        ByteBuffer buffer = null;
        if (format == ImageFormat.JPEG) {
            buffer = planes[0].getBuffer();
            data = new byte[buffer.capacity()];
            buffer.get(data);
            return data;
        }
        int offset = 0;
        data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        for (int i = 0; i < planes.length; i++) {
            buffer = planes[i].getBuffer();
            rowStride = planes[i].getRowStride();
            pixelStride = planes[i].getPixelStride();
            int w = (i == 0) ? width : width / 2;
            int h = (i == 0) ? height : height / 2;
            for (int row = 0; row < h; row++) {
                int bytesPerPixel = ImageFormat.getBitsPerPixel(format) / 8;
                if (pixelStride == bytesPerPixel) {
                    int length = w * bytesPerPixel;
                    buffer.get(data, offset, length);
                    buffer.position(buffer.position() + rowStride - length);
                    offset += length;
                } else {
                    buffer.get(rowData, 0, rowStride);
                    for (int col = 0; col < w; col++) {
                        data[offset++] = rowData[col * pixelStride];
                    }
                }
            }
        }
        return data;
    }

    public void setTextureView(TextureView txTexture) {
        this.txTexture = txTexture;
    }

    public void createCameraPreviewSession() {

        HandlerThread handlerThread = new HandlerThread("Image Processing Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        SurfaceTexture texture = txTexture.getSurfaceTexture();
        texture.setDefaultBufferSize(width, height);
        Surface surface = new Surface(texture);
        reader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 3);
        reader.setOnImageAvailableListener(iaListener, null);
        try {
            final CaptureRequest.Builder builder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);
            builder.addTarget(reader.getSurface());
            device.createCaptureSession(Arrays.asList(surface, reader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    CameraHelper.this.session = session;
                    try {
                        CameraHelper.this.session.setRepeatingRequest(builder.build(), null, null);
                    } catch (CameraAccessException ex) {
                        ex.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, null);

        } catch (CameraAccessException ex) {
            ex.printStackTrace();
        }
    }

    private void encode() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Frame image = queue.poll();
                encoder.encode(image.getData());
            }
        });
    }

    private ImageReader.OnImageAvailableListener iaListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(final ImageReader reader) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Image image = null;
                    try {
                        image = reader.acquireNextImage();

                        if (image != null) {
                            byte[] bytes = getBytes(image);
                            queue.add(new Frame(bytes));
                            encode();
                        }
                    } catch (IllegalStateException ex) {
                        ex.printStackTrace();
                    } finally {
                        if (image != null) image.close();
                    }
                }
            });
        }
    };

    private CameraDevice.StateCallback cdc = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            device = camera;
            createCameraPreviewSession();
            Log.i(TAG, "Camera opened");
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            device.close();
            device = null;
            Log.i(TAG, "Camera disconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.i(TAG, "Error camera ID: " + camera.getId() + ", error: " + error);
        }
    };
}

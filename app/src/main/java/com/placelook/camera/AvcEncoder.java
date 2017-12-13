package com.placelook.camera;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import com.placelook.util.Parameter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class AvcEncoder extends Encoder {
    private static final String TAG = AvcEncoder.class.getSimpleName();
    private EncodedFrameListener frameListener;
    private MediaCodec mediaCodec;

    private byte[] sps;
    private byte[] pps;
    private ParameterSetsListener parameterSetsListener;
    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;

    public AvcEncoder() {
        super();
    }

    public AvcEncoder(ArrayList<Parameter> list) {
        super(list);
    }

    public boolean open() {
        try {
            mediaCodec = MediaCodec.createEncoderByType(getStringParameter(MediaFormat.KEY_MIME));
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(getStringParameter(MediaFormat.KEY_MIME), getIntParameter(MediaFormat.KEY_WIDTH), getIntParameter(MediaFormat.KEY_HEIGHT));
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, getIntParameter(MediaFormat.KEY_BIT_RATE));
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, getIntParameter(MediaFormat.KEY_FRAME_RATE));
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, getIntParameter(MediaFormat.KEY_COLOR_FORMAT));
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, getIntParameter(MediaFormat.KEY_I_FRAME_INTERVAL));
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            return true;
        } catch (UndefinedImportantParameterException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean start() {
        mediaCodec.start();
        inputBuffers = mediaCodec.getInputBuffers();
        outputBuffers = mediaCodec.getOutputBuffers();
        return true;
    }

    @Override
    public void encode(byte[] input) {
        try {
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(input);
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, 0, 0);
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            while (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);
                if (sps != null && pps != null) {
                    ByteBuffer frameBuffer = ByteBuffer.wrap(outData);
                    frameBuffer.putInt(bufferInfo.size - 4);
                    if (null != frameListener) {
                        frameListener.onEncoded(outData, 0, outData.length);
                    }
                } else {
                    ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
                    if (spsPpsBuffer.getInt() == 0x00000001) {
                        Log.i(TAG, "parsing sps/pps");
                    } else {
                        Log.i(TAG, "something is amiss?");
                    }
                    int ppsIndex = 0;
                    while (!(spsPpsBuffer.get() == 0x00 && spsPpsBuffer.get() == 0x00 && spsPpsBuffer.get() == 0x00 && spsPpsBuffer.get() == 0x01)) {

                    }
                    ppsIndex = spsPpsBuffer.position();
                    sps = new byte[ppsIndex - 8];
                    System.arraycopy(outData, 4, sps, 0, sps.length);
                    pps = new byte[outData.length - ppsIndex];
                    System.arraycopy(outData, ppsIndex, pps, 0, pps.length);
                    if (null != parameterSetsListener) {
                        parameterSetsListener.onParameters(sps, pps);
                    }
                }
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    @Override
    public boolean stop() {
        mediaCodec.stop();
        return true;
    }

    @Override
    public boolean close() {
        mediaCodec.release();
        return true;
    }

    public void setOnEncodedFrameListener(EncodedFrameListener listener) {
        frameListener = listener;
    }

}
package com.placelook.camera;

import android.media.MediaFormat;

import com.placelook.util.Parameter;

import java.util.ArrayList;

public class AVCEncoder extends Encoder {
    private static final String TAG = AVCEncoder.class.getSimpleName();

    static {
        System.loadLibrary("NativeCoder");
    }

    public AVCEncoder() {
        super();
    }

    public AVCEncoder(ArrayList<Parameter> list) {
        super(list);
        String mime = getStringParameter(MediaFormat.KEY_MIME);
        int width = getIntParameter(MediaFormat.KEY_WIDTH);
        int height = getIntParameter(MediaFormat.KEY_HEIGHT);
        int frameRate = getIntParameter(MediaFormat.KEY_FRAME_RATE);
        int bitRate = getIntParameter(MediaFormat.KEY_BIT_RATE);
        int colorFormat = getIntParameter(MediaFormat.KEY_COLOR_FORMAT);
        int iFrame = getIntParameter(MediaFormat.KEY_I_FRAME_INTERVAL);
        nativeCreate(mime, width, height, frameRate, bitRate, colorFormat, iFrame);
    }

    @Override
    public boolean open() {
        return nativeOpen();
    }

    @Override
    public boolean start() {
        return nativeStart();
    }

    @Override
    public void encode(byte[] input) {
        byte[] output = null;
        nativeEncode(input, output);
    }

    @Override
    public boolean stop() {
        return nativeStop();
    }

    @Override
    public boolean close() {
        return nativeClose();
    }

    private native Object nativeCreate(String mime, int width, int height, int frameRate, int bitRate, int colorFormat, int iFrame);

    private native boolean nativeOpen();

    private native boolean nativeStart();

    private native boolean nativeEncode(byte[] input, byte[] output);

    private native boolean nativeStop();

    private native boolean nativeClose();
}
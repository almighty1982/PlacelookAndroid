package com.placelook.camera;

/**
 * Created by victor on 24.12.17.
 */

public class Frame {
    private byte[] data;

    public Frame() {
        data = null;
    }

    public Frame(byte[] data) {
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public byte[] getData() {
        byte[] result = new byte[this.data.length];
        System.arraycopy(this.data, 0, result, 0, this.data.length);
        return result;
    }
}

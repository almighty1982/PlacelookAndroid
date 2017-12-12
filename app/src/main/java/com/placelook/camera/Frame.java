package com.placelook.camera;

/**
 * Created by victor on 10.12.17.
 */

public class Frame {
    private byte[] data;

    public Frame(byte[] data) {
        this.data = data.clone();
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data.clone();
    }
}

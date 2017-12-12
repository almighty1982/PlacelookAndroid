package com.placelook.camera;

/**
 * Created by victor on 26.11.17.
 */

public interface EncodedFrameListener {
    void onEncoded(byte[] data, int offset, int lenght);
}

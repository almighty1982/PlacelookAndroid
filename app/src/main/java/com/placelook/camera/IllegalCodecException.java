package com.placelook.camera;

/**
 * Created by victor on 13.12.17.
 */

public class IllegalCodecException extends IllegalStateException {
    public IllegalCodecException() {
        super("Illegal codec state");
    }
}

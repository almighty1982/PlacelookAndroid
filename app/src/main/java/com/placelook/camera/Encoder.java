package com.placelook.camera;

import com.placelook.util.Parameter;

import java.util.ArrayList;

/**
 * Created by victor on 26.11.17.
 */

public abstract class Encoder {
    private ArrayList<Parameter> parameters;

    public Encoder() {
        parameters = new ArrayList<Parameter>();
    }

    public abstract boolean open();

    public abstract boolean start();

    public abstract void encode(byte[] data);

    public abstract boolean stop();

    public abstract boolean close();

    public void setParameter(String name, String value) {
        Parameter parameter = new Parameter(name, value);
        parameters.add(parameter);
    }

    public void setParameter(String name, int value) {
        Parameter parameter = new Parameter(name, value);
        parameters.add(parameter);
    }

    public void setParameter(String name, long value) {
        Parameter parameter = new Parameter(name, value);
        parameters.add(parameter);
    }

    public int getIntParameter(String name) {
        for (Parameter parameter : parameters) {
            if (parameter.getName().equals(name)) {
                return (int) parameter.getValue();
            }
        }
        return -1;
    }

    public String getStringParameter(String name) {
        for (Parameter parameter : parameters) {
            if (parameter.getName().equals(name)) {
                return (String) parameter.getValue();
            }
        }
        return "undefined";
    }

}

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

    public Encoder(ArrayList<Parameter> list) {
        this();
        for (Parameter parameter : list) {
            parameters.add(parameter);
        }
    }

    public abstract boolean open();

    public abstract boolean start();

    public abstract void encode(byte[] data);

    public abstract boolean stop();

    public abstract boolean close();

    public void addParameter(String name, String value) {
        Parameter parameter = new Parameter(name, value);
        parameters.add(parameter);
    }

    public void addParameter(String name, int value) {
        Parameter parameter = new Parameter(name, value);
        parameters.add(parameter);
    }

    public void addParameter(String name, long value) {
        Parameter parameter = new Parameter(name, value);
        parameters.add(parameter);
    }

    public int getIntParameter(String name) throws UndefinedImportantParameterException {
        for (Parameter parameter : parameters) {
            if (parameter.getName().equals(name)) {
                return (int) parameter.getValue();
            }
        }
        throw new UndefinedImportantParameterException();
    }

    public String getStringParameter(String name) throws UndefinedImportantParameterException {
        for (Parameter parameter : parameters) {
            if (parameter.getName().equals(name)) {
                return (String) parameter.getValue();
            }
        }
        throw new UndefinedImportantParameterException();
    }

}

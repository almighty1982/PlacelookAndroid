package com.placelook.util;

/**
 * Created by victor on 02.12.17.
 */

public class Parameter {
    private String name;
    private Object value;

    public Parameter() {
        this.name = "";
        this.value = new Object();
    }

    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Parameter(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public Parameter(String name, long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

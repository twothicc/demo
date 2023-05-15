package com.example.demo.model;

import java.io.Serializable;

public class SerializedObj implements Serializable {
    private int obj1;
    private String obj2;

    public SerializedObj(int obj1, String obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public int getObj1() {
        return obj1;
    }

    public String getObj2() {
        return obj2;
    }

    @Override
    public String toString() {
        return String.valueOf(obj1) + " " + obj2;
    }
}

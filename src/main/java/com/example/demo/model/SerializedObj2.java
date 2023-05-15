package com.example.demo.model;

import java.io.Serializable;

public class SerializedObj2 implements Serializable {
    private int obj1;
    private boolean obj2;

    public SerializedObj2(int obj1, boolean obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public int getObj1() {
        return obj1;
    }

    public boolean getObj2() {
        return obj2;
    }

    @Override
    public String toString() {
        return String.valueOf(obj1) + " " + String.valueOf(obj2);
    }
}

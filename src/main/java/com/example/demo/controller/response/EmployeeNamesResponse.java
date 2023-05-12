package com.example.demo.controller.response;

import java.util.ArrayList;

public class EmployeeNamesResponse {
    private final ArrayList<String> names;
    private final String msg;

    public EmployeeNamesResponse(ArrayList<String> names, String msg) {
        this.names = names;
        this.msg = msg;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public String getMsg() {
        return msg;
    }
}

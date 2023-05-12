package com.example.demo.controller.response;

public class CountEligibleResponse {
    private final long count;
    private final String msg;

    public CountEligibleResponse(long count, String msg) {
        this.count = count;
        this.msg = msg;
    }

    public long getCount() {
        return count;
    }

    public String getMsg() {
        return msg;
    }
}

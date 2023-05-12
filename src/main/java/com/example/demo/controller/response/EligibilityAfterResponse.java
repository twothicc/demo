package com.example.demo.controller.response;

public class EligibilityAfterResponse {
    private final Integer afterAge;
    private final String msg;
    private final boolean eligibilitySet;

    public EligibilityAfterResponse(Integer afterAge, boolean eligibilitySet, String msg) {
        this.afterAge = afterAge;
        this.eligibilitySet = eligibilitySet;
        this.msg = msg;
    }

    public Integer getAfterAge() {
        return afterAge;
    }

    public String getMsg() {
        return msg;
    }

    public boolean getEligibilitySet() {
        return eligibilitySet;
    }
}

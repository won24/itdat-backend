package com.itdat.back.entity.auth;

import org.antlr.v4.runtime.misc.NotNull;

public class SmsRequest {
    @NotNull
    private String phoneNumber;

    @NotNull
    private String message;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

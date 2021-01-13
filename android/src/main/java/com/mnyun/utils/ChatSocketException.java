package com.mnyun.utils;

public class ChatSocketException extends Exception {
    public ChatSocketException(String msg) {
        super(msg);
    }
    public ChatSocketException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

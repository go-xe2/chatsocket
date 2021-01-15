package com.mnyun.chatsocket;

import java.nio.ByteBuffer;

public interface ChatSocket {
    void send(String text);
    void send(byte[] data);
    void send(ByteBuffer bytes);
    void close();
    boolean isConnected();
}

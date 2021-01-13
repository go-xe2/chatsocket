package com.mnyun.chatsocket;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class ChatClient implements ChatSocket {
    private final String TAG = ChatClient.class.getName();
    private Heartbeat heartbeat;
    private WebSocketClient client = null;
    public ChatClient() {
    }

    /**
     * 开始发送心跳
     */
    private void startHeartbeat() {
        this.stopHeartbeat();
        this.heartbeat = new ChatSocketHeartbeat(this);
        this.heartbeat.start();
    }
    private void stopHeartbeat() {
        if (this.heartbeat != null) {
            this.heartbeat.stop();
            this.heartbeat = null;
        }
    }

    public void Connect(String host, String deviceId) {
        URI uri = URI.create(host + "?device_id=" + deviceId);
        this.client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "webSocket open");
                ChatClient.this.startHeartbeat();
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "webSocket onMessage:" + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "webSocket onClose:" + reason + remote);
            }

            @Override
            public void onError(Exception ex) {
                Log.d(TAG, "webSocket onError:" + ex.getMessage());
                ChatClient.this.close();
            }
        };
        this.client.connect();
    }

    public void close() {
        if (this.client != null) {
            this.client.close();
        }
    }

    public void reconnect() {
        if (this.client != null) {
            this.client.connect();
        }
    }

    @Override
    public void send(String text) {

    }

    @Override
    public void send(byte[] data) {

    }

    @Override
    public void send(ByteBuffer bytes) {

    }
}
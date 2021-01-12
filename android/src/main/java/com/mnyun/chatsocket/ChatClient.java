package com.mnyun.chatsocket;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ChatClient {
    private final String TAG = ChatClient.class.getName();
    WebSocketClient client = null;
    public ChatClient() {
    }
    public void Connect(String host, String deviceId) {
        URI uri = URI.create(host + "?device_id=" + deviceId);
        this.client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "webSocket open");
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
            }
        };
        this.client.connect();
    }

    public void Reconnect() {
        if (this.client != null) {
            this.client.connect();
        }
    }
}
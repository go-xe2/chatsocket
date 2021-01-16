package com.mnyun.chatsocket;

import android.os.Handler;
import android.os.Looper;
import android.print.PrinterId;
import android.util.Log;

import com.mnyun.utils.StringUtils;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

public class ChatClient implements ChatSocket {
    private final String TAG = ChatClient.class.getName();
    private Heartbeat heartbeat;
    private WebSocketClient client = null;
    private String wsHost;
    private String deviceId;
    private final static String CHAT_PONG_MSG = "pong";
    private boolean isStop = false;
    private ChatClientHandler clientHandler;
    private boolean mIsConnected = false;

    public ChatClient() {
        super();
    }
    public ChatClient(String wsHost, String deviceId) {
        super();
        this.wsHost = wsHost;
        this.deviceId = deviceId;
    }

    /**
     * 绑定处理事件
     * @param handler
     */
    public void setHandler(ChatClientHandler handler) {
        this.clientHandler = handler;
    }
    /**
     * 开始发送心跳
     */
    private void startHeartbeat() {
        this.stopHeartbeat();
        this.heartbeat = new ChatSocketHeartbeat(this);
        this.heartbeat.start();
    }

    /**
     * 停止心跳包
     */
    private void stopHeartbeat() {
        if (this.heartbeat != null) {
            this.heartbeat.stop();
            this.heartbeat = null;
        }
    }

    /**
     * 连接webSocket服务
     * @param host
     * @param deviceId
     */
    public void connect(String host, String deviceId) {
        this.wsHost = host;
        this.deviceId = deviceId;
        if (StringUtils.isBlank(this.wsHost) || StringUtils.isBlank(this.deviceId)) {
            return;
        }
        if (this.isConnected()) {
            return;
        }
        URI uri = URI.create(host + "?device_id=" + deviceId);
        this.client = createSocketClient(uri);
        this.client.connect();
    }

    private WebSocketClient createSocketClient(URI uri) {
        return new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "成功连接IM服务:" + uri.toString());
                ChatClient.this.onOpenHandler(handshakedata);
            }
            @Override
            public void onMessage(String message) {
                ChatClient.this.onMessageHandler(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                ChatClient.this.onCloseHandler(code, reason, remote);
            }

            @Override
            public void onError(Exception ex) {
                ChatClient.this.onErrorHandler(ex);
            }
        };
    }

    private void onCloseHandler(int code, String reason, boolean remote) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "webSocket onClose:" + reason + remote);
        this.mIsConnected = false;
        this.socketCloseRetry();
        if (this.clientHandler != null) {
            this.clientHandler.onClose(code, reason, remote);
        }
    }

    private void onErrorHandler(Exception ex) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "webSocket onError:" + ex.getMessage());
        this.innerClose();
        if (this.clientHandler != null) {
            this.clientHandler.onError(ex);
        }
    }

    private void onOpenHandler(ServerHandshake handshake) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "webSocket open");
        this.mIsConnected = true;
        this.startHeartbeat();
        isStop = false;
        if (this.clientHandler != null) {
            this.clientHandler.onOpen(handshake);
        }
    }

    private void onSignInRespHandler(JSONObject msgData) {
        if (this.clientHandler != null) {
            this.clientHandler.onUserSignInResp(msgData);
        }
    }

    private void onSignOutRespHandler(JSONObject msgData) {
        if (this.clientHandler != null) {
            this.clientHandler.onUserSignOutResp(msgData);
        }
    }

    private void onSendMessageRespHandler(JSONObject msgData) {
        if (this.clientHandler != null) {
            this.clientHandler.onSendMessageResp(msgData);
        }
    }

    protected void onMessageHandler(String message) {
        if ("ping".equals(message)) {
            this.send(CHAT_PONG_MSG);
            return;
        }
        // 处理消息业务
        if (message == null || "".equals(message) || message.length() < 7) {
            return;
        }
        String pkInfo = message.substring(0, 7);
        String pkType = pkInfo.substring(0, 2);
        String pkCodeType = pkInfo.substring(2, 3);
        String pkVer = pkInfo.substring(3);
        if (!"1".equals(pkCodeType)) {
            return; // 不是json编码
        }
        JSONObject msgData;
        try {
            msgData = new JSONObject(message.substring(7));
        } catch (JSONException e) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "parse message json error:" + e.getMessage());
            return;
        }
        switch (pkType) {
            case "05":
                // 登录响应
                this.onSignInRespHandler(msgData);
                break;
            case "07":
                // 退出登录响应
                this.onSignOutRespHandler(msgData);
                break;
            case "0B":
                // 发送消息响应
                this.onSendMessageRespHandler(msgData);
                break;
            case "0C":
                // 收到消息响应
                if (this.clientHandler != null) {
                    this.clientHandler.onMessage(msgData);
                }
                break;
        }
    }
    private void socketCloseRetry() {
        if (this.isStop) {
            return;
        }
        this.stopHeartbeat();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    reconnect();
                } catch (InterruptedException e) {
                    Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, e.getMessage());
                }
            }
        }).start();
    }

    private void innerClose() {
        this.stopHeartbeat();
        if (this.client != null) {
            this.client.close();
            this.client = null;
        }
    }

    /**
     * 获取服务是否已连接
     * @return
     */
    public boolean isConnected() {
        return this.client != null && this.client.isOpen();
    }

    /**
     * 关闭服务
     */
    public void close() {
        this.isStop = true;
        this.innerClose();
    }

    /**
     * 重新连接服务
     */
    public void reconnect() {
        if (client == null) {
            if (StringUtils.isBlank(this.wsHost) ||StringUtils.isBlank(this.deviceId)) {
                return;
            }
            URI uri = URI.create(wsHost + "?device_id=" + deviceId);
            client = createSocketClient(uri);
            client.connect();
            return;
        }
        if (!client.isOpen()) {
            if (client.getReadyState().equals(WebSocket.READYSTATE.NOT_YET_CONNECTED)) {
                try {
                    client.connect();
                } catch (IllegalStateException e) {
                    Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "webSocket connect error:" + e.getMessage());
                }
            } else if (client.getReadyState().equals(WebSocket.READYSTATE.CLOSING) || client.getReadyState().equals(WebSocket.READYSTATE.CLOSED)) {
                URI uri = URI.create(wsHost + "?device_id=" + deviceId);
                client = createSocketClient(uri);
                this.client = createSocketClient(uri);
                this.client.connect();
            }
        }
    }

    /**
     * 用户登录请求
     * @param token
     * @param userId
     */
    public void userSignIn(String token, String userId) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("token", userId);
            obj.put("user_id", userId);
            String pk = "041EA01" + obj.toString();
            this.send(pk);
        } catch (JSONException e) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "userSignIn encode json error:" + e.getMessage());
        }
    }

    @Override
    public void send(String text) {
        if (this.client != null) {
            this.client.send(text);
        }
    }

    @Override
    public void send(byte[] data) {
        if (this.client != null) {
            this.client.send(data);
        }
    }

    @Override
    public void send(ByteBuffer bytes) {
        if (this.client != null) {
            this.client.send(bytes);
        }
    }

}
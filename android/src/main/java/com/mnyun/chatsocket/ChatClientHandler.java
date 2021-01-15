package com.mnyun.chatsocket;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public interface ChatClientHandler {
    /**
     * 用户登录返回响应消息
     * @param data
     */
    void onUserSignInResp(JSONObject data);

    /**
     * 用户退出登录响应消息
     * @param data
     */
    void onUserSignOutResp(JSONObject data);

    /**
     * socket发送消息响应消息
     * @param data
     */
    void onSendMessageResp(JSONObject data);

    /**
     * 收到消息
     * @param data
     */
    void onMessage(JSONObject data);
    void onOpen(ServerHandshake handshake);
    void onClose(int code, String reason, boolean remote);
    void onError(Exception ex);
}

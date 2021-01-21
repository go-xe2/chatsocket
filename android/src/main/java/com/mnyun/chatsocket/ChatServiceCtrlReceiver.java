package com.mnyun.chatsocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/**
 * chatService控制消息接收器
 */
public class ChatServiceCtrlReceiver extends BroadcastReceiver {
    private ChatService chatService;
    public ChatServiceCtrlReceiver() {
        super();
    }

    public ChatServiceCtrlReceiver(ChatService chatService) {
        super();
        this.chatService = chatService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "ChatServiceCtrlReceiver收到消息:" + intent.getAction());
        if (intent == null) {
            return;
        }
        String ctrlType = intent.getStringExtra(ChatSocketConstants.CTRL_CHAT_CTRL_TYPE);
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "收到chatService控制消息:" + ctrlType);
        if (TextUtils.isEmpty(ctrlType)) {
            return;
        }
        Bundle content = intent.getBundleExtra(ChatSocketConstants.CTRL_CHAT_CTRL_PARAM);
        if (this.chatService == null || this.chatService.getChatServiceSocket() == null || !this.chatService.getChatServiceSocket().isConnected()) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "未连接IM服务，用户不能登录.");
            return;
        }
        ChatServiceSocket socket = this.chatService.getChatServiceSocket();
        if (ChatSocketConstants.CTRL_CHAT_SIGN_IN.equals(ctrlType)) {
            if (content == null) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "userSignIn content is null.");
                return;
            }
            String token = content.getString("token");
            String userId = content.getString("userId");
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "用户登录IMC服务 token:" + token + ", userId:" + userId);
            socket.signIn(token, userId);
        } else if (ChatSocketConstants.CTRL_CHAT_SIGN_OUT.equals(ctrlType)) {
            if (content == null) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "userSignOut content is null.");
                return;
            }
            String token = content.getString("token");
            socket.signOut(token);
        }
    }
}

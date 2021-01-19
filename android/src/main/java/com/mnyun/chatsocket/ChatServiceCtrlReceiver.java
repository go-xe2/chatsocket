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
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String ctrlType = intent.getStringExtra(ChatSocketConstants.CTRL_CHAT_CTRL_TYPE);
        if (TextUtils.isEmpty(ctrlType)) {
            return;
        }
        Bundle content = intent.getBundleExtra(ChatSocketConstants.CTRL_CHAT_CTRL_PARAM);
        ChatServiceSocket chatServiceSocket = ChatServiceSocket.getInstance();
        if (!chatServiceSocket.isConnected()) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "未登录，用户不能登录.");
            return;
        }
        if (ChatSocketConstants.CTRL_CHAT_CTRL_TYPE.equals(ctrlType)) {
            if (content == null) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "userSignIn content is null.");
                return;
            }
            String token = content.getString("token");
            String userId = content.getString("userId");
            chatServiceSocket.signIn(token, userId);
        } else if (ChatSocketConstants.CTRL_CHAT_CTRL_PARAM.equals(ctrlType)) {
            if (content == null) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "userSignOut content is null.");
                return;
            }
            String token = content.getString("token");
            chatServiceSocket.signOut(token);
        }
    }
}

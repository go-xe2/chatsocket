package com.mnyun.chatsocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.ReactBridge;
import com.facebook.react.bridge.ReactContext;

/**
 * ChatService事件广播接收者
 */
public class ChatBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ChatSocketConstants.CST_BROADCAST_CHAT_ACTION)) {
            Bundle params = intent.getExtras();
            String event = params.getString("event");
            String data = params.getString("content");
        }
    }

    public void  broadcastToJs(Context context, String event, String data) {
        if (context instanceof ReactContext) {

        }
    }
}

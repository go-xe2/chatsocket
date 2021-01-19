package com.mnyun.chatsocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * chatService用户接受开机，亮屏，wifi状态变动等开启服务
 */
public class StartChatServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = "";
        if (intent != null) {
            actionName = intent.getAction();
        }
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "StartChatServiceReceiver.onReceive action:" + actionName);
        Intent serviceIntent = new Intent(context, ChatService.class);
        context.startService(serviceIntent);
    }
}

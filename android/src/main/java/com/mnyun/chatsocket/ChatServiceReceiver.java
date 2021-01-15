package com.mnyun.chatsocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * chatService用户接受开机，亮屏，wifi状态变动等开启服务
 */
public class ChatServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, ChatService.class);
        context.startService(serviceIntent);
    }
}

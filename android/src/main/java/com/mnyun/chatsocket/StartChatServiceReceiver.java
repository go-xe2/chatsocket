package com.mnyun.chatsocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

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
        Toast.makeText(context, "收到广播消息，准备启动服务", Toast.LENGTH_SHORT).show();
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "StartChatServiceReceiver.onReceive action:" + actionName);
//        Intent serviceIntent = new Intent(context, ChatService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "调用startForegroundService启动服务." + context.toString());
            context.startForegroundService(new Intent(context, ChatService.class));
        } else {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "调用startService启动服务.");
            context.startService(new Intent(context, ChatService.class));
        }
    }
}

package com.mnyun.chatsocket;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

public class ChatService extends Service {
    ChatServiceSocket chatServiceSocket;
    public ChatService() {
        super();
    }
    private final ChatServiceCtrlReceiver chatServiceCtrlReceiver = new ChatServiceCtrlReceiver();
    @Override
    public void onCreate() {
        super.onCreate();
        this.chatServiceSocket = ChatServiceSocket.getInstance();
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "chatService create.");
        IntentFilter filter = new IntentFilter();
        filter.addAction(chatServiceCtrlReceiver.getClass().getName());
        this.registerReceiver(chatServiceCtrlReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "chatService onStartCommand.");
        if(intent != null) {
            Bundle params = intent.getBundleExtra("params");
            if (params != null) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "====OnStartCommand params:" + params.toString());
            }
        }
        if (this.chatServiceSocket == null) {
            this.chatServiceSocket = ChatServiceSocket.getInstance();
        }
        this.startForeground(0,showRunNotification());
        if (TextUtils.isEmpty(this.chatServiceSocket.getDeviceId())) {
            this.chatServiceSocket.init(ChatManager.getInstance().getContext());
        }
        if (!this.chatServiceSocket.isConnected()) {
            this.chatServiceSocket.close();
        }
        this.chatServiceSocket.doConnect();
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification showRunNotification() {
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 创建一个Notification对象
        Notification.Builder builder = new Notification.Builder(this);
        // 设置打开该通知，该通知自动消失
        builder.setAutoCancel(true);
        // 设置通知的图标
        builder.setSmallIcon(R.drawable.redbox_top_border_background);
        // 设置通知内容的标题
        builder.setContentTitle("免农云");
        // 设置通知内容
        builder.setContentText("运行中...");
        //设置使用系统默认的声音、默认震动
        builder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE);
        //设置发送时间
        builder.setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
//        notificationManager.notify(notificationId,notif);
        return notification;
    }

    @Override
    public void onDestroy() {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "chatService onDestroy.");
        if (this.chatServiceSocket != null) {
            this.chatServiceSocket.close();
        }
        this.stopForeground(true);
        this.unregisterReceiver(chatServiceCtrlReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "chatService onBind.");
        return null;
    }
}

package com.mnyun.chatsocket;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mnyun.utils.ResourceUtil;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            this.startForeground(1, showRunNotification());
            this.setNotification("保持后台运行以支持接收消息");
        }
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
        if (TextUtils.isEmpty(this.chatServiceSocket.getDeviceId())) {
            this.chatServiceSocket.init(this.getApplicationContext());
        }
        if (!this.chatServiceSocket.isConnected()) {
            this.chatServiceSocket.close();
        }
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "服务启动中，chatServiceSocket准备链接");
        this.chatServiceSocket.doConnect();
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    private void setNotification(String text) {
        Intent intentClicked = new Intent(this, NotificationLaunchReceiver.class);
        intentClicked.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intentClicked, PendingIntent.FLAG_UPDATE_CURRENT);
        Context appContext = ChatManager.getInstance().getContext();
        Notification notification = null;
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int smallIcon = ResourceUtil.getMipmapResId(appContext, "ic_launcher");
        if (smallIcon == 0) {
            smallIcon = ResourceUtil.getDrawableResId(appContext, "ic_launcher");
        }
        if (smallIcon == 0) {
            smallIcon = android.R.drawable.ic_lock_idle_charging;
        }
        String appName = getString(ResourceUtil.getStringResId(appContext, "app_name"));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Uri mUri = Settings.System.DEFAULT_NOTIFICATION_URI;

            NotificationChannel mChannel = new NotificationChannel(ChatSocketConstants.NOTIFICATION_CHANNEL_ID, ChatSocketConstants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);

            mChannel.setDescription(appName);

            mChannel.setSound(mUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);

            notificationManager.createNotificationChannel(mChannel);

            notification = new Notification.Builder(this, ChatSocketConstants.NOTIFICATION_CHANNEL_ID)
                    .setChannelId(ChatSocketConstants.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(smallIcon)
                    .setContentTitle(appName)
                    .setContentText(text)
                    .setContentIntent(pi)
                    .build();
        } else {
            // 提升应用权限
            notification = new Notification.Builder(this)
                    .setSmallIcon(smallIcon)
                    .setContentTitle(appName)
                    .setContentText(text)
                    .setContentIntent(pi)
                    .build();
        }
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(10000, notification);
    }

    @Override
    public void onDestroy() {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "chatService onDestroy.");
        if (this.chatServiceSocket != null) {
            this.chatServiceSocket.close();
        }
        this.unregisterReceiver(chatServiceCtrlReceiver);
        ChatManager.startChatService(this.getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.stopForeground(true);
        }super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "chatService onBind.");
        return null;
    }
}

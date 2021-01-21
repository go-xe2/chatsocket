package com.mnyun.chatsocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.mnyun.utils.BadgeUtils;
import com.mnyun.utils.ResourceUtil;
import com.mnyun.utils.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowChatNotificationReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "ShowChatNotificationReceiver onReceive");
        if (intent == null) {
            return;
        }
        String szData = intent.getStringExtra("content");
        if (TextUtils.isEmpty(szData)) {
            return;
        }
        int notificationId = intent.getIntExtra("notificationId", 0);
        int requestCode = intent.getIntExtra("requestCode", 0);
        int messageCount = intent.getIntExtra("messageCount", 0);
        ChatManager manager = ChatManager.getInstance();
        if (SystemUtils.isAppAlive(manager.getContext(), manager.getPackageName()) == 1) {
            // app界面在线
            this.sendEventToRN(context, szData);
        } else {
            this.showNotification(context, notificationId, requestCode, messageCount, szData);
            BadgeUtils.setBadgeNumber(messageCount);
        }
    }

    /**
     * 向react发送事件
     * @param content
     */
    protected void sendEventToRN(Context context, String content) {
        Intent intent = new Intent();
        intent.setAction(ChatSocketConstants.CST_BROADCAST_CHAT_ACTION);
        intent.putExtra("event", ChatSocketConstants.CST_ON_CHAT_MESSAGE_EVENT);
        intent.putExtra("content", content);
        intent.setPackage(ChatManager.getInstance().getPackageName());
        intent.setComponent(new ComponentName(ChatManager.getInstance().getPackageName(), RNChatSocketModule.ChatBroadcastReceiver.class.getName()));
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "广播消息: event" + ChatSocketConstants.CST_ON_CHAT_MESSAGE_EVENT + ", data:"  + content);
        context.sendBroadcast(intent);
    }

    /**
     * 显示通知消息
     * @param content
     */
    private void showNotification(Context context, int notificationId, int requestCode, int messageCount, String content) {
        //设置点击通知栏的动作为启动另外一个广播
        JSONObject data  = null;
        int senderType = -1;
        String senderName = "";
        String msgSummary = ChatManager.getInstance().getAppTitle();
        String msgContent = "";
        int msgType = 0;
        try {
            data = new JSONObject(content);
            msgContent = data.getString("content");
            msgType = data.getInt("msg_type");
            JSONObject sender = data.getJSONObject("sender");
            senderType = sender.getInt("sender_type");
            if (senderType == 1 || senderType == 3) {
                msgSummary = "[通知]";
            } else {
                senderName = sender.getString("nick_name");
                msgSummary = "[" + senderName + "]";
            }
        } catch (JSONException e) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, e.getMessage());
        }
        if (data == null) {
            return;
        }

        Intent intentClicked = new Intent(context, NotificationLaunchReceiver.class);
        intentClicked.putExtra(ChatSocketConstants.NOTIFICATION_ID_PARAM, notificationId);
        intentClicked.setAction(ChatSocketConstants.NOTIFICATION_EVENT_CLICKED);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, requestCode, intentClicked, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentCancelled = new Intent(context, NotificationLaunchReceiver.class);
        intentCancelled.putExtra(ChatSocketConstants.NOTIFICATION_ID_PARAM, notificationId);
        intentCancelled.setAction(ChatSocketConstants.NOTIFICATION_EVENT_CANCELLED);

        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, requestCode, intentCancelled, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        ChatManager chatManager = ChatManager.getInstance();
        String appName = context.getString(ResourceUtil.getStringResId(context, "app_name"));
        int smallIcon = ResourceUtil.getMipmapResId(context, "ic_launcher");
        if (smallIcon == 0) {
            smallIcon = ResourceUtil.getDrawableResId(context, "ic_launcher");
        }
        if (smallIcon == 0) {
            smallIcon = R.drawable.redbox_top_border_background;
        }
        if (TextUtils.isEmpty(appName)) {
            appName = chatManager.getAppTitle();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建一个Notification对象
            Notification.Builder builder = new Notification.Builder(context);

            // 设置打开该通知，该通知自动消失
            builder.setAutoCancel(true);
            // 设置通知的图标
            builder.setSmallIcon(smallIcon);
            // 设置通知内容的标题
            builder.setContentTitle(appName);
            builder.setContentText(msgSummary + ":" + msgContent);
            builder.setTicker(msgContent);
            // 设置通知内容
            builder.setContentIntent(clickPendingIntent);
            builder.setDeleteIntent(cancelPendingIntent);

            //设置使用系统默认的声音、默认震动
            builder.setDefaults(Notification.DEFAULT_SOUND
                    | Notification.DEFAULT_VIBRATE);
            builder.setSmallIcon(android.R.drawable.ic_lock_idle_charging);
            //设置发送时间
            builder.setWhen(System.currentTimeMillis());

            // 8.0 需要创建渠道
            NotificationChannel channel = new NotificationChannel(ChatSocketConstants.NOTIFICATION_CHANNEL_ID, ChatSocketConstants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.RED); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            channel.setDescription(appName);
            notifyManager.createNotificationChannel(channel);
            builder.setChannelId(ChatSocketConstants.NOTIFICATION_CHANNEL_ID);

            Notification notification = builder.build();
            notifyManager.notify(notificationId,notification);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(appName)
                    .setSmallIcon(smallIcon)
                    .setContentText(msgSummary + ":" + msgContent)
                    .setTicker(msgContent)
                    .setContentIntent(clickPendingIntent)
                    .setDeleteIntent(cancelPendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND
                            | Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_charging);

            Notification notification = builder.build();
            notifyManager.notify(notificationId, notification);
        }
    }
}

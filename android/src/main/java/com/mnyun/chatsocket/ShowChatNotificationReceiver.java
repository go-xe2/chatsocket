package com.mnyun.chatsocket;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ShowChatNotificationReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "ShowChatNotificationReceiver onReceive");
        //设置点击通知栏的动作为启动另外一个广播
        Class<BroadcastReceiver> notificationReceiver = ChatManager.getInstance().getNotificationReceiverClass();
        if (notificationReceiver == null) {
            return;
        }
        int requestCode = 0;
        int notificationId = 0;
        if (intent != null) {
            requestCode = intent.getIntExtra("requestCode", 0);
            notificationId = intent.getIntExtra("notificationId", 0);
        }
        Intent broadcastIntent = new Intent(context, notificationReceiver);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建一个Notification对象
            Notification.Builder notification = new Notification.Builder(context);
            // 设置打开该通知，该通知自动消失
            notification.setAutoCancel(true);
            // 设置通知的图标
            notification.setSmallIcon(R.drawable.redbox_top_border_background);
            // 设置通知内容的标题
            notification.setContentTitle("免农云");
            // 设置通知内容
            notification.setContentIntent(pendingIntent);
            //设置使用系统默认的声音、默认震动
            notification.setDefaults(Notification.DEFAULT_SOUND
                    | Notification.DEFAULT_VIBRATE);
            //设置发送时间
            notification.setWhen(System.currentTimeMillis());
            // 创建一个启动其他Activity的Intent
//        Intent intent = new Intent(this
//                , DetailActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(
//                NotificationActivity.this, 0, intent, 0);
            //设置通知栏点击跳转
//        notification.setContentIntent(pi);
            //发送通知
            manager.notify(notificationId,notification.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("免农云")
                    .setTicker("这是通知的ticker")
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND
                            | Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_charging);
            Log.i("repeat", "showNotification");
            manager.notify(notificationId, builder.build());
        }
    }
}

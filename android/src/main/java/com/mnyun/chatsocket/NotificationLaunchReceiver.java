package com.mnyun.chatsocket;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.mnyun.utils.BadgeUtils;
import com.mnyun.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知消息点击消息接收器，用于打开app
 */
public class NotificationLaunchReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int notificationId = intent.getIntExtra(ChatSocketConstants.NOTIFICATION_ID_PARAM, -1);
        if (notificationId != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId);
        }
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "NotificationLaunchReceiver action:" + action + ", notificationId:" + notificationId);
        if (ChatSocketConstants.NOTIFICATION_EVENT_CLICKED.equals(action)) {
            //处理点击事件
            startApp(context, intent);
            BadgeUtils.setBadgeNumber(0);
            return;
        }
        if (ChatSocketConstants.NOTIFICATION_EVENT_CANCELLED.equals(action)) {
            //处理滑动清除和点击删除事件
            return;
        }
    }

    protected void startApp(Context context, Intent intent) {
        ChatManager manager = ChatManager.getInstance();
        String mPackageName = manager.getPackageName();
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "NotificationLaunchReceiver.onReceive:");
        if (TextUtils.isEmpty(mPackageName)) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "未设备app的包名称");
            return;
        }
        //判断app进程是否存活
        int aLive = SystemUtils.isAppAlive(context, mPackageName);
        if (aLive > 0) {
            //如果存活的话，就直接启动DetailActivity，但要考虑一种情况，就是app的进程虽然仍然在
            //但Task栈已经空了，比如用户点击Back键退出应用，但进程还没有被系统回收，如果直接启动
            //DetailActivity,再按Back键就不会返回MainActivity了。所以在启动
            //DetailActivity前，要先启动MainActivity。
            Log.i(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "the app process is alive， live status:" + aLive);
            List intents = new ArrayList<Intent>();

            String launchActivityClassName = manager.getLaunchActivityName();
            String mainActivityClassName = manager.getMainActivityName();
            if (!TextUtils.isEmpty(launchActivityClassName) && !launchActivityClassName.equals(mainActivityClassName)) {
                try {
                    Class launchClass = Class.forName(launchActivityClassName);
                    Intent launchIntent = new Intent(context, launchClass);
                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intents.add(launchIntent);
                } catch (ClassNotFoundException e) {
                    Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "创建LaunchActivity出错:" + e.getMessage());
                }
            }
            try {
                Class mainActivity = Class.forName(manager.getMainActivityName());
                Intent mainIntent = new Intent(context, mainActivity);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intents.add(mainIntent);
            } catch (ClassNotFoundException e) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "创建mainActivity出错:" + e.getMessage());
            }
            Intent[] arrIndent = new Intent[intents.size()];
            intents.toArray(arrIndent);
            if (aLive == 2) {
                // 切换到前台
                SystemUtils.setTopApp(context);
            }
            context.startActivities(arrIndent);
        } else {
            //如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
            //SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入             //参数跳转到DetailActivity中去了
            Log.i(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "the app process is dead");
            Intent launchIntent = context.getPackageManager().
                    getLaunchIntentForPackage(mPackageName);
            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle args = new Bundle();
            if (intent != null) {
                args = intent.getExtras();
            }
            if (args != null) {
                launchIntent.putExtras(args);
            }
            context.startActivity(launchIntent);
        }
    }
}

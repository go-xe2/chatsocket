package com.mnyun.chatsocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mnyun.utils.SystemUtils;

public class ChatNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ChatManager.ChatOptions options = ChatManager.getInstance().getOptions();

        String appPackageName = "";
        if (options != null) {
            appPackageName = options.getAppPackageName();
        } else {
            appPackageName = context.getPackageName();
        }
        //判断app进程是否存活
        if (SystemUtils.isAppAlive(context, appPackageName) > 0) {
            //如果存活的话，就直接启动DetailActivity，但要考虑一种情况，就是app的进程虽然仍然在
            //但Task栈已经空了，比如用户点击Back键退出应用，但进程还没有被系统回收，如果直接启动
            //DetailActivity,再按Back键就不会返回MainActivity了。所以在启动
            //DetailActivity前，要先启动MainActivity。
            Log.i(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "the app process is alive");
            Intent[] intents = ChatManager.getInstance().getNotificationStartIntents();
            if (intents != null && intents.length > 0) {
                context.startActivities(intents);
            }
        } else {
            //如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
            //SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入             //参数跳转到DetailActivity中去了
            Log.i(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "the app process is dead");
            Intent launchIntent = context.getPackageManager().
                    getLaunchIntentForPackage(appPackageName);
            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
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

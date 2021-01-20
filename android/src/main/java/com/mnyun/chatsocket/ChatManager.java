package com.mnyun.chatsocket;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mnyun.utils.BadgeUtils;
import com.mnyun.utils.ChatSocketException;
import com.mnyun.utils.ReactUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatManager {
    private Context appContext;
    private String packageName;
    private String launchActivityName;
    private String mainActivityName;
    private String appTitle;
    private Intent chatServiceIndent;
    private final static class chatManagerInstance {
        private final static ChatManager instance = new ChatManager();
    }

    public static ChatManager getInstance() {
        return chatManagerInstance.instance;
    }

    public void init(Context appContext) {
        this.appContext = appContext;

    }
//    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init(Context appContext, Options options) {
        this.init(appContext);
        if (options != null) {
            packageName = options.getPackageName();
            launchActivityName = options.getLaunchActivityName();
            mainActivityName = options.getMainActivityName();
            appTitle = options.getAppTitle();
        }
        BadgeUtils.init(this.packageName, this.mainActivityName, this.appContext);
    }

    /**
     * 启动chatService服务
     * @param context
     */
    public static void startChatService(Context context) {
        // 发送启动chatService广播
        Intent startServiceBroadcastIndent = new Intent(ChatSocketConstants.CST_START_CHAT_SERVICE_ACTION);
        startServiceBroadcastIndent.setComponent(new ComponentName(ChatManager.getInstance().getPackageName(), StartChatServiceReceiver.class.getName()));
        startServiceBroadcastIndent.setPackage(ChatManager.getInstance().getPackageName());
//        startServiceBroadcastIndent.setAction(ChatSocketConstants.CST_START_CHAT_SERVICE_ACTION);
        context.sendBroadcast(startServiceBroadcastIndent);
    }

    /**
     * 获取app上下文
     * @return
     */
    public Context getContext() {
        return this.appContext;
    }

    /**
     * 获取包名
     * @return
     */
    public String getPackageName() {
        return this.packageName;
    }

    /**
     * 获取app应用标题
     * @return
     */
    public String getAppTitle() {
        return this.appTitle;
    }

    /**
     * 获取主场景页完成名称
     * @return
     */
    public String getMainActivityName() {
        return this.mainActivityName;
    }

    /**
     * 获取起动页完整名称
     * @return
     */
    public String getLaunchActivityName() {
        return this.launchActivityName;
    }

    public ReactContext getReactAppContext() {
        if (this.appContext == null) {
            return null;
        }
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "application Context:" + this.appContext.getClass().getName());
        if (!(this.appContext instanceof ReactApplication)) {
            return null;
        }
        ReactApplication app = (ReactApplication)this.appContext;
        ReactInstanceManager instanceManager = app.getReactNativeHost().getReactInstanceManager();
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "instanceManager:" + instanceManager.toString());
        if (instanceManager == null) {
            return null;
        }

        if (instanceManager.getCurrentReactContext() == null) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "currentReactContext is null.");
            instanceManager.createReactContextInBackground();
        }
        return instanceManager.getCurrentReactContext();
    }

    public void emitEvent(Context context, Intent intent, String eventName, Object data) {
        if (appContext == null) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "context is null, cannot emit event.");
            return;
        }
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, this.appContext.toString());
        ReactContext reactContext = this.getReactAppContext();
        if (reactContext == null) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "reactContext is null, cannot emit event.");
            return;
        }
        DeviceEventManagerModule.RCTDeviceEventEmitter emitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        if (emitter != null) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "emit event:" + eventName + ", data:" + data.toString());
            emitter.emit(eventName, data);
        }
    }


    public static class Options {
        private String appTitle;
        private String packageName;
        private String launchActivityName;
        private String mainActivityName;

        public String getAppTitle() {
            return appTitle;
        }

        public void setAppTitle(String appTitle) {
            this.appTitle = appTitle;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getLaunchActivityName() {
            return launchActivityName;
        }

        public void setLaunchActivityName(String launchActivityName) {
            this.launchActivityName = launchActivityName;
        }

        public String getMainActivityName() {
            return mainActivityName;
        }

        public void setMainActivityName(String mainActivityName) {
            this.mainActivityName = mainActivityName;
        }

    }
}

package com.mnyun.chatsocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mnyun.utils.ChatSocketException;
import com.mnyun.utils.ReactUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatManager {
    private static Context appContext;
    private static ChatManagerInitHandler initHandler;
    private final static class chatManagerInstance {
        private final static ChatManager instance = new ChatManager();
    }
    public static ChatManager getInstance() {
        return chatManagerInstance.instance;
    }
    public void init(Context appContext) {
        this.appContext = appContext;
    }
    public void init(Context appContext, ChatManagerInitHandler handler) {
        this.appContext = appContext;
        this.initHandler = handler;
    }
    public ReactContext getReactAppContext() {
        if (appContext == null) {
            return null;
        }
        if (!(appContext instanceof ReactApplication)) {
            return null;
        }
        ReactApplication app = (ReactApplication)appContext;
        ReactInstanceManager instanceManager = app.getReactNativeHost().getReactInstanceManager();
        if (instanceManager == null) {
            return null;
        }
        return instanceManager.getCurrentReactContext();
    }

    public void emitEvent(Context context, Intent intent, String eventName, Object data) {
        if (appContext == null) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "context is null, cannot emit event.");
            if (this.initHandler != null) {
                this.initHandler.startApp(context, intent);
            }
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
            emitter.emit(eventName, data);
        }
    }

    public interface ChatManagerInitHandler {
        // 启动app
       void startApp(Context context, Intent intent);
    }
}

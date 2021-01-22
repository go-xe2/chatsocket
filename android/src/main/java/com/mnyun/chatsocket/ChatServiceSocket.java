package com.mnyun.chatsocket;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.mnyun.imServerClient.IMServerCallback;
import com.mnyun.imServerClient.IMServerClient;
import com.mnyun.imServerClient.IMServerResult;
import com.mnyun.utils.StringUtils;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

public class ChatServiceSocket implements ChatClientHandler {
    private Context appContext;
    private ChatClient chatClient;
    private String deviceId; // 设备id
    private String wsHost; // webSocket 地址
    private String wsHttpUrl; // im服务的http地址
    private int notificationId = 0;
    private int messageCount = 0;
    private int requestCode = 0;

    private final static class ChatServiceSocketInstance {
        private final static ChatServiceSocket instance = new ChatServiceSocket();
    }

    /**
     *  获取唯一实例
     * @return
     */
    public static ChatServiceSocket getInstance() {
        return ChatServiceSocketInstance.instance;
    }

    public ChatServiceSocket() {
        super();
        this.chatClient = new ChatClient();
    }

    /**
     * 初始化连接
     */
    public void init(Context appContext) {
        this.appContext = appContext;
        if (this.chatClient == null) {
            this.chatClient = new ChatClient();
        }
        SettingManager setting = new SettingManager(this.appContext);
        String wsHost = setting.getIMHost();
        String wsHttpUrl = setting.getIMHttpUrl();
        if (TextUtils.isEmpty(wsHost) || TextUtils.isEmpty(wsHttpUrl)) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "未配置IMHost及imHttpUrl参数");
            return;
        }
        this.deviceId = this.getOrRegisterDeviceId(setting);
        this.wsHost = wsHost;
        this.wsHttpUrl = wsHttpUrl;
        if (TextUtils.isEmpty(this.deviceId)) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "deviceId为空，未连接IM服务");
            return;
        }
        this.doConnect();
    }

    /**
     * 获取设备的id
     * @return
     */
    public String getDeviceId() {
        return this.deviceId;
    }

    /**
     *  获取或自动注册设备id
     */
    protected String getOrRegisterDeviceId(final SettingManager setting) {
        final String deviceId = setting.getDeviceID();
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        DeviceInfo info;
        try {
            info = setting.getDeviceInfo();
            if (info == null || TextUtils.isEmpty(info.getUuid())) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "未配置deviceInfo");
                return "";
            }
        } catch (JSONException e) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "读取deviceInfo出错:" + e.getMessage());
            return "";
        }
        IMServerClient client = new IMServerClient(appContext);
        String appKey = setting.getAppKey();
        String appSecret = setting.getAppSecret();
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "应用配置appKey:" + appKey + ", appSecret:" + appSecret);
        if (TextUtils.isEmpty(appKey) || TextUtils.isEmpty(appSecret)) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "未设备appKey,appSecret参数");
            return "";
        }
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] szResultDeviceId = new String[1];
        client.regDevice(appKey, appSecret,info, new IMServerCallback<String>() {
            @Override
            public void onResult(IMServerResult<String> result) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "注册设备返回:" + result.getContent());
                latch.countDown();
                if (result.isError()) {
                    Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "注册设备出错:" + result.getMsg());
                    return;
                }
                szResultDeviceId[0] = result.getContent();
                setting.saveDeviceID(szResultDeviceId[0]);
            }
        });
        try {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "发送注册设备请求完成，等待返回.");
            latch.await();
        } catch (InterruptedException e) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "等待注册返回出错:" +  e.getMessage());
        }
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "注册设备返回:" + szResultDeviceId[0]);
        return szResultDeviceId[0];
    }

    protected void doConnect() {
        if (this.chatClient.isConnected()) {
            this.chatClient.close();
        }
        this.chatClient.setHandler(this);
        this.chatClient.connect(this.wsHost, this.deviceId);
    }

    /**
     * 关闭链接
     */
    public void close() {
        if (this.chatClient != null) {
            this.chatClient.close();
        }
    }

    /**
     * 重新打开链接
     */
    public void reconnect() {
        if (this.chatClient != null) {
            this.chatClient.reconnect();
        }
    }

    /**
     * 是否已打开链接
     * @return
     */
    public boolean isConnected() {
        if (this.chatClient != null) {
            return this.chatClient.isConnected();
        }
        return false;
    }

    /**
     * 用户登录,与连接绑定
     * @param token
     * @param userId
     */
    public void signIn(String token, String userId) {
        if (!this.isConnected()) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "未打开连接");
            return;
        }
        this.chatClient.userSignIn(token, userId);
        SettingManager setting = new SettingManager(this.appContext);
        setting.saveUserID(userId);
        setting.saveUserToken(token);
    }

    /**
     * 用户退出登录，与连接解除绑定关系
     * @param token
     */
    public void signOut(String token) {
        if (!this.isConnected()) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "未打开链接");
            return;
        }
        this.chatClient.userSignOut(token);
    }

    private void broadcastMessage(String event, JSONObject data) {
        Intent intent = new Intent();
        intent.setAction(ChatSocketConstants.CST_BROADCAST_CHAT_ACTION);
        intent.putExtra("event", event);
        intent.putExtra("content", data.toString());
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "广播消息: event" + event + ", data:"  + data.toString());
        appContext.sendBroadcast(intent);
    }

    @Override
    public void onUserSignInResp(JSONObject data) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onUserSignInResp:" + data.toString());
        this.broadcastMessage(ChatSocketConstants.CST_ON_CHAT_SIGN_IN_RESP_EVENT, data);

    }

    @Override
    public void onUserSignOutResp(JSONObject data) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onUserSignOutResp:" + data.toString());
        this.broadcastMessage(ChatSocketConstants.CST_ON_CHAT_SIGN_OUT_RESP_EVENT, data);
    }

    @Override
    public void onSendMessageResp(JSONObject data) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onSendMessageResp:" + data.toString());
        this.broadcastMessage(ChatSocketConstants.CST_ON_CHAT_SEND_MESSAGE_RESP_EVENT, data);
    }

    @Override
    public void onMessage(JSONObject data) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onMessage:" + data.toString());
        Intent intent = new Intent(appContext, ShowChatNotificationReceiver.class);
        intent.putExtra("content", data.toString());
        intent.putExtra("notificationId", this.notificationId++);
        intent.putExtra("requestCode", this.requestCode++);
        intent.putExtra("messageCount", this.messageCount++);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, e.getMessage());
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onOpen:" + handshake.toString());
        JSONObject obj = new JSONObject();
        try {
            obj.put("status", handshake.getHttpStatus());
            obj.put("msg", handshake.getHttpStatusMessage());
            this.broadcastMessage(ChatSocketConstants.CST_ON_CHAT_OPEN_EVENT, obj);
            SettingManager setting = new SettingManager(this.appContext);
            final String userToken = setting.getUserToken();
            final String userId = setting.getUserID();
            if (!TextUtils.isEmpty(userToken) && !TextUtils.isEmpty(userId)) {
                // 打开链接后，如果之前用户已经登录，自动登录
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        signIn(userToken, userId);
                    }
                });
            }
        } catch (JSONException ex) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onOpen error:" + ex.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "socket onClose: code:" + code + ", reason:" + reason+ ", remote:" + remote);
        JSONObject obj = new JSONObject();
        try {
            obj.put("code", code);
            obj.put("reason", reason);
            obj.put("remote", remote);
            this.broadcastMessage(ChatSocketConstants.CST_ON_CHAT_CLOSE_EVENT, obj);
        } catch (JSONException e) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onClose error:" + e.getMessage());
        }
    }

    @Override
    public void onError(Exception ex) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "socket onError:" + ex.getMessage());
        JSONObject obj = new JSONObject();
        try {
            obj.put("error", true);
            obj.put("msg", ex.getMessage());
            this.broadcastMessage(ChatSocketConstants.CST_ON_CHAT_ERROR_EVENT, obj);
        } catch (JSONException e) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onError error:" + e.getMessage());
        }
    }
}

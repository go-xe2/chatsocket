package com.mnyun.chatsocket;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.JsonIOException;
import com.mnyun.imServerClient.IMServerCallback;
import com.mnyun.imServerClient.IMServerClient;
import com.mnyun.imServerClient.IMServerResult;
import com.mnyun.utils.StringUtils;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatService extends Service implements ChatClientHandler{
    private ChatClient chatClient;
    private String deviceId;
    private String wsHost;
    private String wsHttpUrl;
    private String userToken;
    private String userId;
    private String appKey;
    private String appSecret;
    private int notificationId = 1000;
    public ChatService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        chatClient = new ChatClient();
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "chatService create.");
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
        this.initSocket();
        this.startForeground(0, showNotification("免农云", ""));
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification showNotification(String title, String msg) {
        //设置消息内容和标题
        //新建通知管理器
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 创建一个Notification对象
        Notification.Builder notification = new Notification.Builder(this);
        // 设置打开该通知，该通知自动消失
        notification.setAutoCancel(true);
        // 设置通知的图标
        notification.setSmallIcon(R.drawable.redbox_top_border_background);
        // 设置通知内容的标题
        notification.setContentTitle(title);
        // 设置通知内容
        notification.setContentText(msg);
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
        Notification notif = notification.build();
        notificationManager.notify(notificationId,notif);
        notificationId++;
        return notif;
    }

    protected void initSocket() {
        SettingManager setting = new SettingManager(this);
        this.deviceId = setting.getDeviceID();
        this.wsHost = setting.getIMHost();
        this.wsHttpUrl = setting.getIMHttpUrl();
        this.userId = setting.getUserID();
        this.userToken = setting.getUserToken();
        this.appKey = setting.getAppKey();
        this.appSecret = setting.getAppSecret();

        if (StringUtils.isBlank(wsHost) || StringUtils.isBlank(this.wsHttpUrl)) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "未配置IMHost及imHttpUrl参数");
            return;
        }
        if (StringUtils.isBlank(this.deviceId)) {
            // 注册设备
            this.registerDevice(setting);
            return;
        }
        this.doSocketConnect();
    }

    protected void doSocketConnect() {
        if (this.chatClient.isConnected()) {
            this.chatClient.close();
        }
        this.chatClient.setHandler(this);
        this.chatClient.connect(this.wsHost, this.deviceId);
    }

    protected void doUserSignIn() {
        if (StringUtils.isBlank(this.userToken) || StringUtils.isBlank(this.userId)) {
            return;
        }
        if (this.chatClient.isConnected()) {
            return;
        }
        this.chatClient.userSignIn(this.userToken, this.userId);
        SettingManager setting = new SettingManager(this);
        setting.saveUserToken(this.userToken);
        setting.saveUserID(this.userId);
    }

    protected boolean registerDevice(final SettingManager setting) {
        try {
            DeviceInfo info = setting.getDeviceInfo();
            if (info == null || "".equals(info.getUuid())) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "未配置deviceInfo");
                return false;
            }
            IMServerClient client = new IMServerClient(this);
            client.regDevice(this.appKey, this.appSecret,info, new IMServerCallback<String>() {
                @Override
                public void onResult(IMServerResult<String> result) {
                    if (result.isError()) {
                        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "注册设备出错:" + result.getMsg());
                        return;
                    }
                    ChatService.this.deviceId =  result.getContent();
                    setting.saveDeviceID(deviceId);
                    ChatService.this.doSocketConnect();
                }
            });
            return true;
        } catch (JSONException e) {
            Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "registerDevice error:" + e.getMessage());
        }
        return false;
    }

    @Override
    public void onDestroy() {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "chatService onDestroy.");
        this.stopForeground(true);
        this.chatClient.close();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "chatService onBind.");
        return null;
    }

    protected void socketConnect() {
        this.doSocketConnect();
    }

    protected void userSignIn(String token, String userId) {
        if (StringUtils.isBlank(token) || StringUtils.isBlank(userId)) {
            return;
        }
        this.userToken = token;
        this.userId = userId;
        this.doUserSignIn();
    }

    protected void socketClose() {
        this.chatClient.close();
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
        this.broadcastMessage(ChatSocketConstants.CST_ON_CHAT_MESSAGE_EVENT, data);
        try {
            JSONObject sender = data.getJSONObject("sender");
            int senderType = sender.getInt("sender_type");
            String title = "免农云";
            String senderName = "";
            if (senderType == 2) {
                // 通知消息
                senderName = sender.getString("nick_name");
                title = "来自[" + senderName + "]消息";
            }
            int msgType = data.getInt("msg_type");
            String msgContent = data.getString("content");
            this.showNotification(title, msgContent);
        } catch (JSONException e) {
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

    private void broadcastMessage(String event, JSONObject data) {
        Intent intent = new Intent();
        intent.setAction(ChatSocketConstants.CST_BROADCAST_CHAT_ACTION);
        intent.putExtra("event", event);
        intent.putExtra("content", data.toString());
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "广播消息: event" + event + ", data:"  + data.toString());
        sendBroadcast(intent);
    }
}

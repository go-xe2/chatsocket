
package com.mnyun.chatsocket;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mnyun.imServerClient.ChatMessageType;
import com.mnyun.imServerClient.ContactPage;
import com.mnyun.imServerClient.IMServerCallback;
import com.mnyun.imServerClient.IMServerClient;
import com.mnyun.imServerClient.IMServerResult;
import com.mnyun.imServerClient.MessageStatus;
import com.mnyun.imServerClient.MsgContent;
import com.mnyun.imServerClient.MsgContentList;
import com.mnyun.imServerClient.ReceiverType;
import com.mnyun.imServerClient.SendResContent;
import com.mnyun.imServerClient.SenderType;
import com.mnyun.imServerClient.UserInfo;
import com.mnyun.imServerClient.UserSex;
import com.mnyun.utils.ChatSocketException;
import com.mnyun.utils.ReactUtils;
import com.mnyun.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RNChatSocketModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
  private final ReactApplicationContext reactContext;
  private final SettingManager settingManager;
  private final IMServerClient imClient;
  private Intent chatServiceIndent;
  @RequiresApi(api = Build.VERSION_CODES.O)
  public RNChatSocketModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.settingManager = new SettingManager(reactContext);
    this.imClient = new IMServerClient(reactContext);
    startChatService();
    // 注册接收者
    reactContext.registerReceiver(chatReceiver, new IntentFilter(ChatSocketConstants.CST_BROADCAST_CHAT_ACTION));
  }

  /**
   * 启动即时消息服务客户端
   */
  @RequiresApi(api = Build.VERSION_CODES.O)
  private void startChatService() {
    chatServiceIndent = createChatServiceIndent();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      reactContext.startForegroundService(chatServiceIndent);
    } else {
      reactContext.startService(chatServiceIndent);
    }
  }

  /**
   * 停止chatService服务
   */
  private void stopChatService() {
    if (chatServiceIndent != null) {
      reactContext.stopService(chatServiceIndent);
    }
  }

  /**
   * 创建chatService服务的Indent
   * @return
   */
  private Intent createChatServiceIndent() {
    Intent chatServiceIndent = new Intent(reactContext, ChatService.class);
    Bundle params = new Bundle();
    params.putString(ChatSocketConstants.CST_CHAT_SERVICE_WS_HOST_PARAM, this.settingManager.getIMHost());
    params.putString(ChatSocketConstants.CST_CHAT_SERVICE_HTTP_URL_PARAM, this.settingManager.getIMHttpUrl());
    params.putString(ChatSocketConstants.CST_CHAT_SERVICE_USER_ID_PARAM, this.settingManager.getUserID());
    params.putString(ChatSocketConstants.CST_CHAT_SERVICE_USER_TOKEN_PARAM, this.settingManager.getUserToken());
    try {
      DeviceInfo deviceInfo = this.settingManager.getDeviceInfo();
      if (deviceInfo != null) {
        Bundle deviceParams = new Bundle();
        deviceParams.putString("uuid", deviceInfo.getUuid());
        deviceParams.putString("brand", deviceInfo.getBrand());
        deviceParams.putString("mode", deviceInfo.getMode());
        deviceParams.putString("sysVersion", deviceInfo.getSdkVersion());
        deviceParams.putString("sdkVersion", deviceInfo.getSdkVersion());
        params.putBundle(ChatSocketConstants.CST_CHAT_SERVICE_DEVICE_INFO_PARAM, deviceParams);
      }
    } catch (JSONException e) {
      Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "从配置中读取deviceInfo出错:" + e.getMessage());
    }
    chatServiceIndent.putExtra("params", params);
    return chatServiceIndent;
  }

  @Override
  public String getName() {
    return "RNChatSocket";
  }

  /**
   * 获取chatSocket版本
   * @return
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getVersion() {
    return "v1.0.0";
  }

  /**
   * 获取im的webSocket地址
   * @return
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getIMHost() {
    return this.settingManager.getIMHost();
  }

  /**
   * 获取im的http服务地址
   * @return
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getIMHttpUrl() {
    return this.settingManager.getIMHttpUrl();
  }

  /**
   * 获取im配置的AppKey
   * @return
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getAppKey() {
   return this.settingManager.getAppKey();
  }

  /**
   * 获取im配置的AppSecret
   * @return
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getAppSecret() {
    return this.settingManager.getAppSecret();
  }

  /**
   * 获取本地设备id
   * @return
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getDeviceID() {
    return this.settingManager.getDeviceID();
  }

  /**
   * 获取本地用户token
   * @return
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getUserToken() {
    return this.settingManager.getUserToken();
  }

  /**
   * 获取本地用户id
   * @return
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getUserID() {
    return this.settingManager.getUserID();
  }

  /**
   * 获取本地用户昵称
   * @return
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getUserNickName() {
    return this.settingManager.getUserNickName();
  }

  /**
   * 获取本地用户头像
   * @return
   */
  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getUserAvatarUrl() {
    return this.settingManager.getUserAvatarUrl();
  }

  /**
   * 保存本地用户token
   * @param token
   * @param promise
   */
  @ReactMethod
  public void saveUserToken(String token, final Promise promise) {
    try {
      this.settingManager.saveUserToken(token);
      promise.resolve(true);
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 保存本地用户id
   * @param userID
   * @param promise
   */
  @ReactMethod
  public void saveUserID(String userID, final Promise promise) {
    try {
      this.settingManager.saveUserID(userID);
      promise.resolve(true);
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 保存本地用户昵称
   * @param nickName
   * @param promise
   */
  @ReactMethod
  public void saveUserNickName(String nickName, final Promise promise) {
    try {
      this.settingManager.saveUserNickName(nickName);
      promise.resolve(true);
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 保存本地用户头像
   * @param avatarUrl
   * @param promise
   */
  @ReactMethod
  public void saveUserAvatarUrl(String avatarUrl, final Promise promise) {
    try {
      this.settingManager.saveUserAvatarUrl(avatarUrl);
      promise.resolve(true);
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 保存本地设备信息
   * @param deviceInfo
   * @param promise
   */
  @ReactMethod
  public void saveDeviceInfo(final ReadableMap deviceInfo, final Promise promise) {
    try {
      DeviceInfo info = new DeviceInfo();
      info.fromReadableMap(deviceInfo);
      this.settingManager.saveDeviceInfo(info.getUuid(), info.getBrand(), info.getMode(), info.getSysVersion(), info.getSdkVersion());
      promise.resolve(true);
    } catch (JSONException e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 获取本地设备信息
   * @param promise
   */
  @ReactMethod
  public void getDeviceInfo(final Promise promise) {
    try {
      DeviceInfo info = this.settingManager.getDeviceInfo();
      promise.resolve(info.toWritableMap());
    } catch (JSONException e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 发送消息
   * @param msg
   * @param promise
   */
  @ReactMethod
  public void sendMessage(ReadableMap msg, final Promise promise) {
    try {
      String token = StringUtils.emptyDefault(msg.getString("token"));
      int nReceiverType = msg.getInt("receiverType");
      String receiverId =StringUtils.emptyDefault(msg.getString("receiverId"), "");
      String toUserIds = StringUtils.emptyDefault(msg.getString("toUserIds"), "");
      String msgContent = StringUtils.emptyDefault(msg.getString("content"), "");
      int nMsgType = msg.getInt("messageType");
      ReceiverType receiverType = ReceiverType.values()[nReceiverType];
      ChatMessageType msgType = ChatMessageType.values()[nMsgType];
      this.imClient.send(token, receiverType, receiverId, toUserIds, msgType, msgContent, new IMServerCallback<SendResContent>() {
        @Override
        public void onResult(IMServerResult<SendResContent> result) {
          promise.resolve(result.toWritableMap());
        }
      });
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 发送聊天事件
   * @param options
   * @param promise
   */
  @ReactMethod
  public void sendChatEvent(ReadableMap options, final Promise promise) {
    try {
      String token = StringUtils.emptyDefault(options.getString("token"), "");
      String receiverId = StringUtils.emptyDefault(options.getString("receiverId"), "");
      int event = options.getInt("event");
      this.imClient.sendEvent(token, receiverId, event, new IMServerCallback() {
        @Override
        public void onResult(IMServerResult result) {
          promise.resolve(result.toWritableMap());
        }
      });
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 设置消息为已读
   * @param options
   * @param promise
   */
  @ReactMethod
  public void setMessageRead(ReadableMap options, final Promise promise) {
    try {
      String token = StringUtils.emptyDefault(options.getString("token"), "");
      int msgId = options.getInt("msgId");
      this.imClient.setMessageRead(token, msgId, new IMServerCallback() {
        @Override
        public void onResult(IMServerResult result) {
          promise.resolve(result.toWritableMap());
        }
      });
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 获取消息详情
   * @param options
   * @param promise
   */
  @ReactMethod
  public void getMessage(ReadableMap options, final Promise promise) {
    try {
      String token = StringUtils.emptyDefault(options.getString("token"), "");
      int msgId = options.getInt("msgId");
      this.imClient.getMsg(token, msgId, new IMServerCallback<MsgContent>() {
        @Override
        public void onResult(IMServerResult<MsgContent> result) {
          promise.resolve(result.toWritableMap());
        }
      });
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 获取历史联系人列表,包含系统通知
   * @param options
   * @param promise
   */
  @ReactMethod
  public void getContactList(ReadableMap options, final Promise promise) {
    try {
      String token = StringUtils.emptyDefault(options.getString("token"), "");
      String search = StringUtils.emptyDefault(options.getString("search"), "");
      int pi = options.getInt("pi");
      int ps = options.getInt("ps");
      this.imClient.getContacts(token, search, pi, ps, new IMServerCallback<ContactPage>() {
        @Override
        public void onResult(IMServerResult<ContactPage> result) {
          promise.resolve(result.toWritableMap());
        }
      });
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 同步消息到本地
   * @param options
   * @param promise
   */
  @ReactMethod
  public void syncMessageList(ReadableMap options, final Promise promise) {
    try {
      String token = StringUtils.emptyDefault(options.getString("token"), "");
      int seq = options.getInt("seq");
      int count = options.getInt("count");
      this.imClient.syncMessageList(token, seq, count, new IMServerCallback<MsgContentList>() {
        @Override
        public void onResult(IMServerResult<MsgContentList> result) {
          promise.resolve(result.toWritableMap());
        }
      });
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 删除消息
   * @param options
   * @param promise
   */
  @ReactMethod
  public void delMessage(ReadableMap options, final Promise promise) {
    try {
      String token = StringUtils.emptyDefault(options.getString("token"), "");
      int msgId = options.getInt("msgId");
      this.imClient.delMessage(token, msgId, new IMServerCallback() {
        @Override
        public void onResult(IMServerResult result) {
          promise.resolve(result.toWritableMap());
        }
      });
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 注册设备
   * @param options
   * @param promise
   */
  @ReactMethod
  public void registerDevice(ReadableMap options, final Promise promise) {
    try {
      DeviceInfo deviceInfo = new DeviceInfo();
      deviceInfo.fromReadableMap(options);
      String token = this.settingManager.getUserToken();
      String userId = this.getUserID();
      this.imClient.regDevice(token, userId, deviceInfo, new IMServerCallback<String>() {
        @Override
        public void onResult(IMServerResult<String> result) {
          promise.resolve(result.toWritableMap());
        }
      });
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 注册用户信息
   * @param options
   * @param promise
   */
  @ReactMethod
  public void registerUser(ReadableMap options, final Promise promise) {
    try {
      String token = StringUtils.emptyDefault(options.getString("token"), "");
      UserInfo user = new UserInfo();
      user.fromReadableMap(options);
      this.imClient.regUser(token, user, new IMServerCallback() {
        @Override
        public void onResult(IMServerResult result) {
          promise.resolve(result.toWritableMap());
        }
      });
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 在连接上绑定用户
   * @param options
   * @param promise
   */
  @ReactMethod
  public void userSignIn(ReadableMap options, final Promise promise) {
    try {
//      String token = StringUtils.emptyDefault(options.getString("token"), "");
//      String userId = StringUtils.emptyDefault(options.getString("userId"), "");
//      WritableMap res = Arguments.createMap();;
//      if (StringUtils.isBlank(token) || StringUtils.isBlank(userId)) {
//        res.putBoolean("error", true);
//        res.putString("msg", "token或userId不能为空");
//        promise.resolve(res);
//        return;
//      }
//      if (chatServiceBinder == null) {
//        res.putBoolean("error", true);
//        res.putString("msg", "未连接到chatService服务");
//        return;
//      }
//      chatServiceBinder.userSignIn(token, userId);
//      res.putBoolean("error", false);
//      res.putString("msg", "操作成功,在onSignInResp事件中获取操作结果");
      promise.resolve(false);
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 手动连接
   * @param options
   * @param promise
   */
  @ReactMethod
  public void socketConnect(ReadableMap options, final Promise promise) {
    try {
//      WritableMap res = Arguments.createMap();;
//      if (chatServiceBinder == null) {
//        res.putBoolean("error", true);
//        res.putString("msg", "未连接到chatService服务");
//        return;
//      }
//      chatServiceBinder.Connect();
//      res.putBoolean("error", false);
//      res.putString("msg", "操作成功");
      promise.resolve(false);
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  @Nullable
  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> result = new HashMap<>();
    result.put(ChatSocketConstants.CST_SENDER_TYPE_UNKNOWN, SenderType.Unknown.ordinal());
    result.put(ChatSocketConstants.CST_SENDER_TYPE_IM, SenderType.IM.ordinal());
    result.put(ChatSocketConstants.CST_SENDER_TYPE_USER, SenderType.User.ordinal());
    result.put(ChatSocketConstants.CST_SENDER_TYPE_BUSINESS, SenderType.Business.ordinal());
    result.put(ChatSocketConstants.CST_RECEIVER_TYPE_UNKNOWN, ReceiverType.Unknown.ordinal());
    result.put(ChatSocketConstants.CST_RECEIVER_TYPE_USER, ReceiverType.User.ordinal());
    result.put(ChatSocketConstants.CST_RECEIVER_TYPE_GROUP, ReceiverType.Group.ordinal());
    result.put(ChatSocketConstants.CST_USER_SEX_UNKNOWN, UserSex.Unknown.ordinal());
    result.put(ChatSocketConstants.CST_USER_SEX_BOY, UserSex.Boy.ordinal());
    result.put(ChatSocketConstants.CST_USER_SEX_GIRL, UserSex.Girl.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_TYPE_UNKNOWN, ChatMessageType.Unknown.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_TYPE_TEXT, ChatMessageType.Text.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_TYPE_EMOJI, ChatMessageType.Emoji.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_TYPE_VOICE, ChatMessageType.Voice.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_TYPE_PIC, ChatMessageType.Pic.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_TYPE_FILE, ChatMessageType.File.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_TYPE_LOCATION, ChatMessageType.Location.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_TYPE_CMD, ChatMessageType.Cmd.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_TYPE_DEFINE, ChatMessageType.Define.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_STATUS_UNKNOWN, MessageStatus.Unknown.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_STATUS_NORMAL, MessageStatus.Normal.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_STATUS_RECALL, MessageStatus.Recall.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_STATUS_READ, MessageStatus.Read.ordinal());
    result.put(ChatSocketConstants.CST_MESSAGE_STATUS_BE_READ, MessageStatus.BeRead.ordinal());
    return result;
  }

  @Override
  public void onHostResume() {
    Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onHostResume");
  }

  @Override
  public void onHostPause() {
    Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onHostPause");
  }

  @Override
  public void onHostDestroy() {
    Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "onHostDestroy");
  }

  /**
   * chat事件接收
   */
  final BroadcastReceiver chatReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent == null) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "BroadcastReceiver.onReceive intent is null.");
        return;
      }
      Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "BroadcastReceiver.onReceive action:" + intent.getAction());
      if (ChatSocketConstants.CST_BROADCAST_CHAT_ACTION.equals(intent.getAction())) {
        return;
      }
      Bundle params = intent.getExtras();
      String event = params.getString("event");
      String data = params.getString("content");
      Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "BroadcastReceiver.onReceive data:" + params.toString());
      emitChatData(event, data);
    }
  };

  private void emitChatData(String event, String data) {
    DeviceEventManagerModule.RCTDeviceEventEmitter emitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    if (emitter != null) {
      try {
        JSONObject obj = new JSONObject(data);
        WritableMap params = ReactUtils.convertJsonObjectToWritable(obj);
        emitter.emit(event, params);
      } catch (ChatSocketException e1) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "emitChatData error:" + e1.getMessage());
      } catch (JSONException e) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "emitChatData error:" + e.getMessage());
      }
    }
  }
}
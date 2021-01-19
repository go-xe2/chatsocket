
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

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactBridge;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
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
  private static ReactApplicationContext reactContext;
  private final SettingManager settingManager;
  private final IMServerClient imClient;
  private Intent chatServiceIndent;
  private static RNChatSocketModule instance;
  @RequiresApi(api = Build.VERSION_CODES.O)
  public RNChatSocketModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.settingManager = new SettingManager(reactContext);
    this.imClient = new IMServerClient(reactContext);
    instance = this;
//    startChatService();
//     注册接收者
//    com.mnyun.chatsocket.action.chat
//    reactContext.registerReceiver(new ChatBroadcastReceiver(this), new IntentFilter(ChatSocketConstants.CST_BROADCAST_CHAT_ACTION));
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
      if (info == null) {
        info = new DeviceInfo();
      }
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
  public void signIn(ReadableMap options, final Promise promise) {
    try {
      String token = StringUtils.emptyDefault(options.getString("token"), "");
      String userId = StringUtils.emptyDefault(options.getString("userId"), "");
      WritableMap res = Arguments.createMap();;
      if (StringUtils.isBlank(token) || StringUtils.isBlank(userId)) {
        res.putBoolean("error", true);
        res.putString("msg", "token或userId不能为空");
        promise.resolve(res);
        return;
      }
      Intent signIndent = new Intent(reactContext, ChatServiceCtrlReceiver.class);
      signIndent.putExtra(ChatSocketConstants.CTRL_CHAT_CTRL_TYPE, ChatSocketConstants.CTRL_CHAT_SIGN_IN);
      Bundle content = new Bundle();
      content.putString("token", token);
      content.putString("userId", userId);
      signIndent.putExtra(ChatSocketConstants.CTRL_CHAT_CTRL_PARAM,content);
      reactContext.sendBroadcast(signIndent);
      res.putBoolean("error", false);
      res.putString("msg", "操作成功");
      promise.resolve(res);
    } catch (Exception e) {
      WritableMap result = Arguments.createMap();
      result.putBoolean("error", true);
      result.putString("msg", e.getMessage());
      promise.resolve(result);
    }
  }

  /**
   * 用户退出登录,与device解除绑定
   * @param options
   * @param promise
   */
  @ReactMethod
  public void signOut(ReadableMap options, final Promise promise) {
    try {
      String token = StringUtils.emptyDefault(options.getString("token"), "");
      WritableMap res = Arguments.createMap();;
      if (StringUtils.isBlank(token)) {
        res.putBoolean("error", true);
        res.putString("msg", "token不能为空");
        promise.resolve(res);
        return;
      }
      Intent signIndent = new Intent(reactContext, ChatServiceCtrlReceiver.class);
      signIndent.putExtra(ChatSocketConstants.CTRL_CHAT_CTRL_TYPE, ChatSocketConstants.CTRL_CHAT_SIGN_OUT);
      Bundle content = new Bundle();
      content.putString("token", token);
      signIndent.putExtra(ChatSocketConstants.CTRL_CHAT_CTRL_PARAM,content);
      reactContext.sendBroadcast(signIndent);
      res.putBoolean("error", false);
      res.putString("msg", "操作成功");
      promise.resolve(res);
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

  public static class ChatBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent == null) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "BroadcastReceiver.onReceive intent is null.");
        return;
      }
      Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "BroadcastReceiver.onReceive action:" + intent.getAction());
      if (!ChatSocketConstants.CST_BROADCAST_CHAT_ACTION.equals(intent.getAction())) {
        return;
      }
      Bundle params = intent.getExtras();
      String event = params.getString("event");
      String data = params.getString("content");
      Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "BroadcastReceiver.onReceive data:" + params.toString());
      try {
        JSONObject obj = new JSONObject(data);
        WritableMap paramMap = ReactUtils.convertJsonObjectToWritable(obj);
        ChatManager.getInstance().emitEvent(context, intent, event, paramMap);
      } catch (ChatSocketException e1) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "emitChatData error:" + e1.getMessage());
      } catch (JSONException e) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "emitChatData error:" + e.getMessage());
      }
    }
  }
}

package com.mnyun.chatsocket;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONException;

public class RNChatsocketModule extends ReactContextBaseJavaModule {
  private final ReactApplicationContext reactContext;
  private SettingManager settingManager;
  public RNChatsocketModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.settingManager = new SettingManager(reactContext);
  }

  @Override
  public String getName() {
    return "RNChatsocket";
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getVersion() {
    return "v1.0.0";
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getIMHost() {
    return this.settingManager.getIMHost();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getIMHttpUrl() {
    return this.settingManager.getIMHttpUrl();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getAppKey() {
   return this.settingManager.getAppKey();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getAppSecret() {
    return this.settingManager.getAppSecret();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getDeviceID() {
    return this.settingManager.getDeviceID();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getUserToken() {
    return this.settingManager.getUserToken();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getUserID() {
    return this.settingManager.getUserID();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getUserNickName() {
    return this.settingManager.getUserNickName();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String getUserAvatarUrl() {
    return this.settingManager.getUserAvatarUrl();
  }

  @ReactMethod
  public void saveUserToken(String token, final Promise promise) {
    try {
      this.settingManager.saveUserToken(token);
      promise.resolve(true);
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  public void saveUserID(String userID, final Promise promise) {
    try {
      this.settingManager.saveUserID(userID);
      promise.resolve(true);
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  public void saveUserNickName(String nickName, final Promise promise) {
    try {
      this.settingManager.saveUserNickName(nickName);
      promise.resolve(true);
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  public void saveUserAvatarUrl(String avatarUrl, final Promise promise) {
    try {
      this.settingManager.saveUserAvatarUrl(avatarUrl);
      promise.resolve(true);
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  public void saveDeviceInfo(final ReadableMap deviceInfo, final Promise promise) {
    try {
      String uuid = deviceInfo.getString("uuid");
      String brand = deviceInfo.getString("brand");
      String mode = deviceInfo.getString("mode");
      String sysVersion = deviceInfo.getString("sysVersion");
      String sdkVersion = deviceInfo.getString("sdkVersion");
      uuid = uuid == null ? "" : uuid;
      brand = brand == null ? "" : brand;
      mode = mode == null ? "" : mode;
      sysVersion = sysVersion == null ? "" : sysVersion;
      sdkVersion = sdkVersion == null ? "" : sdkVersion;
      this.settingManager.saveDeviceInfo(uuid, brand, mode, sysVersion, sdkVersion);
      promise.resolve(true);
    } catch (JSONException e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  public void getDeviceInfo(final Promise promise) {
    try {
      SettingManager.DeviceInfo info = this.settingManager.getDeviceInfo();
      WritableMap map = Arguments.createMap();
      map.putString("uuid", info.getUuid());
      map.putString("brand", info.getBrand());
      map.putString("mode", info.getMode());
      map.putString("sysVersion", info.getSysVersion());
      map.putString("sdkVersion", info.getSdkVersion());
      promise.resolve(map);
    } catch (JSONException e) {
      promise.reject(e);
    }
  }
}
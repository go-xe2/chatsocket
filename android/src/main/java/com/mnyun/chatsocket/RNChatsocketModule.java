
package com.mnyun.chatsocket;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNChatsocketModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNChatsocketModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNChatsocket";
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String Version() {
    return "v1.0.0";
  }

  @ReactMethod(isBlockingSynchronousMethod = false)
  public String IMHost() {
    SettingManager setting = new SettingManager(this.getReactApplicationContext());
    return setting.GetIMHost();
  }
}
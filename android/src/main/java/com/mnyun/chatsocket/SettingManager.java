package com.mnyun.chatsocket;

import com.facebook.react.bridge.ReactApplicationContext;
import android.content.Context;
import android.content.SharedPreferences;

public class SettingManager {
    private Context mContext;
    private SharedPreferences mSettings;

    public SettingManager(Context applicationContext) {
        super();
        this.mContext = applicationContext;
        mSettings = applicationContext.getSharedPreferences(ChatSocketConstants.CHAT_SOCKET_PREFERENCES, 0);
    }
    /**
     * 读取项目的IMHost配置
     * @return
     */
    public String GetIMHost() {
        return this.getCustomPropertyFromStringsIfExist("IMHost");
    }
    private String getCustomPropertyFromStringsIfExist(String propertyName) {
        String property;

        String packageName = this.mContext.getPackageName();
        int resId = this.mContext.getResources().getIdentifier("ChatSocket" + propertyName, "string", packageName);

        if (resId != 0) {
            property = this.mContext.getString(resId);
            if (!property.isEmpty()) {
                return property;
            } else {
                ChatSocketUtils.log("Specified " + propertyName + " is empty");
            }
        }
        return null;
    }
    /**
     * 读取deviceId配置
     * @return
     */
    public String GetDeviceID() {
        String deviceId = mSettings.getString(ChatSocketConstants.DEVICE_ID_KEY, "");
        return deviceId;
    }

    /**
     * 保存deviceID配置
     * @param deviceId
     */
    public void SaveDeviceID(String deviceId) {
        mSettings.edit().putString(ChatSocketConstants.DEVICE_ID_KEY, deviceId).commit();
    }
}

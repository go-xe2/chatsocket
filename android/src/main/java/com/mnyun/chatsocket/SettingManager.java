package com.mnyun.chatsocket;

import com.facebook.react.bridge.ReactApplicationContext;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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
    public String getIMHost() {
        return this.getCustomPropertyFromStringsIfExist("IMHost");
    }

    /**
     * 获取IM服务http请求地址
     * @return
     */
    public String getIMHttpUrl() {
        return  this.getCustomPropertyFromStringsIfExist("IMHttpUrl");
    }

    /**
     * 获取IM的AppKey配置
     * @return
     */
    public String getAppKey() {
        return this.getCustomPropertyFromStringsIfExist("AppKey");
    }

    /**
     * 获取IM的AppSecret配置
     * @return
     */
    public String getAppSecret() {
        return this.getCustomPropertyFromStringsIfExist("AppSecret");
    }

    private String getCustomPropertyFromStringsIfExist(String propertyName) {
        String property;

        String packageName = this.mContext.getPackageName();
        int resId = this.mContext.getResources().getIdentifier("ChatSocket" + propertyName, "string", packageName);
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "getCustomPropertyFromStringsIfExist key:" + "ChatSocket" + propertyName);
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "resId:" + resId);
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
    public String getDeviceID() {
        String deviceId = mSettings.getString(ChatSocketConstants.DEVICE_ID_KEY, "");
        return deviceId;
    }

    /**
     * 保存deviceID配置
     * @param deviceId
     */
    public void saveDeviceID(String deviceId) {
        mSettings.edit().putString(ChatSocketConstants.DEVICE_ID_KEY, deviceId).commit();
    }

    /**
     * 获取用户令牌
     * @return
     */
    public String getUserToken() {
        String token = mSettings.getString(ChatSocketConstants.USER_TOKEN_KEY, "");
        return token;
    }

    /**
     * 获取用户ID
     * @return
     */
    public String getUserID() {
        return mSettings.getString(ChatSocketConstants.USER_ID_KEY, "");
    }

    /**
     * 获取用户昵称
     * @return
     */
    public String getUserNickName() {
        return mSettings.getString(ChatSocketConstants.USER_NICK_NAME_KEY, "");
    }

    /**
     * 获取用户头像
     * @return
     */
    public String getUserAvatarUrl() {
        return mSettings.getString(ChatSocketConstants.USER_AVATAR_URL_KEY, "");
    }

    /**
     * 保存用户令牌
     * @param token
     */
    public void saveUserToken(String token) {
        mSettings.edit().putString(ChatSocketConstants.USER_TOKEN_KEY, token).commit();
    }

    /**
     * 保存用户ID
     * @param userID
     */
    public void saveUserID(String userID) {
        mSettings.edit().putString(ChatSocketConstants.USER_ID_KEY, userID).commit();
    }

    /**
     * 保存用户昵称
     * @param nickName
     */
    public void saveUserNickName(String nickName) {
        mSettings.edit().putString(ChatSocketConstants.USER_NICK_NAME_KEY, nickName).commit();
    }

    /**
     * 保存用户头像
     * @param avatarUrl
     */
    public void saveUserAvatarUrl(String avatarUrl) {
        mSettings.edit().putString(ChatSocketConstants.USER_AVATAR_URL_KEY, avatarUrl).commit();
    }

    /**
     * 保存设备信息
     * @param uuid
     * @param brand
     * @param mode
     * @param sysVersion
     * @param sdkVersion
     * @throws JSONException
     */
    public void saveDeviceInfo(String uuid, String brand, String mode,String sysVersion, String sdkVersion) throws JSONException {
        JSONObject setting = new JSONObject();
        setting.put("uuid", uuid);
        setting.put("brand", brand);
        setting.put("mode", mode);
        setting.put("sysVersion", sysVersion);
        setting.put("sdkVersion", sdkVersion);
        mSettings.edit().putString(ChatSocketConstants.DEVICE_INFO_KEY, setting.toString()).commit();
    }

    public DeviceInfo getDeviceInfo() throws JSONException {
        String szData = mSettings.getString(ChatSocketConstants.DEVICE_INFO_KEY, null);
        if (szData == null) {
            return null;
        }
        JSONObject obj = new JSONObject(szData);
        DeviceInfo result = new DeviceInfo();
        result.uuid = obj.getString("uuid");
        result.brand = obj.getString("brand");
        result.mode = obj.getString("mode");
        result.sysVersion = obj.getString("sysVersion");
        result.sdkVersion = obj.getString("sdkVersion");
        return result;
    }

    public class DeviceInfo {
        private String uuid = "";
        private String brand = "";
        private String mode = "";
        private String sysVersion = "";
        private String sdkVersion = "";

        public String getUuid() {
            return uuid;
        }

        public String getBrand() {
            return brand;
        }

        public String getMode() {
            return mode;
        }

        public String getSysVersion() {
            return sysVersion;
        }

        public String getSdkVersion() {
            return sdkVersion;
        }
    }

}

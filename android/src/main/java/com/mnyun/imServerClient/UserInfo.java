package com.mnyun.imServerClient;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.mnyun.utils.StringUtils;

public class UserInfo implements WritableConvert {
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserSex getSex() {
        return sex;
    }

    public void setSex(UserSex sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    String nickName;
    String avatarUrl;
    UserSex sex;
    String mobile;
    String extra;

    @Override
    public WritableMap toWritableMap() {
        WritableMap map = Arguments.createMap();
        map.putString("nickName", this.nickName);
        map.putString("avatarUrl", this.avatarUrl);
        map.putInt("sex", this.sex.ordinal());
        map.putString("mobile", this.mobile);
        map.putString("extra", this.extra);
        return map;
    }

    /**
     * 从readableMap读取数据
     * @param map
     */
    public void fromReadableMap(ReadableMap map) {
        if (map == null) {
            return;
        }
        this.nickName = StringUtils.emptyDefault(map.getString("nickName"), "");
        this.avatarUrl = StringUtils.emptyDefault(map.getString("avatarUrl"), "");
        this.mobile = StringUtils.emptyDefault(map.getString("mobile"), "");
        this.extra = StringUtils.emptyDefault(map.getString("extra"));
        int nSex = map.getInt("sex");
        this.sex = UserSex.values()[nSex];
    }
}

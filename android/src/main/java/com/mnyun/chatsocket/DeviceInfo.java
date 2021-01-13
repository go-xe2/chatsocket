package com.mnyun.chatsocket;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.mnyun.imServerClient.WritableConvert;
import com.mnyun.utils.StringUtils;

public class DeviceInfo implements WritableConvert  {
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

    public DeviceInfo() {
        super();
    }
    public DeviceInfo(String uuid, String brand, String mode, String sysVersion, String sdkVersion) {
        super();
        this.uuid = uuid;
        this.brand = brand;
        this.mode = mode;
        this.sysVersion = sysVersion;
        this.sdkVersion = sdkVersion;
    }

    @Override
    public WritableMap toWritableMap() {
        WritableMap map = Arguments.createMap();
        map.putString("uuid", this.uuid);
        map.putString("brand", this.brand);
        map.putString("mode", this.mode);
        map.putString("sysVersion", this.sysVersion);
        map.putString("sdkVersion", this.sdkVersion);
        return map;
    }

    public void fromReadableMap(ReadableMap map) {
        if (map == null) {
            return;
        }
        this.uuid = StringUtils.emptyDefault(map.getString("uuid"));
        this.brand = StringUtils.emptyDefault(map.getString("brand"), "");
        this.mode = StringUtils.emptyDefault(map.getString("mode"), "");
        this.sysVersion = StringUtils.emptyDefault(map.getString("sysVersion"), "");
        this.sdkVersion = StringUtils.emptyDefault(map.getString("sdkVersion"), "");
    }
}
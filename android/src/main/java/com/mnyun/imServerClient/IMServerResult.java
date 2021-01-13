package com.mnyun.imServerClient;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.mnyun.utils.DateUtils;

import java.util.Date;

import javax.annotation.Nullable;

/**
 * IM服务器返回数据结构
 * @param <T>
 */
public class IMServerResult<T> implements WritableConvert {
    private boolean error;
    private String msg;
    private T content;

    public IMServerResult(boolean err, String msg) {
        super();
        this.error = err;
        this.msg = msg;
    }

    public IMServerResult(boolean err, String msg, @Nullable T content) {
        super();
        this.error = err;
        this.msg = msg;
        this.content = content;
    }

    public boolean isError() {
        return error;
    }
    public String getMsg() {
        return msg;
    }

    public T getContent() {
        return content;
    }

    @Override
    public WritableMap toWritableMap() {
        WritableMap map = Arguments.createMap();
        map.putBoolean("error", this.error);
        map.putString("msg", this.msg);
        if (this.content != null) {
            if (this.content instanceof WritableConvert) {
                map.putMap("content", ((WritableConvert) this.content).toWritableMap());
            } else if (this.content instanceof Integer) {
                int value = ((Integer) this.content).intValue();
                map.putInt("content", value);
            } else if (this.content instanceof String) {
                String s = (String) this.content;
                map.putString("content", s);
            } else if (this.content instanceof Double) {
                double d = ((Double) this.content).doubleValue();
                map.putDouble("content", d);
            } else if (this.content instanceof Float) {
                float f = ((Float) this.content).floatValue();
                map.putDouble("content", f);
            } else if (this.content instanceof Long) {
                long l = ((Long) this.content).longValue();
                map.putInt("content", (int)l);
            } else if (this.content instanceof Boolean) {
                boolean b = ((Boolean) this.content).booleanValue();
                map.putBoolean("content", b);
            } else if (this.content instanceof Date) {
                Date d = (Date) this.content;
                map.putString("content", DateUtils.DateFormat(d));
            } else {
                map.putNull("content");
            }
        }
        return map;
    }
}

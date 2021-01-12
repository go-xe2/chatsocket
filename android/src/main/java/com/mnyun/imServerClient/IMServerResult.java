package com.mnyun.imServerClient;

import javax.annotation.Nullable;

/**
 * IM服务器返回数据结构
 * @param <T>
 */
public class IMServerResult<T> {
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
}

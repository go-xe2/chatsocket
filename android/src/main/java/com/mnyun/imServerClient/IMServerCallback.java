package com.mnyun.imServerClient;

/**
 * IM服务调用回调接口
 * @param <T>
 */
public interface IMServerCallback<T> {
    void onResult(IMServerResult<T> result);
}
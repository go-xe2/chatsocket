package com.mnyun.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.mnyun.chatsocket.ChatSocketConstants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * OkHttpMange管理类
 * Created by leict on 2017/6/6.
 */

public class OkHttpManager {
    private OkHttpClient mClient;
//    private Handler mHnadler;
    private Gson mGson;

    private static class okHttpManagerInstance {
        private final static OkHttpManager instance = new OkHttpManager();
    }

    /**
     * 单例
     *
     * @return
     */
    public static OkHttpManager getInstance() {
        return okHttpManagerInstance.instance;
    }

    /**
     * 构造函数
     */
    private OkHttpManager() {
        super();
        initOkHttp();
        mGson = new Gson();
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "okHttpManager初始化完成.");
    }

    /**
     * 初始化OkHttpClient
     */
    private void initOkHttp() {
        mClient = new OkHttpClient().newBuilder()
                .readTimeout(30000, TimeUnit.SECONDS)
                .connectTimeout(30000, TimeUnit.SECONDS)
                .writeTimeout(30000, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 请求
     *
     * @param client
     * @param callBack
     */
    public void request(BaseOkHttpClient client, final BaseCallback callBack) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, " request:" + client.toString());
        if (callBack == null) {
            throw new NullPointerException(" callback is null");
        }
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, " request 提交.");
        mClient.newCall(client.buildRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, " request 提交失败:" + e.getMessage());
                sendonFailureMessage(callBack, call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, " request 提交返回.");
                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, " request 提交返回 isSuccessful:" + response.isSuccessful());
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, " request 提交返回:" + result);
                    if (callBack.mType == null || callBack.mType == String.class) {
                        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, " request 提交返回1.");
                        sendonSuccessMessage(callBack, result);
                    } else {
                        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, " request 提交返回2.");
                        sendonSuccessMessage(callBack, mGson.fromJson(result, callBack.mType));
                    }
                    if (response.body() != null) {
                        response.body().close();
                    }
                } else {
                    Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, " request 提交返回3.");
                    sendonErrorMessage(callBack, response.code());
                }
            }
        });
    }

    /**
     * 成功信息
     *
     * @param callBack
     * @param result
     */
    private void sendonSuccessMessage(final BaseCallback callBack, final Object result) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "sendonSuccessMessage run.");
        callBack.onSuccess(result);
//        mHnadler.post(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "sendonSuccessMessage run.");
//                callBack.onSuccess(result);
//            }
//        });
    }

    /**
     * 失败信息
     *
     * @param callBack
     * @param call
     * @param e
     */
    private void sendonFailureMessage(final BaseCallback callBack, final Call call, final IOException e) {
        Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "sendonFailureMessage run.");
        callBack.onFailure(call, e);
//        mHnadler.post(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(ChatSocketConstants.REACT_NATIVE_LOG_TAG, "sendonFailureMessage run.");
//                callBack.onFailure(call, e);
//            }
//        });
    }

    /**
     *
     * 错误信息
     *
     * @param callBack
     * @param code
     */
    private void sendonErrorMessage(final BaseCallback callBack, final int code) {
        callBack.onError(code);
//        mHnadler.post(new Runnable() {
//            @Override
//            public void run() {
//                callBack.onError(code);
//            }
//        });
    }
}
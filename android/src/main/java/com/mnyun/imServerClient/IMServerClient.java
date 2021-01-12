package com.mnyun.imServerClient;

import android.content.Context;

import com.mnyun.chatsocket.SettingManager;
import com.mnyun.net.BaseCallback;
import com.mnyun.net.BaseOkHttpClient;
import com.mnyun.net.OkHttpManager;
import com.mnyun.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class IMServerClient {
    private Context mContext;
    private SettingManager setting;
    private String imHttpUrl;
    private String appKey;
    private String appSecret;
    public IMServerClient(Context applicationContext) {
        super();
        this.mContext = applicationContext;
        this.setting = new SettingManager(applicationContext);
        this.imHttpUrl = this.setting.getIMHttpUrl();
    }

    public IMServerResult parseJsonResult(String data, IMContentConvert convert) {
        try {
            JSONObject obj = new JSONObject(data);
            int status = obj.getInt("status");
            String msg = obj.getString("msg");
            if (status > 0) {
                return new IMServerResult(true, msg);
            }
            if (convert != null) {
                try {
                    Object content = convert.Convert(obj);
                    return new IMServerResult(false, msg, content);
                } catch (Exception e) {
                    return new IMServerResult(true, e.getMessage());
                }
            }
            return new IMServerResult(false, msg);
        } catch (JSONException e) {
            return new IMServerResult(true, e.getMessage());
        }
    }

    /**
     * 注册设备
     * @param info
     * @param callback
     */
    public void regDevice(SettingManager.DeviceInfo info, final IMServerCallback<String> callback) {
        String url = this.imHttpUrl + "/RegDevice";
        BaseOkHttpClient.newBuilder()
                .addParam("app_key", this.appKey)
                .addParam("app_secret", this.appSecret)
                .addParam("uuid", info.getUuid())
                .addParam("brand", info.getBrand())
                .addParam("dtype", 1) // 0:未知设备,1:android,2:ios,3:windows,4:macos,5:web
                .addParam("mode", info.getMode())
                .addParam("sysversion", info.getSysVersion())
                .addParam("sdkversion", info.getSdkVersion())
                .post()
                .url(url)
                .build()
                .execute(new BaseCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        IMServerResult result = parseJsonResult(o.toString(), new IMContentConvert() {
                            @Override
                            public Object Convert(JSONObject content) throws Exception {
                                return content.getString("content");
                            }
                        });
                        callback.onResult(result);
                    }

                    @Override
                    public void onError(int code) {
                        callback.onResult(new IMServerResult<String>(true, "服务错误:" + code, ""));
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onResult(new IMServerResult<String>(true, "调用出错:" + e.getMessage(), ""));
                    }
                });
    }

    /**
     * 注册用户
     * @param token
     * @param user
     * @param callback
     */
    public void regUser(String token, UserInfo user, final IMServerCallback callback) {
        String url = this.imHttpUrl + "/RegUser";
        BaseOkHttpClient.newBuilder()
                .addParam("token", token)
                .addParam("nick_name", user.getNickName())
                .addParam("avatar_url", user.getAvatarUrl())
                .addParam("sex", user.getSex().ordinal())
                .addParam("mobile", user.getMobile())
                .addParam("extra", user.getExtra())
                .post()
                .url(url)
                .build()
                .execute(new BaseCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        IMServerResult result = parseJsonResult(o.toString(), null);
                        callback.onResult(result);
                    }

                    @Override
                    public void onError(int code) {
                        callback.onResult(new IMServerResult(true, "服务错误:" + code));
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onResult(new IMServerResult(true, e.getMessage()));
                    }
                });
    }

    /**
     * 标记消息为已读
     * @param token
     * @param msgId
     * @param callback
     */
    public void setMessageRead(String token, String msgId, final IMServerCallback callback) {
        String url = this.imHttpUrl + "/SetReadMsg";
        BaseOkHttpClient.newBuilder()
                .addParam("token", token)
                .addParam("msg_id", msgId)
                .post()
                .url(url)
                .build()
                .execute(new BaseCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        IMServerResult result = parseJsonResult(o.toString(), null);
                        callback.onResult(result);
                    }

                    @Override
                    public void onError(int code) {
                        callback.onResult(new IMServerResult(true, "服务错误:" + code));
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onResult(new IMServerResult(true, e.getMessage()));
                    }
                });
    }

    /**
     * 删除删除
     * @param token
     * @param msgId
     * @param callback
     */
    public void delMessage(String token, String msgId, final IMServerCallback callback) {
        String url = this.imHttpUrl + "/DelMsg";
        BaseOkHttpClient.newBuilder()
                .addParam("token", token)
                .addParam("msg_id", msgId)
                .post()
                .url(url)
                .build()
                .execute(new BaseCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        IMServerResult result = parseJsonResult(o.toString(), null);
                        callback.onResult(result);
                    }

                    @Override
                    public void onError(int code) {
                        callback.onResult(new IMServerResult(true, "服务错误:" + code));
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onResult(new IMServerResult(true, e.getMessage()));
                    }
                });
    }

    /**
     * 获取历史联系人列表
     * @param token
     * @param search
     * @param pi
     * @param ps
     * @param callback
     */
    public void getContacts(String token, String search, int pi, int ps, final IMServerCallback<ContactPage> callback) {
        String url = this.imHttpUrl + "/GetContacts";
        BaseOkHttpClient.newBuilder()
                .addParam("token", token)
                .addParam("search", search)
                .addParam("pi", pi)
                .addParam("ps", ps)
                .post()
                .url(url)
                .build()
                .execute(new BaseCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        IMServerResult result = parseJsonResult(o.toString(), new IMContentConvert() {
                            @Override
                            public Object Convert(JSONObject content) throws Exception {
                                JSONObject objContent = content.getJSONObject("content");
                                int pi = objContent.getInt("page_index");
                                int ps = objContent.getInt("page_size");
                                int pageCount = objContent.getInt("page_count");
                                int total = objContent.getInt("total");
                                JSONArray objRows = objContent.getJSONArray("rows");
                                ContactPage page = new ContactPage();
                                page.setPi(pi);
                                page.setPs(ps);
                                page.setPageCount(pageCount);
                                page.setTotal(total);
                                List<ContactInfo> list = new ArrayList<ContactInfo>();
                                page.setRows(list);
                                for (int i = 0; i < objRows.length(); i++) {
                                    JSONObject jsonItem = objRows.getJSONObject(i);
                                    int senderType = jsonItem.getInt("sender_type"); // 0: 未知,1: im系统,2:用户,3:业务系统
                                    String userId = jsonItem.getString("sender_id");
                                    String nickName = jsonItem.getString("nick_name");
                                    String avatarUrl = jsonItem.getString("avatar_url");
                                    String extra = jsonItem.getString("extra");
                                    int sex = jsonItem.getInt("sex");
                                    list.add(new ContactInfo(SenderType.values()[senderType], userId, nickName, avatarUrl, extra,UserSex.values()[sex]));
                                }
                                return page;
                            }
                        });
                        callback.onResult(result);
                    }

                    @Override
                    public void onError(int code) {
                        callback.onResult(new IMServerResult(true, "服务错误:" + code));
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onResult(new IMServerResult(true, e.getMessage()));
                    }
                });
    }

    /**
     * 发送消息
     * @param token
     * @param receiverType
     * @param receiverId
     * @param toUserIds
     * @param messageType
     * @param msg
     * @param callback
     */
    public void Send(String token, ReceiverType receiverType, String receiverId, String toUserIds, ChatMessageType messageType, String msg, final IMServerCallback<SendResContent> callback) {
        String url = this.imHttpUrl + "/Send";
        BaseOkHttpClient.newBuilder()
                .addParam("token", token)
                .addParam("receiver_type", receiverType.ordinal())
                .addParam("receiver_id", receiverId)
                .addParam("to_user_ids", toUserIds)
                .addParam("message_type", messageType.ordinal())
                .addParam("message_content", msg)
                .addParam("send_time", DateUtils.DateFormat(new Date()))
                .post()
                .url(url)
                .build()
                .execute(new BaseCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        IMServerResult result = parseJsonResult(o.toString(), new IMContentConvert() {
                            @Override
                            public Object Convert(JSONObject content) throws Exception {
                                JSONObject objContent = content.getJSONObject("content");
                                int msgId = objContent.getInt("msg_id");
                                int seqId = objContent.getInt("seq_id");
                                return new SendResContent(msgId, seqId);
                            }
                        });
                        callback.onResult(result);
                    }

                    @Override
                    public void onError(int code) {
                        callback.onResult(new IMServerResult(true, "服务错误:" + code));
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onResult(new IMServerResult(true, e.getMessage()));
                    }
                });
    }

}

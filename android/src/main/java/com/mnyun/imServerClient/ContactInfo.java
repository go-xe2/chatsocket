package com.mnyun.imServerClient;

import java.util.List;

/**
 * 联系人信息
 */
public class ContactInfo {
    private String userId;
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    private String nickName;
    private String avatarUrl;
    private UserSex sex;
    // 未读消息数
    private int msgCount;
    public ContactInfo() {}
    public ContactInfo(SenderType senderType, String userId, String nickName, String avatarUrl, String extra, UserSex sex) {
        super();
        this.senderType = senderType;
        this.userId = userId;
        this.nickName = nickName;
        this.avatarUrl = avatarUrl;
        this.extra = extra;
        this.sex = sex;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    private String extra;

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    private SenderType senderType;
}

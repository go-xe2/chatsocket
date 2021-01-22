package com.mnyun.imServerClient;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * 消息正文数据结构
 */
public class MsgContent implements WritableConvert {
    private int msgId;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderNickName() {
        return senderNickName;
    }

    public void setSenderNickName(String senderNickName) {
        this.senderNickName = senderNickName;
    }

    public String getSenderAvatarUrl() {
        return senderAvatarUrl;
    }

    public void setSenderAvatarUrl(String senderAvatarUrl) {
        this.senderAvatarUrl = senderAvatarUrl;
    }

    public String getSenderExtra() {
        return senderExtra;
    }

    public void setSenderExtra(String senderExtra) {
        this.senderExtra = senderExtra;
    }

    public UserSex getSenderSex() {
        return senderSex;
    }

    public void setSenderSex(UserSex senderSex) {
        this.senderSex = senderSex;
    }

    public ReceiverType getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(ReceiverType receiverType) {
        this.receiverType = receiverType;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getToUserIds() {
        return toUserIds;
    }

    public void setToUserIds(String toUserIds) {
        this.toUserIds = toUserIds;
    }

    public ChatMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(ChatMessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getSenderMsgId() {
        return senderMsgId;
    }

    public void setSenderMsgId(int senderMsgId) {
        this.senderMsgId = senderMsgId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    private int seq;
    private SenderType senderType;
    private String senderId;
    private String senderNickName;
    private String senderAvatarUrl;
    private String senderExtra;
    private UserSex senderSex;
    private ReceiverType receiverType;
    private String receiverId;
    private String toUserIds;
    private ChatMessageType messageType;
    private String content;
    private String sendTime;
    private MessageStatus status;
    private int readCount;
    private int senderMsgId;

    public ReceiverType getObjectType() {
        return objectType;
    }

    public void setObjectType(ReceiverType objectType) {
        this.objectType = objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    private ReceiverType objectType;
    private String objectId;
    /**
     * 转换为writableMap
     * @return
     */
    public WritableMap toWritableMap() {
        WritableMap map = Arguments.createMap();
        map.putInt("msgId", this.msgId);
        map.putInt("seq", this.seq);
        map.putInt("senderType", this.senderType.ordinal());
        map.putString("senderId", this.senderId);
        map.putString("senderNickName", this.senderNickName);
        map.putString("senderAvatarUrl", this.senderAvatarUrl);
        map.putInt("senderSex", this.senderSex.ordinal());
        map.putString("senderExtra", this.senderExtra);
        map.putInt("receiverType", this.receiverType.ordinal());
        map.putString("receiverId", this.receiverId);
        map.putString("toUserIds", this.toUserIds);
        map.putInt("messageType", this.messageType.ordinal());
        map.putString("content", this.content);
        map.putString("sendTime", this.sendTime);
        map.putInt("status", this.status.ordinal());
        map.putInt("readCount", this.readCount);
        map.putInt("senderMsgId", this.senderMsgId);
        map.putInt("objectType", this.objectType.ordinal());
        map.putString("objectId", this.objectId);
        return map;
    }
}

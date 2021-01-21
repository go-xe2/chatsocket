package com.mnyun.chatsocket;

import com.mnyun.utils.StringUtils;

import java.io.FileInputStream;
import java.security.PublicKey;

public class ChatSocketConstants {
    public static final String REACT_NATIVE_LOG_TAG = "ChatSocket";
    public static final String CHAT_SOCKET_PREFERENCES = "ChatSocket";
    public static final String DEVICE_ID_KEY = "device_id";
    public static final String USER_TOKEN_KEY = "user_token";
    public static final String USER_ID_KEY = "user_id";
    public static final String USER_NICK_NAME_KEY = "user_nick_name";
    public static final String USER_AVATAR_URL_KEY = "user_avatar_url";
    public static final String DEVICE_INFO_KEY = "device_info";
    public static final String CST_SENDER_TYPE_UNKNOWN = "SENDER_TYPE_UNKNOWN";
    public static final String CST_SENDER_TYPE_IM = "SENDER_TYPE_IM";
    public static final String CST_SENDER_TYPE_USER = "SENDER_TYPE_USER";
    public static final String CST_SENDER_TYPE_BUSINESS = "SENDER_TYPE_BUSINESS";
    public static final String CST_RECEIVER_TYPE_UNKNOWN = "RECEIVER_TYPE_UNKNOWN";
    public static final String CST_RECEIVER_TYPE_USER = "RECEIVER_TYPE_USER";
    public static final String CST_RECEIVER_TYPE_GROUP = "RECEIVER_TYPE_GROUP";
    public static final String CST_USER_SEX_UNKNOWN = "USER_SEX_UNKNOWN";
    public static final String CST_USER_SEX_BOY = "USER_SEX_BOY";
    public static final String CST_USER_SEX_GIRL = "USER_SEX_GIRL";
    public static final String CST_MESSAGE_TYPE_UNKNOWN = "MESSAGE_TYPE_UNKNOWN";
    public static final String CST_MESSAGE_TYPE_TEXT = "MESSAGE_TYPE_TEXT";
    public static final String CST_MESSAGE_TYPE_EMOJI = "MESSAGE_TYPE_EMOJI";
    public static final String CST_MESSAGE_TYPE_VOICE = "MESSAGE_TYPE_VOICE";
    public static final String CST_MESSAGE_TYPE_PIC = "MESSAGE_TYPE_PIC";
    public static final String CST_MESSAGE_TYPE_FILE = "MESSAGE_TYPE_FILE";
    public static final String CST_MESSAGE_TYPE_LOCATION = "MESSAGE_TYPE_LOCATION";
    public static final String CST_MESSAGE_TYPE_CMD = "MESSAGE_TYPE_CMD";
    public static final String CST_MESSAGE_TYPE_DEFINE="MESSAGE_TYPE_DEFINE";
    public static final String CST_MESSAGE_STATUS_UNKNOWN="MESSAGE_STATUS_UNKNOWN";
    public static final String CST_MESSAGE_STATUS_NORMAL="MESSAGE_STATUS_NORMAL";
    public static final String CST_MESSAGE_STATUS_RECALL="MESSAGE_STATUS_RECALL";
    public static final String CST_MESSAGE_STATUS_READ="MESSAGE_STATUS_READ";
    public static final String CST_MESSAGE_STATUS_BE_READ="MESSAGE_STATUS_BE_READ";
    public static final String CST_BROADCAST_CHAT_ACTION = "com.mnyun.chatsocket.action.chat";
    public static final String CST_START_CHAT_SERVICE_ACTION = "com.mnyun.chatsocket.action.start";
    public static final String CST_CHAT_SERVICE_CTRL_ACTION = "com.mnyun.chatsocket.action.ctrl";
    public static final String CST_ON_CHAT_OPEN_EVENT = "ON_CHAT_OPEN";
    public static final String CST_ON_CHAT_CLOSE_EVENT = "ON_CHAT_CLOSE";
    public static final String CST_ON_CHAT_SIGN_IN_RESP_EVENT = "ON_CHAT_SIGN_IN_RESP";
    public static final String CST_ON_CHAT_SIGN_OUT_RESP_EVENT = "ON_CHAT_SIGN_OUT_RESP";
    public static final String CST_ON_CHAT_MESSAGE_EVENT = "ON_CHAT_MESSAGE";
    public static final String CST_ON_CHAT_SEND_MESSAGE_RESP_EVENT = "ON_CHAT_SEND_MESSAGE_RESP";
    public static final String CST_ON_CHAT_ERROR_EVENT = "ON_CHAT_ERROR";
    public static final String CST_CHAT_SERVICE_WS_HOST_PARAM = "WS_HOST"; // chatService 服务的webSocket 地址
    public static final String CST_CHAT_SERVICE_HTTP_URL_PARAM = "HTTP_URL"; // chatService 服务的http地址
    public static final String CST_CHAT_SERVICE_USER_ID_PARAM = "USER_ID"; // chatService user_id参数名
    public static final String CST_CHAT_SERVICE_USER_TOKEN_PARAM = "USER_TOKEN"; // chatService 服务的 user_token参数
    public static final String CST_CHAT_SERVICE_DEVICE_INFO_PARAM = "DEVICE_INFO"; // chatService服务的device_info参数名
    public static final String CTRL_CHAT_SIGN_IN = "CHAT_SIGN_IN";
    public static final String CTRL_CHAT_SIGN_OUT = "CHAT_SIGN_OUT";
    public static final String CTRL_CHAT_CTRL_TYPE = "CTRL_TYPE";
    public static final String CTRL_CHAT_CTRL_PARAM = "CTRL_PARAM";
    public static final String NOTIFICATION_EVENT_CLICKED = "notification_clicked";
    public static final String NOTIFICATION_EVENT_CANCELLED ="notification_cancelled";
    public static final String NOTIFICATION_ID_PARAM = "notificationId";
    public static final String NOTIFICATION_CHANNEL_ID = "mnyun";
    public static final String NOTIFICATION_CHANNEL_NAME = "com.mnyun.app";
}

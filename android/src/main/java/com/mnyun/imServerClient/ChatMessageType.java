package com.mnyun.imServerClient;

// 消息类型,0:未知,1:文本,2:表情,3:语音,4:图片,5:文件,6:地理位置,7:指令推送,8:自定义
public enum ChatMessageType {
    Unknown,
    Text,
    Emoji,
    Vol,
    Pic,
    File,
    Location,
    Cmd,
    Define
}

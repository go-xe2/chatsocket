package com.mnyun.imServerClient;

public enum MessageStatus {
    // 未知状态
    Unknown,
    // 正常消息,未读或未被读
    Normal,
    // 撤回
    Recall,
    // 自己消息已读
    Read,
    // 被阅读
    BeRead
}

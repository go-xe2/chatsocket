package com.mnyun.imServerClient;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

public class SendResContent implements WritableConvert {
    private int msgId;
    private int seqId;

    public SendResContent() {
        super();
    }

    public SendResContent(int msgId, int seqId) {
        super();
        this.msgId = msgId;
        this.seqId = seqId;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public int getSeqId() {
        return seqId;
    }

    public void setSeqId(int seqId) {
        this.seqId = seqId;
    }


    @Override
    public WritableMap toWritableMap() {
        WritableMap map = Arguments.createMap();
        map.putInt("msgId", this.msgId);
        map.putInt("seq", this.seqId);
        return map;
    }
}

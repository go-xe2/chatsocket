package com.mnyun.imServerClient;

public class SendResContent {
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



}

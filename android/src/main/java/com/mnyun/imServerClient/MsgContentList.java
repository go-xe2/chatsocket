package com.mnyun.imServerClient;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.List;

public class MsgContentList implements WritableConvert {
    private boolean isEnd;
    private List<MsgContent> rows;

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public List<MsgContent> getRows() {
        return rows;
    }

    public void setRows(List<MsgContent> rows) {
        this.rows = rows;
    }

    /**
     * 转换为writableMap对象
     * @return
     */
    public WritableMap toWritableMap() {
        WritableMap map = Arguments.createMap();
        map.putBoolean("isEnd", this.isEnd);
        WritableArray rows = Arguments.createArray();
        if (this.rows != null) {
            for (int i = 0; i < this.rows.size();i++) {
                rows.pushMap(this.rows.get(i).toWritableMap());
            }
        }
        map.putArray("rows", rows);
        return map;
    }
}

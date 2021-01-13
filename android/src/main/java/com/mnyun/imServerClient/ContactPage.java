package com.mnyun.imServerClient;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.List;

public class ContactPage implements WritableConvert {
    private int pi;
    private int ps;

    public int getPi() {
        return pi;
    }

    public void setPi(int pi) {
        this.pi = pi;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ContactInfo> getRows() {
        return rows;
    }

    public void setRows(List<ContactInfo> rows) {
        this.rows = rows;
    }

    private int pageCount;
    private int total;
    private List<ContactInfo> rows;

    @Override
    public WritableMap toWritableMap() {
        WritableMap map = Arguments.createMap();
        map.putInt("pi", this.pi);
        map.putInt("ps", this.ps);
        map.putInt("pageCount", this.pageCount);
        map.putInt("total", this.total);
        WritableArray rows = Arguments.createArray();
        if (this.rows != null) {
            for (int i = 0; i < this.rows.size(); i++) {
                rows.pushMap(this.rows.get(i).toWritableMap());
            }
        }
        map.putArray("rows", rows);
        return map;
    }
}

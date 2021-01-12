package com.mnyun.imServerClient;

import java.util.List;

public class ContactPage {
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
}

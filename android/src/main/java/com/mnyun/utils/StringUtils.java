package com.mnyun.utils;

public class StringUtils {
    /**
     * 字符串是否为空
     * @param s
     * @return
     */
    public static boolean isBlank(String s) {
        return s == null || "".equals(s);
    }
}

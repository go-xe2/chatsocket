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

    /**
     * 字符串为null或空时返回def
     * @param s
     * @param def
     * @return
     */
    public static String emptyDefault(String s, String def) {
        if (isBlank(s)) {
            return def;
        }
        return s;
    }

    /**
     * 字符串为null或空时返回空字符串
     * @param s
     * @return
     */
    public static String emptyDefault(String s) {
        return emptyDefault(s, "");
    }
}

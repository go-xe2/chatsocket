package com.mnyun.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String DateFormat(Date date, String format) {
        if (StringUtils.isBlank(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * 格式化日期为字符串
     * @param date
     * @return
     */
    public static String DateFormat(Date date) {
        return DateFormat(date, null);
    }
}

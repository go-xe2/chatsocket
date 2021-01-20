package com.mnyun.utils;

import android.content.Context;

/**
 * Created by chenyk on 2017/6/2.
 * 资源工具类
 * 功能：module中根据context动态获取application中对应资源文件
 */
public class ResourceUtil {
    /**
     * 获取布局资源
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getLayoutResId(Context context, String resName) {
        return getResId(context, "layout", resName);
    }

    /**
     * 获取字符串资源
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getStringResId(Context context, String resName) {
        return getResId(context, "string", resName);
    }

    /**
     * 获取Drawable资源
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getDrawableResId(Context context, String resName) {
        return getResId(context, "drawable", resName);
    }

    /**
     * 获取颜色资源
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getColorResId(Context context, String resName) {
        return getResId(context, "color", resName);
    }

    /**
     * 获取id文件中资源
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getIdRes(Context context, String resName) {
        return getResId(context, "id", resName);
    }

    /**
     * 获取数组资源
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getArrayResId(Context context, String resName) {
        return getResId(context, "array", resName);
    }

    /**
     * 获取style中资源
     *
     * @param context
     * @param resName
     * @return
     */
    public static int getStyleResId(Context context, String resName) {
        return getResId(context, "style", resName);
    }

    /**
     * 获取context中对应类型的资源id
     *
     * @param context
     * @param type
     * @param resName
     * @return
     */
    private static int getResId(Context context, String type, String resName) {
        return context.getResources().getIdentifier(resName, type, context.getPackageName());
    }
}
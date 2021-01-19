package com.mnyun.utils;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Huanglinqing
 * @blog: https://blog.csdn.net/huangliniqng
 * @date 2019/6/26
 * @desc 桌面角标显示方案 单例模式
 * <p>
 * 针对国内支持的Launcher分别开发，支持为：
 * ** 常见机型及是否支持
 * *** 华为 支持
 * 小米 支持
 * OPPO 可以支持但需要申请
 * VIVO 不支持
 * SAMSUNG 支持
 * MEIZU 不支持
 * google 8.0开始支持 显示一个点，长按点弹出数字
 * 其他杂牌机不支持
 * <p>
 * ------------------------------------------------------
 * English:
 * <p>
 * * Developed separately for the domestically supported Launcher, the support is:
 *   * ** Common models and support
 *   * *** Huawei Support
 *   * Xiaomi Support
 *   * OPPO can support but need to apply
 *   * VIVO does not support
 *   * SAMSUNG support
 *   * MEIZU does not support
 *   * google 8.0 starts to support display a point, long press the point to pop up the number
 *   * Other brands are not supported
 */

public class BadgeUtils {

    /**
     * 华为手机launcher主题
     * <p>
     * Huawei mobile phone launcher theme
     */
    private static final String HUWEI_LAUNCHERNAME = "com.huawei.android.launcher";

    /**
     * 小米手机launcher主题
     * <p>
     * MIUI phone launcher theme
     */
    private static final String MIUI_LAUNCHERNAME = "com.miui.home";

    /**
     * 摩托罗拉和宏达手机launcher主题
     * <p>
     * Motorola and HTC mobile phone launcher theme
     */
    private static final String MOTOANDHTC_LAUNCHERNAME = " com.android.launcher";

    /**
     * 三星手机launcher主题
     * <p>
     * Samsung mobile phone launcher theme
     */
    private static final String SAMSUNG_LAUNCHERNAME = "com.sec.android.app.launcher";

    /**
     * google 手机launcher主题
     * <p>
     * Google phone launcher theme
     */
    private static final String GOOGLE_LAUNCHERNAME = "com.google.android.apps.nexuslauncher";

    private volatile static BadgeUtils badgeUtils;

    /**
     * vivo手机厂商名字
     * Vivo mobile phone manufacturer name
     */
    private static final String VIVO_MANUFACTURER_NAME = "vivo";


    /**
     * OPPO 手机厂商名字
     * OPPO mobile phone manufacturer name
     */
    private static final String OPPO_MANUFACTURER_NANE = "OPPO";


    /**
     * 应用包名
     * Application package name
     */
    private static String mPackageName = null;


    /**
     * context 对象 这里建议传递Application的context 避免内存泄漏
     * It is recommended to pass the Application context to avoid memory leaks.
     */
    static Context mAppContext = null;

    /**
     * 主Activity的名字 为全路径 比如 com.***..**Activity
     * The name of the main Activity is the full path. For example, com.***..**Activity
     */
    private static String mLaunchActivityClass = null;

    /**
     * notification 对象 小米系统会默认设置 如果修改默认逻辑 必须获取notification对象
     * The millet system will default settings. If you modify the default logic,
     * you must get the notification object.
     */
    private static Notification notification = null;

    private BadgeUtils() {
    }

    public static BadgeUtils getInstance() {
        if (badgeUtils == null) {
            synchronized (BadgeUtils.class) {
                if (badgeUtils == null) {
                    badgeUtils = new BadgeUtils();
                }
            }
        }
        return badgeUtils;
    }

    /**
     * 初始化 init 建议放在Application中执行
     * @param packageName
     * @param launchActivityClass
     * @param appContext
     */
    public static void init(String packageName, String launchActivityClass, Context appContext) {
        mPackageName = packageName;
        mAppContext = appContext;
        mLaunchActivityClass = launchActivityClass;
    }

    public void init(String packageName, String launchActivityClass, Context appContext, Notification notifi) {
        mPackageName = packageName;
        mAppContext = appContext;
        mLaunchActivityClass = launchActivityClass;
        notification = notifi;
    }

    private static String getLauncherClassName() {
        ComponentName launchComponent = getLauncherComponentName(mAppContext);
        if (launchComponent == null) {
            return "";
        } else {
             return launchComponent.getClassName();
        }
    }

    private static ComponentName getLauncherComponentName(Context context) {
         Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
         if (launchIntent != null) {
            return launchIntent.getComponent();
         } else {
           return null;
         }
    }

    /**
     * 设置角标数字
     * Set the angle number
     *
     * @param badgeNumber @desc 数量
     */
    public static void setBadgeNumber(int badgeNumber) {

        if (badgeNumber >= 0) {
            switch (getLauncherClassName()) {
                case HUWEI_LAUNCHERNAME:
                    setBadgeNumberHuawei(badgeNumber);
                    break;
                case MIUI_LAUNCHERNAME:
                    setBadgeNumberMiui(badgeNumber);
                    break;
                case MOTOANDHTC_LAUNCHERNAME:
                    setBadgeNumberhtc(badgeNumber);
                    break;
                case SAMSUNG_LAUNCHERNAME:
                    setBadgeNumberSamsung(badgeNumber);
                    break;
                case GOOGLE_LAUNCHERNAME:
                    setBadgeNumberGoogle(badgeNumber);
                    break;
                default:
                    //再根据制造商去判断
                    String manufacturer = Build.MANUFACTURER;
                    switch (manufacturer) {
                        case VIVO_MANUFACTURER_NAME:
                            setBadgeNumberVivo(badgeNumber);
                            break;
                        case OPPO_MANUFACTURER_NANE:
                            setBadgeNumberOppo(badgeNumber);
                            break;
                        default:
                            break;
                    }
                    break;
            }
        } else {
            // TODO: 2019/6/27 donothing
        }
    }

    /**
     * Google手机设置角标 只支持8.0以上
     * Google phone settings corner Only supports 8.0 or above
     *
     * @param badgeNumber @desc 数量
     */
    private static void setBadgeNumberGoogle(int badgeNumber) {
        if (TextUtils.isEmpty(mPackageName)) {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", badgeNumber);
        intent.putExtra("badge_count_package_name", mPackageName);
        intent.putExtra("badge_count_class_name", mLaunchActivityClass);
        mAppContext.sendBroadcast(intent);

    }

    /**
     * htc和摩托罗拉设置角标
     * Htc and Motorola set the corners
     *
     * @param badgeNumber @desc 数量
     */
    private static void setBadgeNumberhtc(int badgeNumber) {
        if (TextUtils.isEmpty(mPackageName)) {
            return;
        }
        Intent intentNotification = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
        intentNotification.putExtra("com.htc.launcher.extra.COMPONENT", mPackageName);
        intentNotification.putExtra("com.htc.launcher.extra.COUNT", badgeNumber);
        mAppContext.sendBroadcast(intentNotification);
        Intent intentShortcut = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
        intentShortcut.putExtra("packagename", mPackageName);
        intentShortcut.putExtra("count", badgeNumber);
        mAppContext.sendBroadcast(intentShortcut);
    }


    /**
     * 小米手机设置角标
     * MIUI mobile phone settings corner
     * 当APP处于前台时，数字会自动清空
     * When the app is in the foreground, the number will be automatically cleared.
     *
     * @param badgeNumber @desc 数量
     */
    private static void setBadgeNumberMiui(int badgeNumber) {
        try {
            if (notification == null) {
                return;
            } else {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCoun", int.class);
                method.invoke(extraNotification, badgeNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 华为手机设置角标
     * 需添加权限<uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE" />
     * Huawei mobile phone settings corner
     *
     * @param badgeNumber @desc 数量
     */
    private static void setBadgeNumberHuawei(int badgeNumber) {

        if (TextUtils.isEmpty(mPackageName)) {
            return;
        }
        Bundle extra = new Bundle();
        extra.putString("package", mPackageName);
        extra.putString("class", mLaunchActivityClass);
        extra.putInt("badgenumber", badgeNumber);
        mAppContext.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                "change_badge", null, extra);
    }


    /**
     * 三星手机 设置角标
     * Samsung mobile phone set corner
     *
     * @param badgeNumber @desc 数量
     */
    private static void setBadgeNumberSamsung(int badgeNumber) {
        if (TextUtils.isEmpty(mPackageName)) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", badgeNumber);
        intent.putExtra("badge_count_package_name", mPackageName);
        intent.putExtra("badge_count_class_name", mLaunchActivityClass);
        mAppContext.sendBroadcast(intent);
    }

    /**
     * vivo 手机 设置角标
     * Vivo mobile phone set corner
     *
     * @param badgeNumber @desc 数量
     */
    public static void setBadgeNumberVivo(int badgeNumber) {
        try {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", mPackageName);
            String launchClassName = mAppContext.getPackageManager().getLaunchIntentForPackage(mAppContext.getPackageName()).getComponent().getClassName();
            intent.putExtra("className", launchClassName);
            intent.putExtra("notificationNum", badgeNumber);
            mAppContext.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务是否启动
     *
     * @param intent @desc 查找的服务
     * @return
     */
    public static boolean broadcastStarts(Intent intent) {
        PackageManager packageManager = mAppContext.getPackageManager();
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(intent, 0);
        return receivers != null && receivers.size() > 0;
    }


    /**
     * Oppo 手机 设置角标
     * OPPO mobile phone set corner
     *
     * @param badgeNumber @desc 数量
     */
    public static void setBadgeNumberOppo(int badgeNumber) {
        try {
            Intent intent = new Intent("com.oppo.unsettledevent");
            intent.putExtra("pakeageName", mAppContext.getPackageName());
            intent.putExtra("number", badgeNumber);
            intent.putExtra("upgradeNumber", badgeNumber);
            if (broadcastStarts(intent)) {
                mAppContext.sendBroadcast(intent);
            } else {
                try {
                    Bundle extras = new Bundle();
                    extras.putInt("app_badge_count", badgeNumber);
                    mAppContext.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, extras);
                } catch (Throwable th) {
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
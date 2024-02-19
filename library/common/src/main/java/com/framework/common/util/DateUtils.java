package com.framework.common.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author create by Zhengzelong on 2022/5/31
 * @Email : 171905184@qq.com
 * @Description :
 */
public class DateUtils {
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat FORMAT_YMD = new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat FORMAT_YMD_HM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat FORMAT_YMD_HMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * @param source "yyyy-MM-dd"
     * @return Timestamp
     */
    @NonNull
    public static long parse(@Nullable String source) {
        if (TextUtils.isEmpty(source)) {
            return -1L;
        }
        try {
            return FORMAT_YMD.parse(source).getTime();
        } catch (@NonNull ParseException e) {
            return -1L;
        }
    }

    /**
     * @param source "yyyy-MM-dd HH:mm"
     * @return Timestamp
     */
    @NonNull
    public static long parseHM(@Nullable String source) {
        if (TextUtils.isEmpty(source)) {
            return -1L;
        }
        try {
            return FORMAT_YMD_HM.parse(source).getTime();
        } catch (@NonNull ParseException e) {
            return -1L;
        }
    }

    /**
     * @param source "yyyy-MM-dd HH:mm:ss"
     * @return Timestamp
     */
    @NonNull
    public static long parseHMS(@Nullable String source) {
        if (TextUtils.isEmpty(source)) {
            return -1L;
        }
        try {
            return FORMAT_YMD_HMS.parse(source).getTime();
        } catch (@NonNull ParseException e) {
            return -1L;
        }
    }

    /**
     * @return "yyyy-MM-dd"
     */
    @NonNull
    public static String formatTimeMillis(long timeMillis) {
        return FORMAT_YMD.format(new Date(timeMillis));
    }

    /**
     * @return "yyyy-MM-dd hh:mm"
     */
    @NonNull
    public static String formatTimeMillisHM(long timeMillis) {
        return FORMAT_YMD_HM.format(new Date(timeMillis));
    }

    /**
     * @return "yyyy-MM-dd hh:mm:ss"
     */
    @NonNull
    public static String formatTimeMillisHMS(long timeMillis) {
        return FORMAT_YMD_HMS.format(new Date(timeMillis));
    }
}

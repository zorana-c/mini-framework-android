package com.framework.common.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @Author create by Zhengzelong on 2022/5/27
 * @Email : 171905184@qq.com
 * @Description :
 */
public class LettersUtils {

    /**
     * 获取首字母
     * <p>
     *
     * @param input 汉字字符串
     * @return 字母字符串
     */
    @Nullable
    public static String letterFirst(@NonNull String input) {
        try {
            if (TextUtils.isEmpty(input)) {
                return null;
            }
            final char[] units = input.trim().toCharArray();
            if (units == null || units.length == 0) {
                return null;
            }
            if (LettersUtils.checkFirst(units)) {
                return String.valueOf(units[0]).toUpperCase();
            }
            final HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);

            final String[] letters = PinyinHelper.toHanyuPinyinStringArray(units[0], format);
            if (letters == null || letters.length == 0) {
                return null;
            }
            return String.valueOf(letters[0].charAt(0));
        } catch (@NonNull BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean checkFirst(@NonNull char[] units) {
        final char letterFirst = units[0];
        return (letterFirst >= 'a' && letterFirst <= 'z') ||
                (letterFirst >= 'A' && letterFirst <= 'Z');
    }
}

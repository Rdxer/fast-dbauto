package com.rdxer.db.auto.utils;

import org.springframework.lang.Nullable;

import java.util.List;

public class StringEx {
    /**
     * 尾部
     *
     * @param string
     * @param suffixlist
     * @return
     */
    public static String trimTrailing(String string, List<String> suffixlist) {

        for (int i = 0; i < suffixlist.size(); i++) {
            String suf = suffixlist.get(i);
            if (string.endsWith(suf)) {
                string = trimTrailing(string, suf);
                i = -1;
            }
        }

        return string;
    }

    /**
     * 尾部
     *
     * @param string
     * @param suffix
     * @return
     */
    public static String trimTrailing(String string, String suffix) {
        if (string.endsWith(suffix)) {
            return string.substring(0, string.length() - suffix.length());
        }
        return string;
    }

    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str));
    }

    public static boolean isBalnk(String str) {
        return !hasText(str);
    }

    public static boolean hasText(@Nullable CharSequence str) {
        return (str != null && str.length() > 0 && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String block(String str, Integer keep) {
        return block(str, "*", null, keep);
    }

    public static String block(String str, String mask, Double percent, Integer keep) {

        if (isEmpty(str)) {
            return str;
        }

        int length = str.length();

        if (length == 1) {
            return mask;
        }

        double v;
        int vi = 1;
        if (percent != null) {
            v = length * percent;
            vi = (int) v;
        } else if (keep != null) {
            vi = keep;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            String substring = str.substring(i, i + 1);
            if (i < vi) {
                stringBuilder.append(substring);
            } else {
                stringBuilder.append(mask);
            }
        }

        return stringBuilder.toString();
    }

    public static String block(String str) {
        return block(str, "*", 0.5, null);
    }

    public static String replace(String str, int start, int end, String newValue) {
        String startStr = str.substring(0, start);
        String endStr = str.substring(end);

        String s = startStr + newValue + endStr;

        return s;
    }

    public static boolean equals(String str1, String str2) {
        if (str1 != null) {
            return str1.equals(str2);
        }
        if (str2 == null) {
            return true;
        }
        return false;
    }
}

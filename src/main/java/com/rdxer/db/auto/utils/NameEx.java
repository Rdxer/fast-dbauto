package com.rdxer.db.auto.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameEx {
    // 检测是否符合命名规则
    public static boolean check(String ss) {
        String regex = "^[a-zA-Z_]+[a-zA-Z0-9_]*$" ;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(ss);
        return m.find();
    }

    private final static String UNDERLINE = "_" ;

    /***
     * 下划线命名转为驼峰命名
     *  大驼峰
     * @param source
     *        下划线命名的字符串
     * @return new name
     */
    public static String underlineToLowerCamel(String source) {
        return underlineToCamel(source, false);
    }

    /***
     * 下划线命名转为驼峰命名
     *  小驼峰
     * @param source
     *        下划线命名的字符串
     * @return 新名称
     */
    public static String underlineToUpperCamel(String source) {
        return underlineToCamel(source, true);
    }

    public static String underlineToCamel(String source, boolean upperCamel) {
        StringBuilder result = new StringBuilder();
        String a[] = source.split(UNDERLINE);
        for (String s : a) {
            if (!source.contains(UNDERLINE)) {
                result.append(s);
                continue;
            }
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        if (StringEx.hasText(result)) {
            String start = result.substring(0, 1);
            String upperCase = start.toUpperCase();
            result.replace(0, 1, upperCase);
        }
        return result.toString();
    }

    /***
     * 驼峰命名转为下划线命名 驼峰命名的字符串
     *
     * @param para 参数
     * @return newname
     */
    public static String camelToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;//定位
//        if (!para.contains(UNDERLINE)) {
        for (int i = 0; i < para.length(); i++) {
            if (Character.isUpperCase(para.charAt(i)) && i != 0) {
                sb.insert(i + temp, UNDERLINE);
                temp += 1;
            }
        }
//        }
        return sb.toString().toLowerCase();
    }
}

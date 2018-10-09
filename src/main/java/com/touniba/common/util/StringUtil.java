package com.touniba.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: 张恒(多多筹)
 * @Description:
 * @Date: 2018/10/9 19:23
 * @modified By:
 */
public class StringUtil {

    /**
     * 判断多个参数是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(Object str) {
        return ("").equals(str) || str == null;
    }

    /**
     * 替换字符串，修复java.lang.String类的replaceAll方法时第一参数是字符串常量正则时(如："address".
     * replaceAll("dd","$");)的抛出异常：java.lang.StringIndexOutOfBoundsException:
     * String index out of range: 1的问题。
     *
     * @param strSc  需要进行替换的字符串
     * @param oldStr 源字符串
     * @param newStr 替换后的字符串
     * @return 替换后对应的字符串
     * @since 1.2
     */
    public static String replaceAll(String strSc, String oldStr, String newStr) {
        int i = -1;
        while ((i = strSc.indexOf(oldStr)) != -1) {
            strSc = new StringBuffer(strSc.substring(0, i)).append(newStr)
                    .append(strSc.substring(i + oldStr.length())).toString();
        }
        return strSc;
    }

    /**
     * 获取加密的手机号
     *
     * @param phoneNum
     * @return
     */
    public static String getEncryptMobile(String phoneNum) {
        if (!checkMobile(phoneNum)) {
            return phoneNum;
        }
        StringBuilder stringBuilder = new StringBuilder(phoneNum.substring(0, 3));
        stringBuilder.append("****");
        stringBuilder.append(phoneNum.substring(7));
        return stringBuilder.toString();
    }

    /**
     * 检查手机号
     *
     * @param phoneNum
     * @return
     */
    public static boolean checkMobile(String phoneNum) {
        Pattern p = Pattern.compile("^1[3|4|5|7|8]\\d{9}$");
        Matcher m = p.matcher(phoneNum);
        return m.matches();
    }

    /**
     * 是否有中文字符
     *
     * @param str
     * @return
     */
    public static boolean hasChineseChar(String str) {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }
}

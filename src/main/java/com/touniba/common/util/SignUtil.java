package com.touniba.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: 张恒(多多筹)
 * @Description:
 * @Date: 2018/10/11 10:30
 * @modified By:
 */
public class SignUtil {

    /**
     * 通过指定算法签名字符串
     *
     * @param data        Data to digest
     * @param charset     字符串转码为byte[]时使用的字符集
     * @param algorithm   目前其有效值为<code>MD5,SHA,SHA1,SHA-1,SHA-256,SHA-384,SHA-512</code>
     * @param toLowerCase 指定是否返回小写形式的十六进制字符串
     * @return String algorithm digest as a lowerCase hex string
     * @see Calculates the algorithm digest and returns the value as a hex string
     * @see If system dosen't support this <code>algorithm</code>, return "" not null
     * @see It will Calls {@link TradePortalUtil#getBytes(String str, String charset)}
     * @see 若系统不支持<code>charset</code>字符集,则按照系统默认字符集进行转换
     * @see 若系统不支持<code>algorithm</code>算法,则直接返回""空字符串
     * @see 另外,commons-codec.jar提供的DigestUtils.md5Hex(String data)与本方法getHexSign(data, "UTF-8", "MD5", false)效果相同
     */
    public static String getHexSign(String data, String charset, String algorithm, boolean toLowerCase) {
        char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        //Used to build output as Hex
        char[] DIGITS = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
        //get byte[] from {@link TradePortalUtil#getBytes(String, String)}
        byte[] dataBytes = getBytes(data, charset);
        byte[] algorithmData = null;
        try {
            //get an algorithm digest instance
            algorithmData = MessageDigest.getInstance(algorithm).digest(dataBytes);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException");
            return "";
        }
        char[] respData = new char[algorithmData.length << 1];
        //two characters form the hex value
        for (int i = 0, j = 0; i < algorithmData.length; i++) {
            respData[j++] = DIGITS[(0xF0 & algorithmData[i]) >>> 4];
            respData[j++] = DIGITS[0x0F & algorithmData[i]];
        }
        return new String(respData);
    }

    public static byte[] getBytes(String data, String charset) {
        data = (data == null ? "" : data);
        if (StringUtil.isEmpty(charset)) {
            return data.getBytes();
        }
        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            return data.getBytes();
        }
    }

}

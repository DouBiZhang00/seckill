package com.zjr.seckill.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @file: com.zjr.seckill.utils.MD5Util
 * @description: @TODO
 * @author: zjr
 * @date: 2021-12-26
 * @version: V1.0
 * @copyright: Copyright(c) 2021 Sdcncsi Co. Ltd. All rights reserved.
 */

public class MD5Util {

    static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static final char hexDigitsLower[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 对字符串 MD5 无盐值加密
     *
     * @param plainText 传入要加密的字符串
     * @return MD5加密后生成32位(小写字母 + 数字)字符串
     */
    public static String MD5Lower(String plainText) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 使用指定的字节更新摘要
            md.update(plainText.getBytes());

            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值。1 固定值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对字符串 自定义(简单拼接) MD5加盐加密
     * @param plainText 传入要加密的字符串
     * @param salt 传入盐值
     */
    public static String MD5Lower(String plainText, String salt) {
        return MD5Lower(plainText + salt);
    }

    public static void main(String[] args) {
        System.out.println(MD5Lower("300074zjr"));
    }
}
package com.zhan.qingzhiplatform.util;

import com.zhan.qingzhiplatform.exception.BusinessException;

import java.security.MessageDigest;

public class Md5Utils {

    /**
     * 计算数据的 MD5 值
     *
     * @param data 待计算的字节数组
     * @return 32位小写十六进制 MD5 字符串
     */
    public static String calculateMd5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new BusinessException("MD5计算失败");
        }
    }
}

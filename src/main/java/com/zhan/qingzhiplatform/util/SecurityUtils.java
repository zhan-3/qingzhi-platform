package com.zhan.qingzhiplatform.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityUtils {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 密码加密
     *
     * @param rawPassword 原密码
     * @return 加密后的密码
     */
    public static String encode (String rawPassword){
        return encoder.encode(rawPassword);
    }

    /**
     * 校验密码
     *
     * @param rawPassword 原密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches (String rawPassword, String encodedPassword){
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 密码格式校验: 至少8位, 包含数字和字母
     *
     * @param password 密码
     * @return 是否符合格式
     */
    public static boolean isValidPassword(String password){
        if (password == null || password.length() < 8){
            return false;
        }
        return password.matches(".*[a-zA-Z].*") && password.matches(".*\\d.*");
    }
}

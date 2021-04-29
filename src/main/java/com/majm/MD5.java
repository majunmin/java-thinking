package com.majm;

import java.security.MessageDigest;

public class MD5 {

    public static String encode(String s) {
        try {
            return toHex(MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"))).toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("md5 加密", e);
        }
    }

    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    private static String toHex(byte[] bytes) {
        StringBuilder str = new StringBuilder(bytes.length * 2);
        final int fifteen = 0x0f;// 15
        for (byte b : bytes) {// byte 为 32 位
            str.append(HEX_CHARS[(b >> 4) & fifteen]);// 25-28
            str.append(HEX_CHARS[b & fifteen]);//29-32
        }
        return str.toString();
    }
}
package com.cy.webviewagent.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by JLB6088 on 2017/10/17.
 */

final class DecodeUtils {

    private static final String AES = "AES";
    private static final String VIPARA = "0102030405060708";
    private static final String AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";
    private static final String SEED = "GIONEE2012061900";
    private static final String HEX = "0123456789ABCDEF";
    private static final String CHARSET = "UTF-8";

    DecodeUtils() {
    }

    static String encrypt(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes("UTF-8"));
        return toHex(result);
    }

    static String decrypt(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result, "UTF-8");
    }

    static byte[] getRawKey(byte[] seed) throws Exception {
        return seed;
    }

    static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec("0102030405060708".getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, skeySpec, zeroIv);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec("0102030405060708".getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, skeySpec, zeroIv);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];

        for(int i = 0; i < len; ++i) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }

        return result;
    }

    static String toHex(byte[] buf) {
        if(buf == null) {
            return "";
        } else {
            StringBuffer result = new StringBuffer(2 * buf.length);

            for(int i = 0; i < buf.length; ++i) {
                appendHex(result, buf[i]);
            }

            return result.toString();
        }
    }

    static void appendHex(StringBuffer sb, byte b) {
        sb.append("0123456789ABCDEF".charAt(b >> 4 & 15)).append("0123456789ABCDEF".charAt(b & 15));
    }

    static String get(String str) {
        if(str == null) {
            str = "";
        }

        String masterPassword = "GIONEE2012061900";

        try {
            String e = encrypt(masterPassword, str);
            return e;
        } catch (Exception var3) {
            var3.printStackTrace();
            return "";
        }
    }

    static String decrypt(String encrypted) {
        try {
            return decrypt("GIONEE2012061900", encrypted);
        } catch (Exception var2) {
            var2.printStackTrace();
            return "";
        }
    }

    static void main(String[] args) {
        try {
            String e = get("123655474174521");
            System.out.println("encrypted: " + e);
            String decrypt = decrypt(e);
            System.out.println("decrypt: " + decrypt);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}

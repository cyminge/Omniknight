package com.cy.omniknight.tools;

import android.content.Context;
import android.util.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/16
 *     desc  : utils about string
 * </pre>
 */
public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return whether the string is null or 0-length.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * Return whether the string is null or whitespace.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isTrimEmpty(final String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * Return whether the string is null or white space.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return whether string1 is equals to string2.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean equals(final CharSequence s1, final CharSequence s2) {
        if (s1 == s2) return true;
        int length;
        if (s1 != null && s2 != null && (length = s1.length()) == s2.length()) {
            if (s1 instanceof String && s2 instanceof String) {
                return s1.equals(s2);
            } else {
                for (int i = 0; i < length; i++) {
                    if (s1.charAt(i) != s2.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether string1 is equals to string2, ignoring case considerations..
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }

    /**
     * Return {@code ""} if string equals null.
     *
     * @param s The string.
     * @return {@code ""} if string equals null
     */
    public static String null2Length0(final String s) {
        return s == null ? "" : s;
    }

    /**
     * Return the length of string.
     *
     * @param s The string.
     * @return the length of string
     */
    public static int length(final CharSequence s) {
        return s == null ? 0 : s.length();
    }

    /**
     * Set the first letter of string upper.
     *
     * @param s The string.
     * @return the string with first letter upper.
     */
    public static String upperFirstLetter(final String s) {
        if (s == null || s.length() == 0) return "";
        if (!Character.isLowerCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * Set the first letter of string lower.
     *
     * @param s The string.
     * @return the string with first letter lower.
     */
    public static String lowerFirstLetter(final String s) {
        if (s == null || s.length() == 0) return "";
        if (!Character.isUpperCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * Reverse the string.
     *
     * @param s The string.
     * @return the reverse string.
     */
    public static String reverse(final String s) {
        if (s == null) return "";
        int len = s.length();
        if (len <= 1) return s;
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * Convert string to DBC.
     *
     * @param s The string.
     * @return the DBC string
     */
    public static String toDBC(final String s) {
        if (s == null || s.length() == 0) return "";
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * Convert string to SBC.
     *
     * @param s The string.
     * @return the SBC string
     */
    public static String toSBC(final String s) {
        if (s == null || s.length() == 0) return "";
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /* Convert a object to string in base64 */
    public static String objectToString(Serializable obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(
                    new Base64OutputStream(baos, android.util.Base64.NO_PADDING | android.util.Base64.NO_WRAP));
            oos.writeObject(obj);
            oos.close();
            return baos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* convert a base64 String to Object */
    public static Object stringToObject(String str) {
        try {
            return new ObjectInputStream(new Base64InputStream(
                    new ByteArrayInputStream(str.getBytes()), android.util.Base64.NO_PADDING
                    | android.util.Base64.NO_WRAP)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析转义字符为符号
     *
     * @param str
     * @return
     */
    public static String parseESC2Symbol(String str) {
        str = str.replace("&amp;", "&");
        str = str.replace("&lt;", "<");
        str = str.replace("&gt;", ">");
        str = str.replace("&quot;", "\"");
        str = str.replace("&nbsp;", " ");
        str = str.replace("&copy;", "©");
        str = str.replace("&reg;", "®");
        str = str.replace("&apos;", "'");
        return str;
    }

    /**
     * 获取带参数的字符串资源
     *
     * @param context
     * @param params
     * @return
     */
    public static String getResourceStringWithParam(Context context, int id, Object... params) {
        String formatString = context.getResources().getString(id);
        return String.format(formatString, params);
    }

    public static boolean compare(Integer integer1, Integer integer2) {
        boolean bRet = false;
        if (null == integer1) {
            if (null == integer2) {
                bRet = true;
            }
        }
        else {
            if (null != integer2) {
                bRet = (0 == integer1.compareTo(integer2));
            }
        }
        return bRet;
    }

    public static boolean compare(Long integer1, Long integer2) {
        boolean bRet = false;
        if (null == integer1) {
            if (null == integer2) {
                bRet = true;
            }
        }
        else {
            if (null != integer2) {
                bRet = (0 == integer1.compareTo(integer2));
            }
        }
        return bRet;
    }

    public static boolean compare(String str1, String str2) {
        boolean bRet = false;
        if (null == str1) {
            if (null == str2) {
                bRet = true;
            }
        }
        else {
            if (null != str2) {
                bRet = str1.equals(str2);
            }
        }
        return bRet;
    }

    /**
     * Judge parameter string is a numeric string or not
     *
     * @param string
     * @return
     */
    public static boolean isNumeric(String string) {
        for (int i = string.length(); --i >= 0;) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 解析html标签
     * 这个方法不正确，别使用
     *
     * @param str
     * @return
     */
    public static String parseLabel2Null(String str) {
        str = str.replace("^</?[a-z]+>$", "");
        return str;
    }

    /**
     * 匹配汉字数字字母
     *
     * @param str
     * @return
     */
    public static boolean isRegularMatch_Ch_num_Let(String str) {
        String regular = "^[\u4e00-\u9fa5a-zA-Z0-9]+$";
        Pattern p = Pattern.compile(regular);
        Matcher m = p.matcher(str);

        return m.matches();
    }

    /**
     * 匹配字母斜杠
     *
     * @param str
     * @return
     */
    public static boolean isRegularMatch_Let_sprit(String str) {
        String regular = "^</?[a-z]+>$";
        Pattern p = Pattern.compile(regular);
        Matcher m = p.matcher(str);

        return m.matches();
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainGBK(String str) {
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5a]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        System.out.println("共有 " + count + "个 ");
        return m.matches();
    }

    /**
     * 特殊字符过滤
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String stringFilter(String str) throws PatternSyntaxException {
//        String regEx = "[/\\:*%?<>|\"\n\t]";
        String regEx = "[/\\%<>|\"\n\t]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }
}

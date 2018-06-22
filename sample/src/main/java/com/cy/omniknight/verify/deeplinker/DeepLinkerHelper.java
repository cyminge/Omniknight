package com.cy.omniknight.verify.deeplinker;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

public final class DeepLinkerHelper {

    /**
     * 创建deeplink
     * @return
     */
    public static String createDeeplink(String packageName, String targetIntentString) {
        String deeplink = "gnad://"+ encodeString(packageName) +"?it="+targetIntentString;
        Log.e("cyTest", "deeplink==>> "+deeplink);
        return deeplink;
    }

    /**
     * 创建要跳转的Activity的intent
     * @return
     */
    public static String createTargetIntent11() {
        Intent intent = new Intent();
        intent.setAction("gn.com.android.gamehall.action.external.DETAIL");
        intent.putExtra("from", "gn_ad");
        intent.putExtra("source", "gn_ad");
        intent.putExtra("packageName", "com.tencent.tmgp");
        intent.putExtra("gameId", "6029");
        intent.putExtra("target_packagename", "com.tencent.tmgp");
        return intent2String(intent);
    }

    public static String createTargetIntent22() {
        String str = "gngamehall://GameDetailView?packageName=com.tencent.tmgp&gameId=6029&from=hahaha";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
        return intent2String(intent);
    }

    /**
     * 将intent转为string并进行加密
     * @param intent
     * @return
     */
    public static String intent2String(Intent intent) {
        if (intent == null) {
            return null;
        }
        String intentUri = intent.toUri(Intent.URI_INTENT_SCHEME);
        Log.e("cyTest", "intentUri==>> "+intentUri);
        return encodeString(intentUri);
    }

    public static String encodeString(String intentStr) {

        if (TextUtils.isEmpty(intentStr)) {
            return intentStr;
        }

        intentStr = encrypt(intentStr);
        String encodeStr = new String(Base64.encode(intentStr.getBytes(), Base64.URL_SAFE));// NOSONAR
        encodeStr = encodeStr.replaceAll("\n", "");
        return encodeStr;
    }

    private static String encrypt(String content) {
        try {
            char[] chars = content.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                chars[i] = (char) (chars[i] ^ 15);
            }
            return new String(chars);
        } catch (Exception e) {
            return null;
        }
    }

    private static String decrypt(String content) {
        try {
            char[] chars = content.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                chars[i] = (char) (15 ^ chars[i]);
            }
            return new String(chars);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decodeString(String encodeStr) {
        if (TextUtils.isEmpty(encodeStr)) {
            return encodeStr;
        }
        String decodeStr = new String(Base64.decode(encodeStr.getBytes(), Base64.URL_SAFE));// NOSONAR
        decodeStr = decrypt(decodeStr);
        return decodeStr;
    }

}

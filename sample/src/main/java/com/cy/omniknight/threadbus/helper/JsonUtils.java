package com.cy.omniknight.threadbus.helper;

/**
 * Created by JLB6088 on 2017/7/6.
 */

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public class JsonUtils {
    private static final String GAMEHALL_FAIL = "gamehall_fail";

    public static String postData(String actionUrl) {
        HttpURLConnection conn = null;
        OutputStream op = null;
        try {
            String finalUrl = actionUrl;
            URL url = new URL(finalUrl);
            String datas = "client_pkg=gn.com.android.gamehall.test111&imei=EDCDE1F6A83F67E84A6AAF5E1F66E9A2&brand=GiONEE&sp=GN8001_1.7.3.a1_5.0.16_Android5.1_1080*1920_N06000_null4g_EDCDE1F6A83F67E84A6AAF5E1F66E9A2&version=1.7.3.a1";
            byte[] data = datas.getBytes("utf-8");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            conn.setRequestProperty("Accept-Encoding", "gzip");

            op = conn.getOutputStream();
            op.write(data);

            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                return getConnectResult(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("cyTest", "postData_error:"+e.getMessage());
        } finally {
            try {
                if (op != null) {
                    op.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
            }
        }
        return GAMEHALL_FAIL;
    }


    private static String getConnectResult(URLConnection conn) {
        InputStream finalInputStream = null;
        try {
            InputStream is = conn.getInputStream();
            if ("gzip".equals(conn.getContentEncoding())) {
                finalInputStream = new BufferedInputStream(new GZIPInputStream(is));
            } else {
                finalInputStream = is;
            }
            StringBuilder res = new StringBuilder();
            int ch;
            while ((ch = finalInputStream.read()) != -1) {
                res.append((char) ch);
            }
            return res.toString();
        } catch (Exception e) {
            Log.e("cyTest", "getConnectResult_error:"+e.getMessage());
        } finally {
            try {
                if (finalInputStream != null) {
                    finalInputStream.close();
                }
            } catch (IOException e) {
            }
        }
        return GAMEHALL_FAIL;
    }

}


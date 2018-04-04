package com.cy.omniknight.tools.httpsutil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import android.util.Log;

/**
 * https ssl 主机名验证
 * @author JLB6088
 *
 */
public class MyHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String urlHostName, SSLSession session) {
        Log.w("cyTest", "Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
        return true;
    }

}

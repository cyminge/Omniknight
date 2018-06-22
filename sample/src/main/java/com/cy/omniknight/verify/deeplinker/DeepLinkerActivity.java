package com.cy.omniknight.verify.deeplinker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.cy.omniknight.BaseActivity;

import java.net.URISyntaxException;

public class DeepLinkerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String deeplink = createDeeplink();
        parseDeeplink(deeplink);
    }

    private String createDeeplink() {
        String packageName = "gn.com.android.gamehall";
        String targetIntentString = DeepLinkerHelper.createTargetIntent11();
        return DeepLinkerHelper.createDeeplink(packageName, targetIntentString);
    }

    private void parseDeeplink(String deeplink) {
        String encryptedIntentString;
        String encryptedPackageName;
        String scheme;
        try {
            Intent intent = Intent.parseUri(deeplink, Intent.URI_INTENT_SCHEME);
            scheme = intent.getScheme();
            encryptedPackageName = intent.getData().getHost();
            encryptedIntentString = intent.getData().getQueryParameter("it");

            Log.e("cyTest", "scheme--> "+scheme);
            Log.e("cyTest", "encryptedPackageName--> "+encryptedPackageName);
            Log.e("cyTest", "encryptedIntentString--> "+encryptedIntentString);
        } catch (URISyntaxException e) {
            Log.e("cyTest", "deeplink11 格式不对");
            return;
        }

        launchActivity(encryptedIntentString, encryptedPackageName);
    }

    private void launchActivity(String intentString, String packageName) {
        String decryptedIntentString = DeepLinkerHelper.decodeString(intentString);
        String decryptedPackageName = DeepLinkerHelper.decodeString(packageName);

        Log.e("cyTest", "decryptedIntentString--> "+decryptedIntentString);
        Log.e("cyTest", "decryptedPackageName--> "+decryptedPackageName);
        try {
            Intent intent22 = Intent.parseUri(decryptedIntentString, Intent.URI_INTENT_SCHEME);
            startActivity(intent22);
        } catch (URISyntaxException e) {
            Log.e("cyTest", "deeplink22 格式不对");
        } catch (Exception e) {
            try {
                // 如果要打开的对应的应用页面不存在则打开对应应用的启动页
                PackageManager pm = getPackageManager();
                Intent targetIntent = pm.getLaunchIntentForPackage(decryptedPackageName);
                startActivity(targetIntent);
            } catch (Exception ee) {
                Log.e("cyTest", "要打开的应用不存在");
            }
        }
    }
}

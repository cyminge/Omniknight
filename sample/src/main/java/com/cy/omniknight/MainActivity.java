package com.cy.omniknight;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.cy.omniknight.verify.animation.AnimationActivity;
import com.cy.omniknight.verify.deeplinker.DeepLinkerActivity;
import com.cy.omniknight.verify.dialog.Activity_TestDialogWithContext;
import com.cy.omniknight.verify.fontsize.FontSizeChangerActivity;
import com.cy.omniknight.verify.picdecode.Activity_PicDecode;
import com.cy.omniknight.verify.rgb888.Activity_TestRGB8888;
import com.cy.omniknight.verify.touchevent.TouchEventTestActivity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Log.e("cyTest", "-->getFilesDir = " + this.getFilesDir());
//        Log.e("cyTest", "-->getCacheDir = " + this.getCacheDir());
//        Log.e("cyTest", "-->getExternalCacheDir = " + this.getExternalCacheDir());
//        Log.e("cyTest", "-->getExternalFilesDir = " + this.getExternalFilesDir("cy"));
//        Log.e("cyTest", "-->Environment.getExternalStorageState = " + Environment.getExternalStorageState());
//        Log.e("cyTest", "-->Environment.getDataDirectory = " + Environment.getDataDirectory());
//        Log.e("cyTest",
//                "-->Environment.getDownloadCacheDirectory = " + Environment.getDownloadCacheDirectory());
//        Log.e("cyTest",
//                "-->Environment.getExternalStorageDirectory = " + Environment.getExternalStorageDirectory());
//        Log.e("cyTest", "-->Environment.getRootDirectory = " + Environment.getRootDirectory());
//        for (File file : this.getExternalCacheDirs()) {
//            Log.e("cyTest", "-->getExternalCacheDirs = " + file.getPath());
//        }

//        EditText et = new EditText(this);
//        try {
//            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
//            field.setAccessible(true);
//            field.set(et, Color.rgb(0, 0, 0));
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
////            Log.e("cyTest", "ignored");
//        }


    }

    public static void setDataConnectionState(Context cxt, boolean state) {
        Log.e("cyTest", "重启数据连接： state="+state);
        ConnectivityManager connectivityManager = null;
        Class<?> connectivityManagerClz = null;
        try {
            connectivityManager = (ConnectivityManager) cxt.getSystemService("connectivity");
            connectivityManagerClz = connectivityManager.getClass();
            Method method = connectivityManagerClz.getMethod("setMobileDataEnabled", new Class[] { boolean.class });
            method.invoke(connectivityManager, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> sVold = new ArrayList<String>();
    private static String DefPath = "/mnt/sdcard";

    private static void readVoldFile() {
        try {
            sVold.add(Environment.getExternalStorageDirectory().getPath());
            if (!sVold.contains(DefPath))
                sVold.add(DefPath);
        } catch (Exception e) {
        }

        try {
            File mountFile = new File("/proc/mounts");
            if(mountFile.exists()){
                Scanner scanner = new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("/dev/block/vold/")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[1];

                        // don't add the default mount path
                        // it's already in the list.
                        if (!sVold.contains(element))
                            sVold.add(element);
                    }
                }
            }
        } catch (Exception e) {
        }

//        try {
//            Scanner scanner = new Scanner(new File("/system/etc/vold.fstab"));
//            while (scanner.hasNext()) {
//                String line = scanner.nextLine();
//                if (line.startsWith("dev_mount")) {
//                    String[] lineElements = line.split(" ");
//                    String element = lineElements[2];
//
//                    if (element.contains(":"))
//                        element = element.substring(0, element.indexOf(":"));
//
//                    if (element.contains("usb"))
//                        continue;
//
//                    // don't add the default vold path
//                    // it's already in the list.
//                    if (!sVold.contains(element))
//                        sVold.add(element);
//                }
//            }
//        } catch (Exception e) {
//        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn1 :
                intent.setClass(this, FontSizeChangerActivity.class);
                startActivity(intent);
                break;
            case R.id.btn2 :
                intent.setClass(this, TouchEventTestActivity.class);
                startActivity(intent);
                break;
            case R.id.btn3 :
                intent.setClass(this, Activity_TestDialogWithContext.class);
                startActivity(intent);
                break;
            case R.id.btn4 :
                intent.setClass(this, AnimationActivity.class);
                startActivity(intent);
                break;
            case R.id.btn5 :
                intent.setClass(this, Activity_PicDecode.class);
                startActivity(intent);
                break;
            case R.id.btn6 :
                intent.setClass(this, Activity_TestRGB8888.class);
                startActivity(intent);
                break;
            case R.id.btn7 :
                intent.setClass(this, DeepLinkerActivity.class);
                startActivity(intent);
                break;
        }

    }

    public boolean isAppForeground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(null == am.getRunningTasks(1)) {
            return false;
        }
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
        String topAppPackage = foregroundTaskInfo.topActivity.getPackageName();
        Log.e("cyTeset", "--------------------topAppPackage:"+topAppPackage);
        if (topAppPackage != null && topAppPackage.contentEquals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        setIntent(intent);
//        Log.e("cyTest", "--> MainActivity.onNewIntent from :"+getIntent().getStringExtra("from"));

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Log.e("cyTest", "Activity_A.onWindowFocusChanged");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        Log.e("cyTest", "Activity_A.onAttachedToWindow");
    }

//    @Override
//    public String getCallingPackage() {
//    	return super.getCallingPackage();
//    }

    @Override
    protected void onPause() {
//        Log.e("cyTest", "Activity_A.onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
//        Log.e("cyTest", "Activity_A.onStop");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        Log.e("cyTest", "Activity_A.onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt("aa", 11);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        Log.e("cyTest", "Activity_A.onDetachedFromWindow");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("cyTest", "MainActivity.onDestroy");
    }
}

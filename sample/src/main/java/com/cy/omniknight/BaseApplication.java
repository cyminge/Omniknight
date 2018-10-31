package com.cy.omniknight;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.cy.omniknight.tracer.Tracer;

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		
		if (Build.VERSION.SDK_INT < 23) {
            int storage = checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int state = checkCallingOrSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            if (storage != PackageManager.PERMISSION_GRANTED || state != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "無權限", Toast.LENGTH_LONG).show();
            }
        }

        WatchDog.init(this);
	}

    @Override
    protected void attachBaseContext(Context base) {
        Log.e(TAG, "attachBaseContext");
        super.attachBaseContext(base);
        installMultiDexIfNeed(base);
    }

    private void installMultiDexIfNeed(Context base) {
        if (Build.VERSION.SDK_INT >= 21) {
            return;
        }
        try {
            MultiDex.install(base);
        } catch (Exception e) {
            Tracer.w(TAG, e.getMessage());
        }
    }
	
}

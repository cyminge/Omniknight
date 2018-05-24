package com.cy.omniknight;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import com.cy.omniknight.socket.SPushService;
import com.cy.omniknight.tools.receiver.ReceiverManager;
import com.cy.omniknight.tracer.Tracer;
import com.gionee.threadbus.ThreadBus;

public class BaseApplication extends Application {

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

        ThreadBus.init(this); // 线程池入口函数
		Tracer.init(this);
        ReceiverManager.getInstance().init(this);
        SPushService.startPushServer(this);
	}
	
}

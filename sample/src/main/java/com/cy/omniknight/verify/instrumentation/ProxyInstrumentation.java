package com.cy.omniknight.verify.instrumentation;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

public class ProxyInstrumentation extends Instrumentation {
	private Context context;
	private Instrumentation origInstrumentation;
	private ComponentName cnMain;
	private static boolean startFromOgc;

	public ProxyInstrumentation(Context context, Instrumentation origInstrumentation) {
		this.context = context;
		this.origInstrumentation = origInstrumentation;
	}

	public Activity newActivity(ClassLoader cl, String className, Intent intent)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Log.d("ProxyInstrumentation", "-->newActivity intent 11 = " + intent);
		obtainStartInfo(this.context, intent);
		return this.origInstrumentation.newActivity(cl, className, intent);
	}
	
	@Override
	public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application,
			Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id,
			Object lastNonConfigurationInstance) throws InstantiationException, IllegalAccessException {
		Log.d("ProxyInstrumentation", "-->newActivity intent 22 = " + intent);
		return super.newActivity(clazz, context, token, application, intent, info, title, parent, id,
				lastNonConfigurationInstance);
	}

	public void callActivityOnNewIntent(Activity activity, Intent intent) {
		Log.d("ProxyInstrumentation", "ProxyInstrumentation::callActivityOnNewIntent intent = " + intent);
		obtainStartInfo(activity, intent);
		this.origInstrumentation.callActivityOnNewIntent(activity, intent);
	}

	private void obtainStartInfo(Context context, Intent intent) {
		if (this.cnMain == null) {
			this.cnMain = getMainComponent(context);
			Log.d("ProxyInstrumentation", "obtainStartInfo cnMain = " + this.cnMain);
		}
		Log.d("ProxyInstrumentation", "obtainStartInfo intent.getComponent() = "
				+ intent.getComponent());
		if ((this.cnMain != null) && (this.cnMain.equals(intent.getComponent()))) {
			startFromOgc = intent.getBooleanExtra("key", false);
//			startFromOgc = (intent.getPackage() == "com.example.androidttt");
			Log.d("ProxyInstrumentation", "obtainStartInfo startFromOgc = "
					+ startFromOgc);
		}
	}

	private ComponentName getMainComponent(Context who) {
		ComponentName result = null;
		PackageManager pm = who.getPackageManager();
		Intent temp = pm.getLaunchIntentForPackage(who.getPackageName());
		if (temp != null) {
			result = temp.getComponent();
		}
		return result;
	}

	public static boolean startFromOgc() {
		return startFromOgc;
	}
}

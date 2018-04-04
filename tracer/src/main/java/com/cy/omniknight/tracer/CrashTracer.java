package com.cy.omniknight.tracer;

import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import com.cy.omniknight.tracer.export.IOnShowExceptionInfo;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Timer;
import java.util.TimerTask;

public class CrashTracer implements UncaughtExceptionHandler {

	private Context mContext;

	private boolean reportDone = false;
	private boolean confirmed = false;

	UncaughtExceptionHandler mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();

	private IOnShowExceptionInfo onShowInfo = null; // 是否提示用户出现异常

	Thread mUncaughtException = null;
	Throwable mThrowable = null;

	// 设置判断是否提示用户出现异常的判断
	public void setShowInfo(IOnShowExceptionInfo showInfo) {
		onShowInfo = showInfo;
	}

	public void onEvent(ReportDoneEvent report) {

		// Tracer.d("CrashTracer", "on report done " + reportGson.error);
//		EventBus.getDefault().unregister(CrashTracer.this, ReportDoneEvent.class);
		reportDone = true;
		finishApplication();
	}

	private void finishApplication() {
		if (confirmed == true || reportDone == true) {
			try {
				Tracer.d("CrashTracer", "kill process");
				android.os.Process.killProcess(android.os.Process.myPid());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				mDefaultUEH.uncaughtException(mUncaughtException, mThrowable);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		Boolean reboot = true;

		if (throwable instanceof ExceptionCaughted) {
			ExceptionCaughted exception = (ExceptionCaughted) throwable;
			if (ExceptionCaughted.REBOOT != exception.mOperation) {
				reboot = false;
			}
		}

		mUncaughtException = thread;
		mThrowable = throwable;

		/* start time task */
		if (reboot) {
			Timer timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				public void run() {
					reportDone = true;
					finishApplication();
				}
			};
			timer.schedule(timerTask, 12000);
		}

//		EventBus.getDefault().unregister(CrashTracer.this, ReportDoneEvent.class);
//		EventBus.getDefault().register(CrashTracer.this, ReportDoneEvent.class);
//
//		boolean report = Tracer.printException(thread, throwable, Tracer.EXCEP_TYPE.UNCAUGHTED);
//		Tracer.persisteLog(true); // 打印异常时会自动上报
//
//		if (!reboot) {
//			return;
//		}
//
//		if (null != onShowInfo && !onShowInfo.isShownInfo()) {
//			confirmed = true;
//			if (!report) { // 不需要等待上报完成
//				reportDone = true;
//				finishApplication();
//			}
//			return; // 不提示用户出现异常
//		}
//
//		new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				AlertDialog dialog;
//				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
////				builder.setTitle(mContext.getResources().getString(R.string.crash_tracer_tip));
//				builder.setTitle("Tips");
//				builder.setCancelable(false);
//				builder.setMessage("Sorry, uncaught exception has accored");
//				builder.setNeutralButton(mContext.getString(R.string.ok),
//						new OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// ((Activity) mContext).finish();
//
////								Intent intent = new Intent(mContext, LoadingActivity.class);
////								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////								mContext.startActivity(intent);
//
//								dialog.dismiss();
//								confirmed = true;
//								finishApplication();
//							}
//						});
//
//				dialog = builder.create();
//				dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
//				dialog.show();
//
//				// new AlertDialog.Builder(mContext)
//				// .setTitle("��ʾ")
//				// .setCancelable(false)
//				// .setMessage(mContext.getString(R.string.tip_app_crash))
//				// .setNeutralButton(mContext.getString(R.string.str_ok),
//				// new OnClickListener() {
//				// @Override
//				// public void onClick(DialogInterface dialog,
//				// int which) {
//				// // ((Activity) mContext).finish();
//				// confirmed = true;
//				// finishApplication();
//				// }
//				// }).create().show();
//				Looper.loop();
//			}
//		}.start();

		/*
		 * Intent intent = new Intent(mContext, CrashNotifyActivity.class);
		 * mContext.startActivity(intent);
		 */

		// mDefaultUEH.uncaughtException(thread, throwable);
	}

	public void init(Context context) {
		mContext = context.getApplicationContext();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/* singleton for crash tracer */
	private static CrashTracer instance = null;

	private CrashTracer() {
	}

	public static CrashTracer getInstance() {
		if (instance == null) {
			instance = new CrashTracer();
		}
		return instance;
	}

	private void notifyUserFriendly() {
		WindowManager windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		wmParams.gravity = Gravity.CENTER;
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
	}
}

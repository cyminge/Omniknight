package com.cy.omniknight.tools.device;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

public class Notifier {
	private static Context mContext = null;
	private static final String NOTIFY_PREF = "notify_pref";

	private static Vibrator mVibrator;
	private static Ringtone mRingtone;

	/* configuration for chat */
	private static final String TAG_CHAT_VIBRATE = "chat_vibrate";
	private static final String TAG_CHAT_TONE = "chat_tone";
	private static boolean vibrateForChat = true;
	private static boolean toneForChat = true;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void notificationForChat(int iconId, CharSequence ticker, CharSequence title, CharSequence content, Class<?> targetClazz) {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent notificationIntent = new Intent(mContext, targetClazz);

			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

			PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

			Notification notification = new Notification.Builder(mContext)
					.setSmallIcon(iconId)
					.setTicker(ticker)
					.setContentTitle(title)
					.setContentText(content)
					.setContentIntent(contentIntent)
					// .setNumber(count) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
					.build();

			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.flags |= Notification.FLAG_NO_CLEAR;

			notificationManager.notify(0, notification);
		} else {
			
		}
	}
	
	/**
	 * 兼容模式
	 * @param iconId
	 * @param ticker
	 * @param title
	 * @param content
	 * @param targetClazz
	 */
	public static void sendNotification(int iconId, CharSequence ticker, CharSequence title, CharSequence content, Class<?> targetClazz) {

		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(mContext, targetClazz);

		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

		builder.setSmallIcon(iconId)
				.setTicker(ticker)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setContentText(content)
				.setContentIntent(contentIntent);

		notificationManager.notify(0, builder.build());
	}

	
	public static void notifyForChat() {
		if (vibrateForChat) {
			vibrate();
		}
		if (toneForChat) {
			playTone();
		}
	}

	public static void saveConfigForChat(boolean vibrate, boolean tone) {
		vibrateForChat = vibrate;
		toneForChat = tone;
		SharedPreferences preferences = getPreferences();
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.remove(TAG_CHAT_VIBRATE);
			editor.remove(TAG_CHAT_TONE);

			editor.putBoolean(TAG_CHAT_VIBRATE, vibrate);
			editor.putBoolean(TAG_CHAT_TONE, tone);

			editor.commit();
		}
	}

	private static void restoreConfig() {
		SharedPreferences preferences = getPreferences();
		if (preferences != null) {
			vibrateForChat = preferences.getBoolean(TAG_CHAT_VIBRATE, true);
			toneForChat = preferences.getBoolean(TAG_CHAT_TONE, true);
		}
	}

	private static void vibrate() {
		if (mVibrator != null)
			mVibrator.vibrate(new long[] { 100, 10, 100, 1000 }, -1);
	}

	private static void playTone() {
		if (mRingtone != null) {
			mRingtone.play();
		}
	}

	public static void initial(Context context) {
		mContext = context;
		/* restore configuration */
		restoreConfig();

		/* initial vibrate */
		mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);

		/* initial ringtone */
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mRingtone = RingtoneManager.getRingtone(context, notification);

	}

	private static SharedPreferences getPreferences() {
		SharedPreferences preferences = null;
		if (mContext != null) {
			preferences = mContext.getSharedPreferences(NOTIFY_PREF, Context.MODE_PRIVATE);
		}
		return preferences;
	}

}

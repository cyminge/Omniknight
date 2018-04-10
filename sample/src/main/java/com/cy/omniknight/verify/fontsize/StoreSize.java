package com.cy.omniknight.verify.fontsize;

import android.content.Context;
import android.content.SharedPreferences;

public class StoreSize {

	private static boolean isDone = false;
	private static float size;
	private static float currFontScale;

	public static float getCurrFontScale(Context context) {
		if (0 == currFontScale) {
			currFontScale = loadPreferenceFloat(context, "currFontScale");
		}
		return currFontScale;
	}

	public static void setCurrFontScale(Context context, float currFontScale) {
		StoreSize.currFontScale = currFontScale;
		persistentPreferenceFloat(context, "currFontScale", currFontScale);
	}

	public static boolean isDone(Context context) {
		return isDone;
	}

	public static void setDone(Context context, boolean isDone) {
		StoreSize.isDone = isDone;
		persistentPreferenceBoolean(context, "isDone", isDone);
	}

	public static float getSize() {
		return size;
	}

	public static void setSize(Context context, float size) {
		if (isDone(context)) {
			return;
		}
		setDone(context, true);
		StoreSize.size = size;
		persistentPreferenceFloat(context, "size", size);
	}

	/**
	 * 加载preference信息
	 */
	public static float loadPreferenceFloat(Context context, String key) {
		float value = -1;
		SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if (preferences != null) {
			value = preferences.getFloat(key, 0);
		}
		return value;
	}

	/**
	 * 存储preference信息
	 */
	public static void persistentPreferenceFloat(Context context, String key, float value) {
		SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	/**
	 * 加载preference信息
	 */
	public static int loadPreferenceInt(Context context, String key) {
		int value = -1;
		SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if (preferences != null) {
			value = preferences.getInt(key, 0);
		}
		return value;
	}

	/**
	 * 存储preference信息
	 */
	public static void persistentPreferenceInt(Context context, String key, int value) {
		SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 加载preference信息
	 */
	public static boolean loadPreferenceBoolean(Context context, String key) {
		boolean value = false;
		SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if (preferences != null) {
			value = preferences.getBoolean(key, false);
		}
		return value;
	}

	/**
	 * 存储preference信息
	 */
	public static void persistentPreferenceBoolean(Context context, String key, boolean isLogin) {
		SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, isLogin);
		editor.commit();
	}
}

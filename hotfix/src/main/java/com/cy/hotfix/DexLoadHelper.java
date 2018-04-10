package com.cy.hotfix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.text.TextUtils;

@SuppressLint("NewApi")
public class DexLoadHelper {
	public static final String CHANNEL_ID = "20004a";
	public static final String SDK_VERSION = "3.2.9";
	public static final String SDK_VERSION_CODE = "1";
	public static final String ASDK_APPID = "21";

	private Context mContext;
	private SharedPreferences mPreferences;

	public DexLoadHelper(Context context) {
		mContext = context;
		mPreferences = mContext.getSharedPreferences(DexSpInfo.DEX_LOAD_INFO, Context.MODE_PRIVATE);
	}

	public void checkDexUpdate() {
		if (!isDexNeedUpdate()) {
			return;
		}

		requestDex();
	}

	private void requestDex() {

	}

	private boolean isDexNeedUpdate() {
		boolean update = mPreferences.getBoolean(DexSpInfo.DEX_UPDATE, false);
		if (!update) {
			return false;
		}

		long last_time = mPreferences.getLong(DexSpInfo.DEX_LOAD_TIME, 0);
		if (last_time == 0) {
			return true;
		}

		long interval_time = mPreferences.getLong(DexSpInfo.DEX_LOAD_INTERVAL, 86400000);
		if (System.currentTimeMillis() - last_time >= interval_time) {
			return true;
		} else {
			return false;
		}
	}

	public void saveDexUpdateInfo(String jsonStr) {
		if (TextUtils.isEmpty(jsonStr)) {
			return;
		}

		Editor editor = mPreferences.edit();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			editor.putLong(DexSpInfo.DEX_LOAD_INTERVAL, jsonObject.getLong("request_interval") * 1000);
			if (jsonObject.getInt("need_update") == 1) {
				editor.putBoolean(DexSpInfo.DEX_NEED_UPDATE, true);
				editor.putString(DexSpInfo.DEX_URL, jsonObject.getString("url"));
				editor.putString(DexSpInfo.DEX_MD5, jsonObject.getString("md5"));
				editor.putString(DexSpInfo.DEX_UPDATE_VERSION, jsonObject.getString(DexSpInfo.DEX_SVR));
			} else {
				editor.putBoolean(DexSpInfo.DEX_NEED_UPDATE, false);
			}
			editor.putLong(DexSpInfo.DEX_LOAD_TIME, System.currentTimeMillis());
			editor.apply();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void moveDexToTarget() {
		File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator + "cyDexTemp");
		if (!tempFile.exists()) {
			return;
		}

		File dexFile = new File(mContext.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath()
				+ HotFixManager.DEX_FILE_PATH);
		if (dexFile.exists()) {
			if (!dexFile.delete()) {
				return;
			}
		}
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(tempFile);
			fos = new FileOutputStream(dexFile);
			byte[] buffer = new byte[1024];
			int i = -1;
			while ((i = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, i);
			}
			if (HotFixManager.INSTANCE.isDexCorrect()) {
				// 如果没有问题的话直接将本地版本升级
				String newVersion = mPreferences.getString(DexSpInfo.DEX_UPDATE_VERSION, SDK_VERSION_CODE);
				mPreferences.edit().putString(DexSpInfo.DEX_VERSION, newVersion).apply();
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void release() {
		mContext = null;
		mPreferences = null;
	}

}

package com.cy.hotfix;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import dalvik.system.DexClassLoader;

public enum HotFixManager {
	INSTANCE;

	private HotFixManager() {
	}

	private static final String TAG = "HotFixManager";

	private Context mContext;
	public boolean mIsInitializeDone = false;

	private boolean mIsDexCorrect;
	public static final String DEX_FILE_PATH = File.separator + "cydex.jar";
	
	private DexLoadHelper mDexLoadHelper;

	public static boolean init(Context context) {
		context = context.getApplicationContext();
		return INSTANCE.initialize(context);
	}

	public static void deInit(Context context) {
		context = context.getApplicationContext();
		INSTANCE.deinitialize(context);
	}

	public static Context getContext() {
		return INSTANCE.mContext;
	}

	/**
	 * 初始化函数，app启动时调用
	 * 
	 * @param context
	 * @return
	 */
	private boolean initialize(Context context) {
		if (mIsInitializeDone) {
			return true;
		}
		context = context.getApplicationContext();
		mContext = context;

		// TODO
		checkDexUpdate();

		mIsInitializeDone = true;
		return true;
	}

	private void deinitialize(Context context) {
		if (!mIsInitializeDone) {
			return;
		}
		
		if(null != mDexLoadHelper) {
			mDexLoadHelper.release();
		}

		mIsInitializeDone = false;
	}

	/**
	 * 检查是否有dex更新
	 */
	private void checkDexUpdate() {
		mDexLoadHelper = new DexLoadHelper(mContext);
		mDexLoadHelper.checkDexUpdate();
	}

	@SuppressLint("NewApi")
	public ProxyInterface getProxy() {
		if (!isDexCorrect()) {
			return new LocalProxy();
		}

		try {
			File dexFile = new File(mContext.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath()
					+ DEX_FILE_PATH);
			DexClassLoader classLoader = new DexClassLoader(dexFile.getAbsolutePath(), mContext.getDir("dex",
					Context.MODE_PRIVATE).getAbsolutePath(), null, mContext.getClassLoader());
			Class<?> libClazz = classLoader
					.loadClass("xxx");
			ProxyInterface proxy = (ProxyInterface) libClazz.newInstance();
			return proxy;
		} catch (Exception e) {
//			Tracer.w(TAG, "dex class load error !!");
			return new LocalProxy();
		}
	}

	/**
	 * 是否已安装的dex文件正常
	 * 
	 * @return
	 */
	public boolean isDexCorrect() {
		if (mIsDexCorrect) {
			return true;
		}

		File dexFile = new File(mContext.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath()
				+ DEX_FILE_PATH);
		if (!dexFile.exists()) {
			return false;
		}

		String SMD5 = mContext.getSharedPreferences(DexSpInfo.DEX_LOAD_INFO, Context.MODE_PRIVATE).getString(
				DexSpInfo.DEX_MD5, "");

//		if (Utils.getFileMd5(dexFile).equals(SMD5)) {
//			mIsDexCorrect = true;
//		} else {
//			dexFile.delete();
//			mIsDexCorrect = false;
//		}

		return mIsDexCorrect;
	}

	/**
	 * 正在更新的dex文件是否处于正常状态
	 * 
	 * @return
	 */
	public boolean isTempFileCurrent() {
		String tempFilePath = Environment.getExternalStorageDirectory() + File.separator + "cyDexTemp";
		File tempDexFile = new File(tempFilePath);
		if (!tempDexFile.exists()) {
			return false;
		}

		String SMD5 = mContext.getSharedPreferences(DexSpInfo.DEX_LOAD_INFO, Context.MODE_PRIVATE).getString(
				DexSpInfo.DEX_MD5, "");
//		if (!Utils.getFileMd5(tempDexFile).equals(SMD5)) {
//			tempDexFile.delete();
//			return false;
//		}

		return true;
	}

}

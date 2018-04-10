package com.cy.omniknight.verify.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.WindowManager;

import com.cy.omniknight.BaseActivity;

public class Activity_TestDialogWithContext extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		showDialogWithContext();
		mThread.start();
	}
	
	Thread mThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				public void run() {
					showDialogWithContext();
				}
			});
		}
	});

	/**
	 * 如果想弹系统警告框，Builder(this.getApplicationContext())
	 * 需要添加 ： alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	 * manifest文件添加 <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />  貌似可以不加，why？
	 */
	private void showDialogWithContext() {
		AlertDialog alertDialog = new Builder(this.getApplicationContext()).setTitle("提示")
	             .setMessage("是否在非wifi环境下下载？")
	             .setNegativeButton("取消", new OnClickListener() {

	                 @Override
	                 public void onClick(DialogInterface dialog, int which) {
//	                     if (listener != null) {
//	                         listener.onClickAdStateChang(ClickAdStateChangListener.STATE_OPTION_DIALOG_CANCLE);
//	                     }
	                 }
	             }).setPositiveButton("确认", new OnClickListener() {

	                 @Override
	                 public void onClick(DialogInterface dialog, int which) {
//	                     if (listener != null) {
//	                         listener.onClickAdStateChang(ClickAdStateChangListener.STATE_OPTION_DIALOG_CONFIRM);
//	                     }
//	                     downLoadApp(adInfo, listener);//启动下载
	                 }

	             }).setOnCancelListener(new OnCancelListener() {

	                 @Override
	                 public void onCancel(DialogInterface dialog) {
//	                     if (listener != null) {
//	                         listener.onClickAdStateChang(ClickAdStateChangListener.STATE_OPTION_DIALOG_CANCLE);
//	                     }
	                 }
	             }).create();

	     if (alertDialog != null) {
			 alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	         if (!alertDialog.isShowing()) {
	             alertDialog.show();
	         }
	     }
	}
	
}

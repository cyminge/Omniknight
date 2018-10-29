package com.cy.omniknight.tools.tem;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cy.omniknight.tools.R;

public class ToastUtil {

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    // 适配功能机
    public static final int TOP_DEFAULT = 1;
    public static final int TOP_CONVERSATION = 2;

    private static int mResId = 0;
    private static Toast mToast = null;
    private static TextView mMessage;
    private static View mToastView;
    private static Context mContext;

    // 指定显示时间
    public static void showToast(Context context, final int resId, final int duration) {
        showToast(context, context.getResources().getString(resId), duration);
        mResId = resId;
    }

    public static void showToast(Context context, final String text, final int duration) {
        int top = (int) context.getResources().getDimension(R.dimen.toast_top_default);
        showToast(context, text, duration, top);
    }

    // 指定显示时间和位置
    public static void showToast(Context context, final int resId, final int duration, final int topType) {
        int top = 0;
        if (TOP_CONVERSATION == topType) {
            top = (int) context.getResources().getDimension(R.dimen.conversation_toast_top_default);
        } else {
            top = (int) context.getResources().getDimension(R.dimen.toast_top_default); // 默认
        }
        showToast(context, context.getResources().getString(resId), duration, top);
        mResId = resId;
    }

    public static void showToast(Context context, final String text, final int duration, final int top) {
        mResId = 0;
        mContext = context;
        if ((Looper.myLooper() != Looper.getMainLooper())) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showToast(text, duration, top);
                }
            });
        } else {
            showToast(text, duration, top);
        }
    }

    /*
     * 显示自定义toast
     */
    private static synchronized void showToast(String text, int duration, int top) {
        if (null == mToast) {
            mToast = new Toast(mContext);
            mToastView = LayoutInflater.from(mContext).inflate(R.layout.toast, null);
            mMessage = (TextView) mToastView.findViewById(R.id.message);
            mToast.setView(mToastView);
        }

        mMessage.setText(text);
        Log.e("cyTest", "width = " + mMessage.getWidth() + "----" + mMessage.getMeasuredWidth());
        mToast.setGravity(Gravity.BOTTOM, 0, top);
        mToast.setDuration(duration);
        mToast.show();
    }

    /*
     * 取消toast的显示
     */
    public static synchronized boolean cancelToast(int resId) {
        if (resId == mResId) {
            if (null != mToast) {
                mToast.cancel();
                // mToast = null;
                mResId = 0;
                return true;
            }
        }
        return false;
    }

}

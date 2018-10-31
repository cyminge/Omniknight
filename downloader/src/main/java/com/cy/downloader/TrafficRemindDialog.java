package com.cy.downloader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cy.downloader.database.entity.DownloadInfo;
import com.cy.omniknight.tools.ScreenUtils;
import com.cy.omniknight.tools.Utils;

/**
 * Created by cy on 18-10-12.
 */

public class TrafficRemindDialog extends Dialog {

    private Context mContext;
    private TextView mTitleTv, mContentTv;
    private Button mPositiveBtn, mNegativeBtn;
    private DownloadInfo mDownloadInfo;
    private OnTrafficRemindCallBack mOnTrafficRemindCallBack;

    public View getView() {
        return mView;
    }

    private View mView;

    public TrafficRemindDialog(DownloadInfo downloadInfo, OnTrafficRemindCallBack onTrafficRemindCallBack, @NonNull Context context) {
        this(downloadInfo, onTrafficRemindCallBack, context, R.style.app_FloatActivityDialogTheme);
    }

    public TrafficRemindDialog(DownloadInfo downloadInfo, OnTrafficRemindCallBack onTrafficRemindCallBack, @NonNull Context context, int themeResId) {
        super(context, themeResId);
        mDownloadInfo = downloadInfo;
        mOnTrafficRemindCallBack = onTrafficRemindCallBack;
        mContext = context;
        mView = LayoutInflater.from(mContext).inflate(R.layout.app_kind_reminder_box, null);
        setContentView(mView);
        setGravity(Gravity.BOTTOM);
        initView();
    }


    public interface OnTrafficRemindCallBack {
        void onMobileNetStart(DownloadInfo downloadInfo);
        void onMobileNetCancel(DownloadInfo downloadInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  super.setContentView(R.layout.kind_reminder_box);
        setGravity(Gravity.BOTTOM);
        initView();*/
    }

    public void setGravity(int gravity) {
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = gravity;
        wlp.width = ScreenUtils.getDisplayWidth(mContext);
        window.setAttributes(wlp);
    }

    @Override
    public void show() {
        if(mContext instanceof Activity) {
            if(((Activity)mContext).isFinishing()) {
                return;
            }
        }
        super.show();
    }

    protected void initView() {
        mTitleTv = findViewById(R.id.dialog_title_tv);
        mContentTv = findViewById(R.id.dialog_content_tv);
        mPositiveBtn = findViewById(R.id.positive_button);
        mNegativeBtn = findViewById(R.id.negative_button);
        mPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnTrafficRemindCallBack != null) {
                    mOnTrafficRemindCallBack.onMobileNetStart(mDownloadInfo);
                }
            }
        });

        mNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnTrafficRemindCallBack != null) {
                    mOnTrafficRemindCallBack.onMobileNetCancel(mDownloadInfo);
                }
            }
        });

        Window window = getWindow();
        if (!(mContext instanceof Activity) && window != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                window.setType(WindowManager.LayoutParams.TYPE_PHONE);
            }
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleTv.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(Utils.getApp().getString(titleId));
    }

    public void setContent(int messageId) {
        setContent(Utils.getApp().getString(messageId));
    }

    public void setContent(CharSequence content) {
        mContentTv.setText(content);
    }

    public void setPositiveText(int textId) {
        mPositiveBtn.setText(Utils.getApp().getString(textId));
    }

    public void setNegativeText(int textId) {
        mNegativeBtn.setText(Utils.getApp().getString(textId));
    }
}

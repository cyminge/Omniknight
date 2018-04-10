package com.cy.omniknight.verify.fontsize;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cy.omniknight.BaseActivity;
import com.cy.omniknight.R;

import java.lang.ref.WeakReference;

/**
 * Created by zhanmin on 18-4-9.
 */

public class FontSizeChangerActivity extends BaseActivity implements View.OnClickListener {

    private MyHandler mHandler;

    private LinearLayout root;
    private Button btn;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.font_size_changer);

        root = (LinearLayout) findViewById(R.id.root);
        btn = (Button) findViewById(R.id.btn);
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_3 = (Button) findViewById(R.id.btn_3);

        mHandler = new MyHandler(FontSizeChangerActivity.this);

        getResources(StoreSize.getCurrFontScale(this));

        // 测试文字的放大缩小
        float btnSize = btn.getTextSize();
        StoreSize.setSize(this, btnSize);
    }

    public Resources getResources(final float fontScale) {
        Resources res = super.getResources();
        Configuration config = res.getConfiguration();
        // config.setToDefaults();
        config.fontScale = fontScale;
//        Log.e("cyTest", "-->res.getDisplayMetrics() = " + res.getDisplayMetrics());
        DisplayMetrics dm = res.getDisplayMetrics();
        dm.scaledDensity = 1;
        res.updateConfiguration(config, dm);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                float size = StoreSize.getSize();
                if (0 == size) {
                    size = btn.getTextSize();
                }
                btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * fontScale);
                btn_1.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * fontScale);
                btn_2.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * fontScale);
                btn_3.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * fontScale);
            }
        });
        StoreSize.setCurrFontScale(this, fontScale);
        return res;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn :
                getResources(1.0f);
                break;
            case R.id.btn_1 :
                getResources(1.25f);
                break;
            case R.id.btn_2 :
                getResources(1.36f);
                break;
            case R.id.btn_3 :
                getResources(1.5f);
                break;
        }
    }

    private static class MyHandler extends Handler {

        WeakReference<FontSizeChangerActivity> mMainActivityWR;

        public MyHandler(FontSizeChangerActivity mainActivity) {
            super();
            mMainActivityWR = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            FontSizeChangerActivity mainActivity = mMainActivityWR.get();

            switch (msg.what) {
            }
        }
    }
}

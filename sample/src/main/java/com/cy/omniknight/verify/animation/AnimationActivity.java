package com.cy.omniknight.verify.animation;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cy.omniknight.BaseActivity;
import com.cy.omniknight.R;

/**
 * Created by zhanmin on 18-4-9.
 */

public class AnimationActivity extends BaseActivity implements View.OnClickListener{

    private static final long ANIM_DURATION = 1500;

    private static final float SCALE_PIVOT_Y = 0.5f;
    private static final float SCALE_PIVOT_X = 0.5f;
    private static final float SCALE_FROM_X = 1.0f;
    private static final float SCALE_TO_X = 0.5f;
    private static final float SCALE_FROM_Y = 1.0f;
    private static final float SCALE_TO_Y = 0.5f;

    private static final float ALPHA_FROM = 1.0f;
    private static final float ALPHA_TO = 0.2f;

    private static final float TRANSLATE_FROM_X = 0.0f;
    private static final float TRANSLATE_FROM_Y = 0.0f;

    private ImageView mAnimationView;
    private LinearLayout root;

    public void startAnim() {

        final int deltaX = -200;
        final int deltaY = -500;

//        Log.e("cyTest", "deltaX = " + deltaX + ", deltaY" + deltaY);

        Animation alphaAnimation = new AlphaAnimation(ALPHA_FROM, ALPHA_TO);
        Animation scaleAnimation = new ScaleAnimation(SCALE_FROM_X, SCALE_TO_X, SCALE_FROM_Y, SCALE_TO_Y,
                Animation.RELATIVE_TO_SELF, SCALE_PIVOT_X, Animation.RELATIVE_TO_SELF, SCALE_PIVOT_Y);
        Animation translateAnimation = new TranslateAnimation(TRANSLATE_FROM_X, deltaX, TRANSLATE_FROM_Y,
                deltaY);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(ANIM_DURATION);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        mAnimationView.startAnimation(animationSet);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        root = (LinearLayout) findViewById(R.id.root);

        ImageView iv = new ImageView(this);
        iv.setBackgroundResource(R.drawable.activation_code_receive_progress);

        RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);// 设置动画持续时间
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());//不停顿
        iv.setAnimation(animation);
        animation.startNow();
        root.addView(iv, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (null == mAnimationView) {
            mAnimationView = new ImageView(this);
            mAnimationView.setBackgroundResource(R.drawable.activation_code_gift_icon);
        }

        root.addView(mAnimationView);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn1) {
            startAnim();
        }
    }
}

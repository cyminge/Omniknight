package com.cy.omniknight.verify.touchevent;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.cy.omniknight.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ClickableViewAccessibility") 
public class TouchEventTestActivity extends Activity {

	private static final String TAG = "MotionEventDispatch";
	private CustomButton button;
	private CustomLayout layout;

	private void floatAnim(View view,int delay){
		List<Animator> animators = new ArrayList<>();
		ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(view, "translationX", -6.0f,6.0f,-6.0f);
		translationXAnim.setDuration(1000);
		translationXAnim.setRepeatCount(ValueAnimator.INFINITE);//无限循环
//		 translationXAnim.setRepeatMode(ValueAnimator.INFINITE);//
//		 translationXAnim.start();
//		 animators.add(translationXAnim);
		 ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(view, "translationY", -3.0f,3.0f,-3.0f);
		 translationYAnim.setDuration(1500);
		 translationYAnim.setRepeatCount(ValueAnimator.INFINITE);
//		 translationYAnim.setRepeatMode(ValueAnimator.INFINITE);
		 translationYAnim.start();
		 animators.add(translationYAnim);
		 AnimatorSet btnSexAnimatorSet = new AnimatorSet();
		 btnSexAnimatorSet.playTogether(translationYAnim);
		 btnSexAnimatorSet.setStartDelay(delay);
		 btnSexAnimatorSet.start();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touchevent);
		button = (CustomButton) findViewById(R.id.button1);
		layout = (CustomLayout) findViewById(R.id.linearlayout_test);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.i(TAG, "CustomButton------------------onClick");

			}
		});

		ImageView iv = findViewById(R.id.moveview);
		floatAnim(iv, 100);
//		button.setOnTouchListener(new View.OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					Log.i(TAG, "CustomButton-onTouchListener-ACTION_DOWN");
//					break;
//				case MotionEvent.ACTION_UP:
//					Log.i(TAG, "CustomButton-onTouchListener-ACTION_UP");
//					break;
//				default:
//					break;
//				}
//				return false;
//			}
//		});

		layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "CustomLayout---------------------onClick");
			}
		});

//		layout.setOnTouchListener(new View.OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//
//				case MotionEvent.ACTION_DOWN:
//					Log.i(TAG, "CustomLayout-onTouchListener-ACTION_DOWN");
//					break;
//				case MotionEvent.ACTION_UP:
//					Log.i(TAG, "CustomLayout-onTouchListener-ACTION_UP");
//					break;
//
//				default:
//					break;
//
//				}
//				return false;
//			}
//		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "MainActivity-dispatchTouchEvent-ACTION_DOWN");
			break;
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "MainActivity-dispatchTouchEvent-ACTION_UP");
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "MainActivity-onTouchEvent-ACTION_DOWN");
			break;
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "MainActivity-onTouchEvent-ACTION_UP");
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
}

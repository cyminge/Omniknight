package com.cy.omniknight.verify.touchevent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

@SuppressLint("ClickableViewAccessibility")
public class CustomButton extends Button {

	private static final String TAG = "MotionEventDispatch";

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
            Log.i(TAG, "CustomButton-onTouchEvent-ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE :
//			 Log.i(TAG, "CustomButton-onTouchEvent-ACTION_MOVE");
		case MotionEvent.ACTION_UP:
            Log.i(TAG, "CustomButton-onTouchEvent-ACTION_UP");
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
            Log.i(TAG, "CustomButton-dispatchTouchEvent-ACTION_DOWN");
			break;
		case MotionEvent.ACTION_UP:
            Log.i(TAG, "CustomButton-dispatchTouchEvent-ACTION_UP");
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(event);
	}

}

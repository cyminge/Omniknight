package com.cy.omniknight.tools;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by cy on 18-10-30.
 */

public class PopupWindowUtils {

    /**
     * 初始化PopupWindow
     *
     * @param context
     * @param view 要显示的布局
     * @param width PopupWindow宽度
     * @param height PopupWindow高度
     * @param drawable PopupWindow背景图片
     * @param animStyle PopupWindow动画类型
     * @return
     */
    public static PopupWindow initPopupWindow(Context context, View view, int width, int height, Drawable drawable, int animStyle) {
        PopupWindow mPopupWindow = new PopupWindow(view, width, height);
        mPopupWindow.setBackgroundDrawable(drawable);
        mPopupWindow.setOutsideTouchable(true);
//		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        mPopupWindow.setAnimationStyle(animStyle);
        mPopupWindow.update();
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        return mPopupWindow;
    }

    /**
     * 初始化PopupWindow
     *
     * @param context
     * @param view 要显示的布局
     * @param width PopupWindow宽度
     * @param height PopupWindow高度
     * @param drawable PopupWindow背景图片
     * @return
     */
    public static PopupWindow initPopupWindow(Context context, View view, int width, int height, Drawable drawable) {
        PopupWindow mPopupWindow = new PopupWindow(view, width, height);
        mPopupWindow.setBackgroundDrawable(drawable);
        mPopupWindow.setOutsideTouchable(true);
//		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        mPopupWindow.update();
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        return mPopupWindow;
    }

    /**
     * 初始化PopupWindow
     *
     * @param context
     * @param view 要显示的布局
     * @param drawable PopupWindow背景图片
     * @return
     */
    @SuppressLint("InlinedApi")
    public static PopupWindow initPopupWindow(Context context, View view, Drawable drawable) {
        PopupWindow mPopupWindow = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mPopupWindow.setBackgroundDrawable(drawable);
        mPopupWindow.setOutsideTouchable(true);
//		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        mPopupWindow.update();
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        return mPopupWindow;
    }

    /**
     * 显示PopupWindow
     *
     * @param view
     */
    public static void showPopupWindowCenter(PopupWindow popupWindow, View view) {
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * 显示PopupWindow
     *
     * @param view
     */
    public static void showPopupWindowBelowView(PopupWindow popupWindow, View view) {
        if (!popupWindow.isShowing()) {
            popupWindow.showAsDropDown(view, 0, 0);
        }
    }
}

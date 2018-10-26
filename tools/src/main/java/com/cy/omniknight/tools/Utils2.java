package com.cy.omniknight.tools;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 网络上查询到的公共方法集合
 * Created by cy on 18-10-16.
 */

public class Utils2 {

    public static int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.left + bounds.width();
        return width;
    }

    public static int getTextHeight(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int height = bounds.bottom + bounds.height();
        return height;
    }

}

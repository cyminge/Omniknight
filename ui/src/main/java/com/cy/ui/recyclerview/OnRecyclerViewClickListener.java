package com.cy.ui.recyclerview;

import android.view.View;

/**
 * Created by zhanmin on 18-3-1.
 */

public interface OnRecyclerViewClickListener {
    /**
     * ItemView的点击事件
     * @param view
     * @param position
     */
    void onItemClick(View view, int position);

    /**
     * 每个Item里面的子View的点击事件
     * @param view
     * @param position
     */
    void onViewClick(View view, int position);
}

package com.cy.ui.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 如果有滑动事件、或者长按事件等等其他事件，又该如何传递
 * @param <T>
 */
public abstract class BaseRecyclerViewHolder<T> extends RecyclerView.ViewHolder {

	private boolean mIsLastData;
	
	public BaseRecyclerViewHolder(View itemView) {
		super(itemView);

//		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		itemView.setLayoutParams(layoutParams);
	}
	
	public abstract void initItemView(View.OnClickListener onClickListener);
	
	public abstract void bindItemData(T data, int position);

	public void setIsLastData(boolean isLastData) {
		mIsLastData = isLastData;
	}

	public boolean isLastData() {
		return mIsLastData;
	}

	public void onDestroy() {

	}

}

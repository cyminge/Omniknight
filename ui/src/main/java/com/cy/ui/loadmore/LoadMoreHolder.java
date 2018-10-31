package com.cy.ui.loadmore;

import android.view.View;

import com.cy.ui.recyclerview.BaseRecyclerViewHolder;

/**
 * Created by cyminge on 2017/12/29.
 */

public class LoadMoreHolder extends BaseRecyclerViewHolder {

    private LoadMoreView mLoadMoreView;

    public LoadMoreHolder(View itemView, LoadMoreView loadMoreView) {
        super(itemView);
        mLoadMoreView = loadMoreView;
    }

    @Override
    public void initItemView(View.OnClickListener onClickListener) {
        mLoadMoreView.initView(itemView, onClickListener);
    }

    @Override
    public void bindItemData(Object data, int position) {
        mLoadMoreView.setItemView();
    }
}

package com.cy.ui.loadmore;


import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.cy.ui.R;

/**
 * Created by cyminge on 2017/12/29.
 */

public abstract class LoadMoreView {

    public static final int STATUS_DEFAULT = 1;
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_FAIL = 3;
    public static final int STATUS_END = 4;

    private int mLoadMoreStatus = STATUS_DEFAULT;
    private boolean mLoadMoreEndGone = false;

    private LinearLayout mLoadingView;
    private FrameLayout mLoadFailView;
    private FrameLayout mLoadEndView;

    public void initView(View itemView, View.OnClickListener onClickListener) {
        mLoadingView = (LinearLayout) itemView.findViewById(R.id.load_more_loading_view);
        mLoadFailView = (FrameLayout) itemView.findViewById(R.id.load_more_load_fail_view);
        mLoadFailView.setOnClickListener(onClickListener);
        mLoadEndView = (FrameLayout) itemView.findViewById(R.id.load_more_load_end_view);
    }

    public void setItemView() {
        convert();
    }

    public void setLoadMoreStatus(int loadMoreStatus) {
        this.mLoadMoreStatus = loadMoreStatus;
    }

    public int getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    public void convert() {
        switch (mLoadMoreStatus) {
            case STATUS_LOADING:
                visibleLoading(true);
                visibleLoadFail(false);
                visibleLoadEnd(false);
                break;
            case STATUS_FAIL:
                visibleLoading(false);
                visibleLoadFail(true);
                visibleLoadEnd(false);
                break;
            case STATUS_END:
                visibleLoading(false);
                visibleLoadFail(false);
                visibleLoadEnd(true);
                break;
            case STATUS_DEFAULT:
                visibleLoading(false);
                visibleLoadFail(false);
                visibleLoadEnd(false);
                break;
            default:
                break;
        }
    }

    private void visibleLoading(boolean visible) {
        mLoadingView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void visibleLoadFail( boolean visible) {
        mLoadFailView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void visibleLoadEnd(boolean visible) {
        mLoadEndView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public final void setLoadMoreEndGone(boolean loadMoreEndGone) {
        this.mLoadMoreEndGone = loadMoreEndGone;
    }

    public final boolean isLoadEndMoreGone() {
        if (getLoadEndViewId() == 0) {
            return true;
        }
        return mLoadMoreEndGone;
    }

    @Deprecated
    public boolean isLoadEndGone() {
        return mLoadMoreEndGone;
    }

    /**
     * load more layout
     *
     * @return
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * loading view
     *
     * @return
     */
    protected abstract
    @IdRes
    int getLoadingViewId();

    /**
     * load fail view
     *
     * @return
     */
    protected abstract
    @IdRes
    int getLoadFailViewId();

    /**
     * load end view, you can return 0
     *
     * @return
     */
    protected abstract
    @IdRes
    int getLoadEndViewId();
}

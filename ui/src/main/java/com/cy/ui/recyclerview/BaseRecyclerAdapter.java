package com.cy.ui.recyclerview;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cy.ui.R;
import com.cy.ui.loadmore.LoadMoreHolder;
import com.cy.ui.loadmore.LoadMoreView;
import com.cy.ui.loadmore.SimpleLoadMoreView;
import com.cy.ui.multitype.BinderNotFoundException;
import com.cy.ui.multitype.ITypePool;
import com.cy.ui.multitype.MultiITypePool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder<T>> implements View.OnClickListener {

    private static final String TAG = "BaseRecyclerAdapter";

    public static final String TAG_ITEM_VIEW = "ItemView";

    public static final int HEADER_ITEM_VIEW_TYPE = Integer.MAX_VALUE - 1;
    private static final int FOOTER_ITEM_VIEW_TYPE = HEADER_ITEM_VIEW_TYPE - 1000;
    private static final int LOAD_MORE_ITEM_VIEW_TYPE = FOOTER_ITEM_VIEW_TYPE - 1;
    protected @NonNull ArrayList<T> mDataArray;
    private @NonNull ITypePool mBodyTypePool;
    private ITypePool mHeaderTypePool;
    private ITypePool mFooterTypePool;

    protected volatile ArrayList<Object> mHeaderViewDataArray = new ArrayList<>();

    private LoadMoreView mLoadMoreView = new SimpleLoadMoreView();
    private LayoutInflater mInflater;
    private boolean mNextLoadEnable = false;
    private boolean mLoadMoreEnable = false;
    private boolean mLoading = false;
    private RequestLoadMoreListener mRequestLoadMoreListener;
    private OnRecyclerViewClickListener mOnRecyclerViewClickListener;

    private BaseRecyclerViewHolder mFooterHolder;

    public BaseRecyclerAdapter() {
        this(new ArrayList<T>());
    }

    public BaseRecyclerAdapter(@NonNull ArrayList<T> items) {
        mDataArray = items;
        mBodyTypePool = new MultiITypePool();
        mHeaderTypePool = new MultiITypePool();
        mFooterTypePool = new MultiITypePool();
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + mDataArray.size() + getFooterViewCount() + getLoadMoreViewCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderViewCount()) {
            return getHeaderItemViewType(position);
        }

        if (getFooterViewCount() > 0 && position >= getItemCount() - getFooterViewCount() && position < getItemCount()) {
            return FOOTER_ITEM_VIEW_TYPE;
        }

        if (getLoadMoreViewCount() > 0 && position >= getItemCount() - 1) {
            return LOAD_MORE_ITEM_VIEW_TYPE;
        }

        if (getHeaderViewCount() > 0) {
            return getMainBodyItemViewType(position - getHeaderViewCount());
        }

        return getMainBodyItemViewType(position);
    }

    private int getHeaderItemViewType(int position) throws BinderNotFoundException {
        Object item = mHeaderViewDataArray.get(position);
        int index = mHeaderTypePool.firstIndexOf(item.getClass());
        if (index != -1) {
            return HEADER_ITEM_VIEW_TYPE - index;
        }
        throw new BinderNotFoundException(item.getClass());
    }

    /**
     * @param position
     * @return need not be zero ??
     */
    private int getMainBodyItemViewType(int position) throws BinderNotFoundException {
        Object item = mDataArray.get(position);
        int index = mBodyTypePool.firstIndexOf(item.getClass());
        if (index != -1) {
            return index;
        }
        throw new BinderNotFoundException(item.getClass());
    }

    public void setOnClickListener(OnRecyclerViewClickListener onRecyclerViewClickListener) {
        mOnRecyclerViewClickListener = onRecyclerViewClickListener;
    }

    public BaseRecyclerViewHolder getFooterHolder() {
        return mFooterHolder;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder holder;
        if (null == mInflater) {
            mInflater = LayoutInflater.from(parent.getContext());
        }

        if (viewType > HEADER_ITEM_VIEW_TYPE - mHeaderTypePool.getTypeCounts()) {
            holder = createViewHolder(mInflater, parent, HEADER_ITEM_VIEW_TYPE - viewType, mHeaderTypePool);
        } else {
            switch (viewType) {
                case FOOTER_ITEM_VIEW_TYPE:
                    holder = createViewHolder(mInflater, parent, FOOTER_ITEM_VIEW_TYPE - viewType, mFooterTypePool);
                    mFooterHolder = holder;
                    break;
                case LOAD_MORE_ITEM_VIEW_TYPE:
                    holder = createLoadMoreHolder(mInflater, parent, viewType);
                    return holder;
                default:
                    holder = createViewHolder(mInflater, parent, viewType, mBodyTypePool);
                    break;
            }
        }

        if (null != holder) {
            holder.itemView.setOnClickListener(this);
        }
        return holder;
    }

    private BaseRecyclerViewHolder createViewHolder(LayoutInflater inflater, ViewGroup parent, int indexViewType, ITypePool typePool) {
        Class<?> clazz = typePool.getHolder(indexViewType);
        Class<?>[] parameterTypes = {View.class};
        Constructor<?> constructor;
        try {
            constructor = clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.e(TAG, "createViewHolder 获取ViewHolder构造函数失败!clazz=" + clazz.getName());
            return null;
        }
        View view = inflater.inflate(typePool.getLayoutId(indexViewType), parent, false);
        try {
            BaseRecyclerViewHolder holder = (BaseRecyclerViewHolder) constructor.newInstance(view);
            holder.initItemView(this);
            return holder;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected BaseRecyclerViewHolder createLoadMoreHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View view = inflater.inflate(mLoadMoreView.getLayoutId(), parent, false);
        LoadMoreHolder holder = new LoadMoreHolder(view, mLoadMoreView);
        holder.initItemView(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder viewHolder, int position) {
        autoLoadMore(position);
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType > HEADER_ITEM_VIEW_TYPE - mHeaderTypePool.getTypeCounts()) {
            bindHeaderViewData(viewHolder, position);
        } else {
            switch (itemViewType) {
                case FOOTER_ITEM_VIEW_TYPE:
                    bindFooterViewData(viewHolder, position);
                    break;
                case LOAD_MORE_ITEM_VIEW_TYPE:
                    viewHolder.bindItemData(null, position);
                    break;
                default:
                    bindMainItemData(viewHolder, position);
                    break;
            }
        }
        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setTag(R.id.tag_item_view, TAG_ITEM_VIEW);
    }

    @Override
    public void onClick(View v) {
        if (isLoadMoreFailViewClick(v)) {
            loadMore();
            return;
        }

        if (null != v.getTag(R.id.tag_item_view)) {
            mOnRecyclerViewClickListener.onItemClick(v, (int) v.getTag());
        } else {
            int position = -1;
            try {
                position = (int) v.getTag();
            } catch (Exception e) {
            }
            mOnRecyclerViewClickListener.onViewClick(v, position);
        }
    }

    private boolean isLoadMoreFailViewClick(View v) {
        return v.getId() == R.id.load_more_load_fail_view;
    }

    protected void bindHeaderViewData(BaseRecyclerViewHolder viewHolder, int position) {
        viewHolder.bindItemData(mHeaderViewDataArray.get(position), position);
    }

    protected void bindMainItemData(BaseRecyclerViewHolder viewHolder, int position) {
        Object item = getItemData(position);
        viewHolder.bindItemData(item, position);
    }

    protected void bindFooterViewData(BaseRecyclerViewHolder viewHolder, int position) {
        viewHolder.bindItemData(null, position);
    }

    public interface RequestLoadMoreListener {
        void onLoadMoreRequested();
    }

    public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener) {
        openLoadMore(requestLoadMoreListener);
    }

    private void openLoadMore(RequestLoadMoreListener requestLoadMoreListener) {
        mRequestLoadMoreListener = requestLoadMoreListener;
        mNextLoadEnable = true;
        mLoadMoreEnable = true;
        mLoading = false;
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void loadMoreEnd(boolean gone) {
        if (getLoadMoreViewCount() == 0) {
            return;
        }
        mLoading = false;
        mNextLoadEnable = false;
        mLoadMoreView.setLoadMoreEndGone(gone);
        if (gone) {
            notifyItemRemoved(getLoadMoreViewPosition());
        } else {
            mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_END);
            notifyItemChanged(getLoadMoreViewPosition());
        }
    }

    public void loadMoreComplete() {
        if (getLoadMoreViewCount() == 0) {
            return;
        }
        mLoading = false;
        mNextLoadEnable = true;
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    public void loadMoreFail() {
        if (getLoadMoreViewCount() == 0) {
            return;
        }
        mLoading = false;
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_FAIL);
//		notifyItemRangeChanged(getLoadMoreViewPosition(), getItemCount()); // 局部刷新有bug，暂时未明白为何，后面有时间了改动
        notifyDataSetChanged();
    }

    private int getLoadMoreViewPosition() {
        return getItemCount() - 1;
    }

    private void autoLoadMore(int position) {
        if (getLoadMoreViewCount() == 0) {
            return;
        }

        if (null == mDataArray || mDataArray.isEmpty()) {
            return;
        }

        if (position < getLoadMoreViewPosition()) {
            return;
        }

        if (mLoadMoreView.getLoadMoreStatus() != LoadMoreView.STATUS_DEFAULT) {
            return;
        }

        loadMore();
    }

    private void loadMore() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_LOADING);
        if (mRequestLoadMoreListener != null) {
            mRequestLoadMoreListener.onLoadMoreRequested();
        }
    }

    /**
     * Set the enabled state of load more.
     *
     * @param enable True if load more is enabled, false otherwise.
     */
    public void setEnableLoadMore(boolean enable) {
        int oldLoadMoreCount = getLoadMoreViewCount();
        mLoadMoreEnable = enable;
        int newLoadMoreCount = getLoadMoreViewCount();

        if (oldLoadMoreCount == newLoadMoreCount) {
            return;
        }

        if (oldLoadMoreCount == 0 && newLoadMoreCount == 1) {
            mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        }
        notifyItemInserted(getLoadMoreViewPosition());
    }

    /**
     * 去掉头尾视图数据
     *
     * @param position
     * @return
     */
    public Object getItemData(int position) {
        if (position < getHeaderViewCount() || position >= getItemCount() - getFooterViewCount()) {
            return null;
        }
        return mDataArray.get(position - getHeaderViewCount());
    }

    public void setLoadMoreView(LoadMoreView loadMoreView) {
        if (null == loadMoreView) {
            return;
        }
        mLoadMoreView = loadMoreView;
//        this.notifyDataSetChanged();
    }

    public int getHeaderViewCount() {
        return mHeaderViewDataArray.size();
    }

    public ArrayList<Object> getHeaderData() {
        return mHeaderViewDataArray;
    }

    public Object getHeaderData(int position) {
        return mHeaderViewDataArray.get(position);
    }

    public int getFooterViewCount() {
        return mFooterTypePool.getTypeCounts();
    }

    public int getLoadMoreViewCount() {
        if (mRequestLoadMoreListener == null || !mLoadMoreEnable) {
            return 0;
        }
        if (!mNextLoadEnable && mLoadMoreView.isLoadEndMoreGone()) {
            return 0;
        }
        if (mDataArray.size() == 0) {
            return 0;
        }
        return null != mLoadMoreView ? 1 : 0;
    }

    public boolean isHeader(int position) {
        return getHeaderViewCount() > 0 && position == 0;
    }

    public boolean isFooter(int position) {
        int lastPosition = getItemCount() - 1;
        return getFooterViewCount() > 0 && position == lastPosition;
    }

    public void clearData() {
        mDataArray.clear();
    }

    /**
     * 更新HeaderView数据
     *
     * @param headerDataArray
     */
    public void updateHeaderData(ArrayList<Object> headerDataArray) {
        mHeaderViewDataArray.clear();
        mHeaderViewDataArray.addAll(headerDataArray);
    }

    /**
     * 添加HeaderView数据
     *
     * @param headerData
     */
    public void addHeaderData(Object headerData) {
        mHeaderViewDataArray.add(headerData);
    }

    public void clearHeaderData() {
        mHeaderViewDataArray.clear();
    }

    /**
     * must to call on main thread
     *
     * @param data 主体界面的数据， 不包括头部尾部数据
     */
    @MainThread
    public void updateData(ArrayList<T> data) {
        mDataArray.clear();
        addData(data);
    }

    /**
     * must to call on main thread
     *
     * @param data 主体界面的数据， 不包括头部尾部数据
     */
    @MainThread
    public void addData(ArrayList<T> data) {
        mDataArray.addAll(data);
        if (mRequestLoadMoreListener != null) {
            mNextLoadEnable = true;
            mLoadMoreEnable = true;
            mLoading = false;
            mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        }
    }


    /**
     * must to call on main thread
     *
     * @param data
     */
    @MainThread
    public void addDataItem(T data) {
        mDataArray.add(data);
        if (mRequestLoadMoreListener != null) {
            mNextLoadEnable = true;
            mLoadMoreEnable = true;
            mLoading = false;
            mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        }
    }

    public @NonNull List<T> getDatas() {
        return mDataArray;
    }

    /**
     * 因为业务的关系，开发人员在设计的时候，有可能将多种展示形式用同一个数据结构来表示（比如信息流中的无图片、一张图片、多张图片的数据结构）。这不是一种好的做法，会让后面的维护和修改变得很困难。
     * 在我看来，不同展示形式就应该用不同数据结构来表示，因为考虑到业务的频繁变更，很多数据结构可能随时做修改，如果多种展示形式用同一个数据结构，更多的业务变更将会使数据结构越来越混乱。
     * 所以这个方法的设计是， dataStructure与holder的对应关系是一对一，或者多对一，多对一这种情况可能正常展示，但是最好不要有这种设计。
     * 如果是一对多，数据将不能正常展示！！！！！
     *
     * @param dataStructure
     * @param holder
     * @param layoutId
     * @param <T>
     */
    public <T> void register(Class<? extends T> dataStructure, Class<? extends RecyclerView.ViewHolder> holder, int layoutId) {
        checkAndRemoveAllTypesIfNeeded(dataStructure);
        mBodyTypePool.register(dataStructure, holder, layoutId);
    }

    private void checkAndRemoveAllTypesIfNeeded(@NonNull Class<?> clazz) {
        if (mBodyTypePool.unregister(clazz)) {
            Log.w(TAG, "You have registered the " + clazz.getSimpleName() + " type. " +
                    "It will override the original binder(s).");
        }
    }

    public void registerHeaderView(Class<?> dataStructure, Class<? extends RecyclerView.ViewHolder> holder, int layoutId) {
        mHeaderTypePool.register(dataStructure, holder, layoutId);
    }

    /**
     * 请注意 该框架只支持一个FooterView，并且只支持静态view，不支持动态数据
     * @param dataStructure
     * @param holder
     * @param layoutId
     */
    public void registerFooterView(Class<?> dataStructure, Class<? extends RecyclerView.ViewHolder> holder, int layoutId) {
        mFooterTypePool.clear();
        mFooterTypePool.register(dataStructure, holder, layoutId);
    }
}

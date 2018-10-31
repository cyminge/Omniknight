/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cy.ui.multitype;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * An List implementation of ITypePool.
 *
 * @author zhanmin
 */
public class HeaderTypePool implements IHeaderTypePool {

    private @NonNull final List<Class<?>> mItemDatas;
    private @NonNull final List<Class<?>> mHolders;
    private @NonNull final List<Integer> mLayoutId;

    public HeaderTypePool() {
        this.mItemDatas = new ArrayList<>();
        this.mHolders = new ArrayList<>();
        this.mLayoutId = new ArrayList<>();
    }

    public HeaderTypePool(int initialCapacity) {
        this.mItemDatas = new ArrayList<>(initialCapacity);
        this.mHolders = new ArrayList<>(initialCapacity);
        this.mLayoutId = new ArrayList<>(initialCapacity);
    }

    public HeaderTypePool(
        @NonNull List<Class<?>> itemDatas,
        @NonNull List<Class<?>> holders,
        @NonNull List<Integer> layoutId,
        @NonNull List<Integer> viewType) {
        this.mItemDatas = itemDatas;
        this.mHolders = holders;
        this.mLayoutId = layoutId;
    }

    @Override
    public <T, V> void register(
        @NonNull Class<? extends T> clazz,
        @NonNull Class<? extends V> holders,
        @NonNull int layoutId) {
        mItemDatas.add(clazz);
        mHolders.add(holders);
        mLayoutId.add(layoutId);
    }

    @Override
    public boolean unregister(@NonNull Class<?> clazz) {
        boolean removed = false;
        while (true) {
            int index = mItemDatas.indexOf(clazz);
            if (index != -1) {
                mItemDatas.remove(index);
                mHolders.remove(index);
                mLayoutId.remove(index);
                removed = true;
            } else {
                break;
            }
        }
        return removed;
    }

    /**
     * @param clazz the item class.
     * @return
     */
    @Override
    public int firstIndexOf(@NonNull final Class<?> clazz) {
        int index = mItemDatas.indexOf(clazz);
        if (index != -1) {
            return index;
        }
        for (int i = 0; i < mItemDatas.size(); i++) {
            if (mItemDatas.get(i).isAssignableFrom(clazz)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public @NonNull
    Class<?> getItemData(int index) {
        return mItemDatas.get(index);
    }

    @Override
    public @NonNull
    Class<?> getHolder(int index) {
        return mHolders.get(index);
    }

    @NonNull
    @Override
    public int getLayoutId(int index) {
        return mLayoutId.get(index);
    }

    @Override
    public int getHeaderTypeCounts() {
        return mHolders.size();
    }
}

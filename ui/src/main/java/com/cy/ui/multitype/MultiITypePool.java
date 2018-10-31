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
public class MultiITypePool implements ITypePool {

    private @NonNull final List<Class<?>> mItemDatas;
    private @NonNull final List<Class<?>> mHolders;
    private @NonNull final List<Integer> mLayoutIds;

    public MultiITypePool() {
        this.mItemDatas = new ArrayList<>();
        this.mHolders = new ArrayList<>();
        this.mLayoutIds = new ArrayList<>();
    }

    @Override
    public <T, V> void register(
        Class<? extends T> itemData,
        @NonNull Class<? extends V> holder,
        @NonNull int layoutId) {
        mItemDatas.add(itemData);
        mHolders.add(holder);
        mLayoutIds.add(layoutId);
    }

    @Override
    public void clear() {
        mItemDatas.clear();
        mHolders.clear();
        mLayoutIds.clear();
    }

    @Override
    public boolean unregister(@NonNull Class<?> clazz) {
        boolean removed = false;
        while (true) {
            int index = mItemDatas.indexOf(clazz);
            if (index != -1) {
                mItemDatas.remove(index);
                mHolders.remove(index);
                mLayoutIds.remove(index);
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
    public Class<?> getItemData(int index) {
        return mItemDatas.get(index);
    }

    @Override
    public @NonNull Class<?> getHolder(int index) {
        return mHolders.get(index);
    }

    @Override
    public @NonNull int getLayoutId(int index) {
        return mLayoutIds.get(index);
    }

    @Override
    public int getTypeCounts() {
        return mHolders.size();
    }
}

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

/**
 * An ordered collection to hold the itemData, holder, layoutId.
 *
 * @author zhanmin
 */
public interface IHeaderTypePool {

    /**
     * Registers a type class and its item view binder.
     *
     * @param itemData the class of a item
     * @param holder the item view holder
     * @param <T> the item data type
     */
    <T, V> void register(
            @NonNull Class<? extends T> itemData,
            @NonNull Class<? extends V> holder,
            @NonNull int layoutId);

    /**
     * Unregister all items with the specified class.
     *
     * @param clazz the class of items
     * @return true if any items are unregistered from the pool
     */
    boolean unregister(@NonNull Class<?> clazz);

    /**
     *
     * @param clazz the item class.
     * @return 返回当前数据对应的ItemViewType
     */
    int firstIndexOf(@NonNull Class<?> clazz);

    /**
     * Gets the class at the specified index.
     *
     * @param index the item index
     * @return the class at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @NonNull
    Class<?> getItemData(int index);

    /**
     * Gets the item view binder at the specified index.
     *
     * @param index the item index
     * @return the item class at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @NonNull
    Class<?> getHolder(int index);

    @NonNull
    int getLayoutId(int index);

    int getHeaderTypeCounts();

}

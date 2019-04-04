/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cy.base.mvp;

import com.cy.base.ThreadBusUtil;
import com.cy.base.callback.CommonCallback;
import com.cy.base.callback.ICallback;
import com.cy.base.exception.ResponseException;
import com.cy.base.integration.IRepositoryManager;
import com.cy.omniknight.tools.ObjectUtils;
import com.cy.threadbus.TaskRunnable;

/**
 * ================================================
 * 基类 Model
 *
 * @see <a href="https://github.com/JessYanCoding/MVPArms/wiki#2.4.3">Model wiki 官方文档</a>
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class BaseModel<T extends IRepositoryManager> implements IBaseContract.IModel {
    public T mRepositoryManager;//用于管理网络请求层, 以及数据缓存层

    public BaseModel(T repositoryManager) {
        this.mRepositoryManager = repositoryManager;
    }

    protected static CommonCallback sEmptyCall = new CommonCallback(SimpleContract.sBasePresenter) {
        @Override
        protected void onSuccess(Object result) {

        }

        @Override
        protected void onError(ResponseException error) {

        }
    };

    /**
     * 在框架中  BasePresenter#onDestroy() 时会默认调用 @link IModel#onDestroy()
     */
    @Override
    public void onDestroy() {
        mRepositoryManager = null;
    }

    protected <T> void enqueue(final Runnable runnable, final ICallback<T> callback) {
        TaskRunnable taskRunnable = new TaskRunnable("AppBaseModel") {
            @Override
            public void runTask() {
                try {
                    runnable.run();
                } catch (Exception e) {
//                    LogUtil.wForce(TAG, e.getMessage());
                    if (ObjectUtils.isNotEmpty(callback)) {
                        callback.onError(new ResponseException(e.getMessage()));
                    }
                }
            }
        };
        ThreadBusUtil.enqueue(taskRunnable);
    }

}

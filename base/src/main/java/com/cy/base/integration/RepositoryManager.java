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
package com.cy.base.integration;

import com.cy.base.integration.cache.Cache;
import com.cy.base.integration.cache.CacheType;
import com.cy.base.sp.ISP;
import com.cy.omniknight.tools.ObjectUtils;

/**
 * ================================================
 * 用来管理网络请求层,以及数据缓存层,以后可能添加数据库请求层
 * 提供给 IModel 层必要的 Api 做数据处理
 * <p>
 * 流程分析：
 * 获取Retrofit和RxCache的实例
 *
 * @see <a href="https://github.com/JessYanCoding/MVPArms/wiki#2.3">RepositoryManager wiki 官方文档</a>
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class RepositoryManager implements IRepositoryManager {
    private Cache<String, Object> mRetrofitServiceCache;
    private static RepositoryManager sRepositoryManager = new RepositoryManager();

    public static IRepositoryManager getInstance() {
        return sRepositoryManager;
    }

    protected RepositoryManager() {
    }

    protected synchronized <T> T getRetrofitService(Class<T> service, String url) {
        if (mRetrofitServiceCache == null) {
            mRetrofitServiceCache = GlobalVariable.sBaseStrategy.getCacheFactory().build(CacheType.RETROFIT_SERVICE_CACHE);
        }
        ObjectUtils.requireNonNull(mRetrofitServiceCache, "Cannot return null from a Cache.Factory#build(int) method");
        T retrofitService = (T) mRetrofitServiceCache.get(service.getCanonicalName());
        if (retrofitService == null) {
            retrofitService = sBaseStrategy.getRetrofitServiceFactory().build(service,url);
            mRetrofitServiceCache.put(service.getCanonicalName(), retrofitService);
        }
        return retrofitService;
    }

    /**
     * 提供SP文件缓存服务
     *
     * @return
     */
    public synchronized ISP getSPService() {
        return sBaseStrategy.getSPService();
    }

    /**
     * 提供内存缓存服务
     *
     * @return
     */
    public synchronized Cache getCacheService() {
        return sBaseStrategy.getCacheFactory().build(CacheType.EXTRAS);
    }
}

package com.cy.webviewagent.util;

import java.lang.ref.WeakReference;

/**
 * Created by JLB6088 on 2017/10/16.
 */

public class Utils {

    /**
     * 根据传入的Object生成WeakReference对象
     * @param target
     * @param <T>
     * @return
     */
    public static <T> WeakReference<T> buildReference(T target) {
        return target == null ? null : new WeakReference<>(target);
    }

    /**
     * 是否WeakReference对象可用
     * @param reference
     * @param <T>
     * @return
     */
    public static <T> boolean isWeakReferenceActive(WeakReference<T> reference) {
        if(null == reference || null == reference.get()) {
            return false;
        }
        return true;
    }

    /**
     * 获取WeakReference包装的Object
     * @param reference
     * @param <T>
     * @return
     */
    public static <T> T getReferenceTarget(WeakReference<T> reference) {
        return reference == null ? null : reference.get();
    }

}

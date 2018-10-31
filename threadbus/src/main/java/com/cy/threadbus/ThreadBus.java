package com.cy.threadbus;

import android.content.Context;

import com.cy.threadbus.scheduler.controller.RulerService;
import com.cy.threadbus.utils.DeviceUtil;
import com.cy.threadbus.utils.ObjectHelper;

/**
 * you must invoke {@code init(Context context)} when application create
 *
 * <pre>
 *     use enum type to create a Java Singleton.
 *     enum usage: {@code ThreadBus.getMainThreadHandler}
 * </pre>
 *
 * Created by JLB6088 on 2017/5/24.
 */

public enum ThreadBus {

    INSTANCE;

    private Context mContext;
    private boolean mIsInitializeDone = false;

    private boolean initialize(Context context) {
        if (mIsInitializeDone) {
            return true;
        }
        mIsInitializeDone = true;
        mContext = context.getApplicationContext();
        RulerService.getInstance().init(context);
        DeviceUtil.init(context);
        return true;
    }

    private void unInitialize() {
        //TODO 第一版屏蔽，不需要释放资源
//        if (!mIsInitializeDone) {
//            return;
//        }
//
//        mContext = null;
//        mIsInitializeDone = false;
    }

    private Assembler createAssembler() {
        ObjectHelper.requireNonNull(INSTANCE.mContext, "invoke with not invoke ThreadBus.init() !");
        return new Assembler();
    }

    // ============== 公共方法  ================================================================================================

    /**
     * 初始化函数，在application初始化的时候调用。
     * @param context
     * @return
     */
    public static boolean init(Context context) {
        return INSTANCE.initialize(context);
    }

    /**
     * 资源释放函数，与init函数对应。
     * @Hide
     */
    public static void release() {
        INSTANCE.unInitialize();
    }

    /**
     * 新建一个任务组装器
     * @return
     */
    public static IAssembler newAssembler() {
        return INSTANCE.createAssembler();
    }

    // ============== end  ================================================================================================

}

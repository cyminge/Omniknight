package com.cy.omniknight;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.cy.omniknight.socket.SPushService;
import com.cy.omniknight.tools.Utils;
import com.cy.omniknight.tools.receiver.ReceiverManager;
import com.cy.omniknight.tracer.Tracer;
import com.cy.statistics.StatisticsManager;
import com.cy.threadbus.ThreadBus;

/**
 * app启动初始化类
 *
 * @author zhanmin
 */
public enum WatchDog {
    INSTANCE;

    WatchDog() {
    }

    private Context mContext;
    public boolean sIsInitializeDone = false;

    public static boolean init(Context context) {
        context = context.getApplicationContext();
        return INSTANCE.initialize(context);
    }

    public static void deInit() {
        INSTANCE.DeInitialize();
    }

    /**
     * 初始化函数，app启动时调用
     *
     * @param context
     * @return
     */
    private boolean initialize(Context context) {
        if (sIsInitializeDone) {
            return true;
        }
        context = context.getApplicationContext();
        mContext = context;

        // 全局初始化代码在这里添加
        initGlobals(context);
        initOthers(context);
        sIsInitializeDone = true;
        return true;
    }

    /**
     * 与业务无关的公共的模块的初始化
     *
     * @param context
     */
    private void initGlobals(Context context) {
//        if (Tracer.isDebug()) {
//            UiThreadBlockWatcher.install(16, UiThreadBlockWatcher.TYPE_LOOPER);
//        }

        Utils.init(context);
        ReceiverManager.getInstance().init(context);
        Tracer.init(context);

        StatisticsManager.init();
        ThreadBus.init(context); // 线程池入口函数

//        ImageLoader.init(context);
//        KeepLiveHelper.get().startKeepByAllPolicy(context);
    }

    /**
     * 业务相关的初始化放在这里
     *
     * @param context
     */
    private void initOthers(Context context) {
        SPushService.startPushServer(context);
//        BackgroundTaskScheduleManager.getInstance(context);
//        //检查提醒任务
//        AppAlarmManager.getInstance().addRepeatingAlarm();
//        //设置参数
//        initSettingData();
//        // 初始化天气sdk
//        WeatherSDK.initSDK(context, context.getPackageName());
//
//        DownloadHelper.getInstance().init();
//        DownloadInnerReceiver.register(context);
    }

    private void DeInitialize() {
        if (!sIsInitializeDone) {
            return;
        }
        sIsInitializeDone = false;
    }

}

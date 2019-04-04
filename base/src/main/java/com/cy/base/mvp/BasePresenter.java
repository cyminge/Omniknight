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

import android.app.Activity;
import android.content.Intent;
import android.os.Message;

import com.cy.omniknight.tools.ObjectUtils;
import com.cy.omniknight.tools.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * ================================================
 * 基类 Presenter
 * <p>
 * ================================================
 */
public class BasePresenter<M extends IBaseContract.IModel, V extends IBaseContract.IView> implements IBaseContract.IPresenter {
    protected final String TAG = this.getClass().getSimpleName();
    protected M mModel;

    public V getRootView() {
        return mRootView;
    }

    protected V mRootView;


    /**
     * 如果当前页面同时需要 Model 层和 View 层,则使用此构造函数(默认)
     *
     * @param model
     * @param rootView
     */
    public BasePresenter(M model, V rootView) {
        ObjectUtils.requireNonNull(model, "%s cannot be null" + IBaseContract.IModel.class.getName());
        ObjectUtils.requireNonNull(rootView, "%s cannot be null" + IBaseContract.IView.class.getName());
        this.mModel = model;
        this.mRootView = rootView;
    }

    /**
     * 如果当前页面不需要操作数据,只需要 View 层,则使用此构造函数
     *
     * @param rootView
     */
    public BasePresenter(V rootView) {
        ObjectUtils.requireNonNull(rootView, "%s cannot be null" + IBaseContract.IView.class.getName());
        this.mRootView = rootView;
        this.mModel = (M) SimpleContract.sBaseModel;
    }

//    /**
//     * 后台交互，无需UI,不要扩展太多构造函数，可以通过传递静态的view和model解决
//     */
//    public BasePresenter() {
//    }


    @Override
    public void onCreate() {
        if (useEventBus())//如果要使用 Eventbus 请将此方法返回 true
        {
            try {
                EventBus.getDefault().register(this);//注册 Eventbus
            } catch (Exception e) {
//                LogUtil.ignore(e);
            }
        }
    }

    /**
     * 在框架中 {@link Activity#onDestroy()} 时会默认调用 @link IPresenter#onDestroy()
     */
    @Override
    public void onDestroy() {
        if (useEventBus())//如果要使用 Eventbus 请将此方法返回 true
        {
            try {
                EventBus.getDefault().unregister(this);//解除注册 Eventbus
            } catch (Exception e) {
//                LogUtil.ignore(e);
            }
        }
        if (mModel != null)
            mModel.onDestroy();
        this.mModel = null;
        this.mRootView = null;
    }

    /**
     * 是否使用 {@link EventBus},默认为使用(true)，
     *
     * @return
     */
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceive(Message message) {
        switch (message.what) {
            case EventBusTag.START_ACTIVITY:
                if (message.obj == null)
                    break;
                dispatchStart(message);
                break;
            case EventBusTag.SHOW_SNACK_BAR:
                if (message.obj == null)
                    break;
                showSnackbar((String) message.obj, message.arg1 == 0 ? false : true);
                break;
            case EventBusTag.KILL_ALL:
                killAll();
                break;
            case EventBusTag.APP_EXIT:
                appExit();
                break;
            default:
                LogUtil.d(TAG, "The message.what not match");
                break;
        }
    }

    /**
     * 让在前台的 {@link Activity},使用  显示文本内容
     *
     * @param message
     * @param isLong
     */
    public void showSnackbar(String message, boolean isLong) {
        ToastUtils.showShort(message);
    }


    private void dispatchStart(Message message) {
        if (message.obj instanceof Intent)
            startActivity((Intent) message.obj);
        else if (message.obj instanceof Class)
            startActivity((Class) message.obj);
    }

    public void startActivity(Class activityClass) {
        startActivity(new Intent(GlobalVariable.sAppContext, activityClass));
    }

    /**
     * 让在栈顶的 {@link Activity} ,打开指定的 {@link Activity}
     *
     * @param intent
     */
    public void startActivity(Intent intent) {
//        if (getTopActivity() == null) {
//            Timber.tag(TAG).w("mCurrentActivity == null when startActivity(Intent)");
//            //如果没有前台的activity就使用new_task模式启动activity
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mApplication.startActivity(intent);
//            return;
//        }
//        getTopActivity().startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        GlobalVariable.sAppContext.startActivity(intent);
    }


    /**
     * 销毁所有activity
     */
    private void killAll() {

    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            killAll();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.cy.base.mvp;

import android.support.annotation.NonNull;


public interface IBaseContract {

    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface IView {
        /**
         * 显示加载
         */
        void showLoading();

        /**
         * 隐藏加载
         */
        void hideLoading();

        void showMessage(@NonNull String message);
        /**
         * 显示信息
         *
         * @param message  消息内容, 不能为 {@code null}
         * @param isFinish 如果是activity，是否关闭页面，处理错误消息的时候传递true，默认false
         */
        void showMessage(@NonNull String message, boolean isFinish);
    }

    interface IPresenter {

        /**
         * 做一些初始化操作
         */
        void onCreate();

        void onDestroy();

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface IModel {

        void onDestroy();
    }
}

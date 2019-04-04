package com.cy.base.mvp;

import android.support.annotation.NonNull;


public class SimpleContract {
    public static IBaseContract.IView sView = new IBaseContract.IView() {
        @Override
        public void showLoading() {

        }

        @Override
        public void hideLoading() {

        }

        @Override
        public void showMessage(@NonNull String message) {

        }

        @Override
        public void showMessage(@NonNull String message, boolean isFinish) {

        }
    };

    public static IBaseContract.IModel sBaseModel = new IBaseContract.IModel() {
        @Override
        public void onDestroy() {

        }
    };

    public static BasePresenter sBasePresenter = new BasePresenter(sBaseModel, sView);
}

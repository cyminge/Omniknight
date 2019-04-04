package com.cy.base.demo;

import com.cy.base.callback.ICallback;
import com.cy.base.mvp.IBaseContract;

/**
 * Created by cy on 19-4-3.
 */

public interface IDemoContract {

    interface IDemoView extends IBaseContract.IView {
        void showData(String data);
    }

    interface IDemoModel extends IBaseContract.IModel {
        void requestData(ICallback callback);
    }

    interface IDemoPresenter extends IBaseContract.IPresenter {
        void getData();
    }

}

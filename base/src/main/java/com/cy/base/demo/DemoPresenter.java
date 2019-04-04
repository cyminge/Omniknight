package com.cy.base.demo;

import com.cy.base.callback.ICallback;
import com.cy.base.exception.ResponseException;
import com.cy.base.mvp.BasePresenter;

/**
 * Created by cy on 19-4-3.
 */

public class DemoPresenter extends BasePresenter<IDemoContract.IDemoModel, IDemoContract.IDemoView> implements IDemoContract.IDemoPresenter {

    public DemoPresenter(IDemoContract.IDemoModel model, IDemoContract.IDemoView rootView) {
        super(model, rootView);
    }

    public DemoPresenter(IDemoContract.IDemoView rootView) {
        super(rootView);
    }

    @Override
    public void getData() {
        ICallback<String> callback = new ICallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(ResponseException error) {

            }
        };
        mModel.requestData(callback);
    }
}

package com.cy.base.demo;

import android.text.TextUtils;

import com.cy.base.callback.ICallback;
import com.cy.base.exception.ResponseException;
import com.cy.base.integration.IRepositoryManager;
import com.cy.base.mvp.BaseModel;

/**
 * Created by cy on 19-4-3.
 */

public class DemoModel extends BaseModel<IRepositoryManager> implements IDemoContract.IDemoModel {

    public DemoModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void requestData(final ICallback callback) {
        enqueue(new Runnable() {
            @Override
            public void run() {
                String aa = mRepositoryManager.getSPService().get("", "");
                if(!TextUtils.isEmpty(aa)) {
                    callback.onSuccess(aa);
                } else {
                    callback.onError(new ResponseException("null value"));
                }
            }
        }, callback);

    }
}

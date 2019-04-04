package com.cy.base.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import com.cy.base.exception.ExceptionHandle;

/**
 * 网络请求相关返回值的统一封装
 *
 */
final public class RetrofitNetCallback<T> implements Callback<T> {

    private ICallback<T> mCallback;

    public RetrofitNetCallback(ICallback<T> callback) {
        mCallback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.raw().code() == 200 || response.raw().code() == 206) { // 200 206是服务器有合理响应
            mCallback.onSuccess(response.body());
            return;
        }
        onFailure(call, new HttpException(response));
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        mCallback.onError(ExceptionHandle.handleException(t));
    }
}

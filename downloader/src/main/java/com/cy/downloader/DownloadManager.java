package com.cy.downloader;

import com.cy.downloader.database.entity.DownloadInfo;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.listener.DownloadListener1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cy on 18-9-25.
 */

public class DownloadManager implements IDownload {

    private OkDownloadHelper mOkDownloadHelper;
    private ConcurrentHashMap<String, DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<>();
    private DownloadListener1 mDownloadListener1;

    public DownloadManager(DownloadListener1 downloadListener1) {
        mOkDownloadHelper = OkDownloadHelper.getInstance();
        mDownloadListener1 = downloadListener1;
    }

    @Override
    public void startDownload(DownloadInfo downloadInfo) {
//        DownloadTask task = mDownloadTaskMap.get(downloadInfo.getUrl());
//        if(null != task) {
//            mOkDownloadHelper.resume(task, mDownloadListener1);
//            return;
//        }
        // TODO 处理重定向下载
        DownloadTask task = mOkDownloadHelper.getDownloadTask(downloadInfo.getUrl(), DownloadConstant.getApkDownloadTempFileName(downloadInfo));
        mDownloadTaskMap.put(downloadInfo.getUrl(), task);
        mOkDownloadHelper.enqueue(task, mDownloadListener1);
    }

    @Override
    public void pauseDownload(DownloadInfo downloadInfo) {
        DownloadTask task = mDownloadTaskMap.get(downloadInfo.getUrl());
        if(null != task) {
            mOkDownloadHelper.pause(task);
        }
    }

    @Override
    public void cancelDownload(DownloadInfo downloadInfo) {
        DownloadTask task = mDownloadTaskMap.get(downloadInfo.getUrl());
        if(null != task) {
            mOkDownloadHelper.cancel(task);
            mDownloadTaskMap.remove(downloadInfo.getUrl());
            OkDownloadHelper.getInstance().delete(task);
        }
    }
}

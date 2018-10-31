package com.cy.downloader;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cy.omniknight.tools.Utils;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.listener.DownloadListener1;

import java.io.File;

/**
 * @Created by chenls on 2018/9/10.
 */
public class OkDownloadHelper {
    private static final OkDownloadHelper sInstance = new OkDownloadHelper();

    public static OkDownloadHelper getInstance() {
        return sInstance;
    }

    private OkDownloadHelper() {

    }

    /**
     * 开始下载
     *
     * @param downloadListener DownloadListener1能满足一般业务需求
     */
    public void enqueue(DownloadTask task, DownloadListener1 downloadListener) {
        task.enqueue(downloadListener);
    }

    /**
     * 忽略存在文件，强制重新下载
     *
     * @param url
     * @return
     */
    public DownloadTask getDownloadTaskIgnoreExistingFile(String url) {
        return new DownloadTask.Builder(url, getParentFile(Utils.getApp()))
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(1000)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(true)
                .build();
    }

    public DownloadTask getDownloadTask(String url) {
        return new DownloadTask.Builder(url, getParentFile(Utils.getApp()))
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(1000)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build();
    }

    public DownloadTask getDownloadTask(String url, String fileName) {
        return new DownloadTask.Builder(url, getParentFile(Utils.getApp()))
                .setFilename(fileName)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(1000)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .setConnectionCount(2)
                .build();
    }

    /**
     * 根据下载任务获取短信信息
     *
     * @param task
     * @return
     */
    public BreakpointInfo getCurrentInfo(DownloadTask task) {
        return StatusUtil.getCurrentInfo(task);
    }

    /**
     * 取消下载
     *
     * @param task
     */
    public void cancel(DownloadTask task) {
        task.cancel();
    }

    /**
     * 暂停
     *
     * @param task
     */
    public void pause(DownloadTask task) {
        cancel(task);
    }

    /**
     * 如果文件在下载过程会删除失败
     *
     * @param task
     */
    public void delete(DownloadTask task) {
        try {
            task.getFile().delete();
        } catch (Exception e) {
//            LogUtil.ignore(e);
        }
    }

    /**
     * 恢复下载
     *
     * @param task
     * @param downloadListener
     */
    public void resume(DownloadTask task, DownloadListener1 downloadListener) {
        enqueue(task, downloadListener);
    }

    /**
     * 取消所有
     *
     * @param task
     */
    public void cancelAll(DownloadTask task) {
        OkDownload.with().downloadDispatcher().cancelAll();
    }

    /**
     * 文件下载的路径，可以指向sdcard
     *
     * @param context
     * @return
     */
    public File getParentFile(@NonNull Context context) {
        final File externalSaveDir = context.getExternalCacheDir();
        if (externalSaveDir == null) {
            return context.getCacheDir();
        } else {
            return externalSaveDir;
        }
    }
}

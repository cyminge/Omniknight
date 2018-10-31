package com.cy.downloader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;

/**
 * @Created by chenls on 2018/9/10.
 */
public class Demo {
    public static void main(String[] args) {
        String url = "https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk";
        OkDownloadHelper okDownloadHelper = OkDownloadHelper.getInstance();
        DownloadTask task = okDownloadHelper.getDownloadTask(url);
        //开始下载
        okDownloadHelper.enqueue(task, new DownloadListener1() {
            @Override
            public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {

            }

            @Override
            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

            }

            @Override
            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {

            }
        });

        //取消
        okDownloadHelper.cancel(task);

        //获取已经完成的下载信息，正常同上面的enqueue方法获取相应进度即可，只有在下载中途退出页面，下次再次进来读取进度需要用到
        BreakpointInfo breakpointInfo = okDownloadHelper.getCurrentInfo(task);
        //已经下载的长度
        breakpointInfo.getTotalOffset();
        //总长度
        breakpointInfo.getTotalLength();


        //暂停
        okDownloadHelper.pause(task);

        //恢复
        okDownloadHelper.resume(task, new DownloadListener1() {
            @Override
            public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {

            }

            @Override
            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

            }

            @Override
            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {

            }
        });
    }
}

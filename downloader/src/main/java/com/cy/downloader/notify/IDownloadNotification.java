package com.cy.downloader.notify;

import com.cy.downloader.database.entity.DownloadInfo;

import java.util.ArrayList;

public interface IDownloadNotification {
    void showNotification(ArrayList<DownloadInfo> downloadInfoList);
    void showNotification(DownloadInfo downloadInfo);
    void cancelNotification(int id);
}

package com.cy.downloader;

import com.cy.downloader.database.entity.DownloadInfo;

/**
 * Created by cy on 18-9-25.
 */

public interface IDownload {
    void startDownload(final DownloadInfo downloadInfo);
    void pauseDownload(final DownloadInfo downloadInfo);
    void cancelDownload(final DownloadInfo downloadInfo);
}

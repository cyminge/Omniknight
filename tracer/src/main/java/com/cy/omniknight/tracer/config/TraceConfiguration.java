package com.cy.omniknight.tracer.config;

import android.content.Context;
import com.cy.omniknight.tracer.util.StorageUtils;

public final class TraceConfiguration {
	
    public static final String LOG_TYPE_VERBOSE = "verbose";
    public static final int LOG_LEVEL_VERBOSE = 1;
    public static final String LOG_TYPE_DEBUG = "debug";
    public static final int LOG_LEVEL_DEBUG = 2;
    public static final String LOG_TYPE_INFO = "info";
    public static final int LOG_LEVEL_INFO = 3;
    public static final String LOG_TYPE_WARN = "warn";
    public static final int LOG_LEVEL_WARN = 4;
    public static final String LOG_TYPE_ERROR = "error";
    public static final int LOG_LEVEL_ERROR = 5;
    public static final String LOG_TYPE_UNCATCHED = "uncatched";
    public static final int LOG_LEVEL_UNCATCHED = 6;

    private final String mHomePath;
    private final boolean mIsAllowReportByMobileNet;
    private final boolean mIsPrintLogCat;
    private final boolean mIsSaveFile;
    private final boolean mIsReportLog;
    private final int mSaveLevel;
    private final int mPersistTriggerLevel;
    
    public static String convertLevel(int level) {
        String levelString = null;
        switch (level) {
            case LOG_LEVEL_VERBOSE:
                levelString = LOG_TYPE_VERBOSE;
                break;
            case LOG_LEVEL_INFO:
                levelString = LOG_TYPE_INFO;
                break;
            case LOG_LEVEL_DEBUG:
                levelString = LOG_TYPE_DEBUG;
                break;
            case LOG_LEVEL_WARN:
                levelString = LOG_TYPE_WARN;
                break;
            case LOG_LEVEL_ERROR:
                levelString = LOG_TYPE_ERROR;
                break;
            case LOG_LEVEL_UNCATCHED:
                levelString = LOG_TYPE_UNCATCHED;
                break;
            default:
                levelString = "unkown";
                break;
        }
        
        return levelString;
    }

    public String getHomePath() {
        return mHomePath;
    }

    public boolean isAllowReportByMobileNet() {
        return mIsAllowReportByMobileNet;
    }

    public boolean isPrintLogCat() {
        return mIsPrintLogCat;
    }

    public boolean isSaveFile() {
        return mIsSaveFile;
    }

    public boolean isReportLog() {
        return mIsReportLog;
    }

    public int getSaveLevel() {
        return mSaveLevel;
    }

    public int getPersistTriggerLevel() {
        return mPersistTriggerLevel;
    }

    private TraceConfiguration(final Builder builder) {
        mHomePath = builder.mHomePath;
        mIsAllowReportByMobileNet = builder.mIsAllowByMobileNet;
        mIsPrintLogCat = builder.mIsPrintLogCat;
        mIsSaveFile = builder.mIsSaveFile;
        mIsReportLog = builder.mIsReportLog;
        mSaveLevel = builder.mSaveLevel;
        mPersistTriggerLevel = builder.mPersistTriggerLevel;
    }

    public static class Builder {

        private Context mContext;

        public Builder(Context context) {
            mContext = context;
            mHomePath = StorageUtils.getOwnCacheDirectory(mContext, "cyTest").getPath();
        }

        private String mHomePath = null;
        private boolean mIsAllowByMobileNet = true;
        private boolean mIsPrintLogCat = false;
        private boolean mIsSaveFile = false;
        private boolean mIsReportLog = false;
        private int mSaveLevel = LOG_LEVEL_DEBUG;
        private int mPersistTriggerLevel = LOG_LEVEL_DEBUG;

        public Builder appFilePath(String filePath) {
            mHomePath = filePath;
            return this;
        }

        public Builder isAllowByMobileNet(boolean allow) {
            mIsAllowByMobileNet = allow;
            return this;
        }

        public Builder isPrintLogCat(boolean isPrintLogCat) {
            mIsPrintLogCat = isPrintLogCat;
            return this;
        }

        public Builder isSaveFile(boolean isSaveFile) {
            mIsSaveFile = isSaveFile;
            return this;
        }

        public Builder isReportLog(boolean isReportLog) {
            mIsReportLog = isReportLog;
            return this;
        }

        public Builder saveLevel(int level) {
            mSaveLevel = level;
            return this;
        }

        public Builder persistTriggerLevel(int level) {
            mPersistTriggerLevel = level;
            return this;
        }

        public TraceConfiguration build() {
            return new TraceConfiguration(this);
        }

    }
}

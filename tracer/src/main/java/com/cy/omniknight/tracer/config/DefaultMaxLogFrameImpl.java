package com.cy.omniknight.tracer.config;

import com.cy.omniknight.tracer.LogToFile;
import com.cy.omniknight.tracer.export.IMaxLogFrame;

public class DefaultMaxLogFrameImpl implements IMaxLogFrame {

    @Override
    public String getLogPath() {
        return LogToFile.getDefLogPath();
    }

    @Override
    public int saveLogToFile(String jsonString) {
        return 0;
    }

    @Override
    public int reportToServer(String jsonString) {
        return 0;
    }

    @Override
    public int reportToServer(String reportType, String logName, String jsonString) {
        return 0;
    }

}

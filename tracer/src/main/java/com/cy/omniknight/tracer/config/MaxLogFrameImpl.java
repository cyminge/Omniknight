package com.cy.omniknight.tracer.config;

import android.util.Log;
import com.cy.omniknight.tracer.LogToFile;
import com.cy.omniknight.tracer.export.IMaxLogFrame;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class MaxLogFrameImpl implements IMaxLogFrame {
    private static final String THIS_FILE = "OnMaxLogFrameDef";

    @Override
    /**
     * 将log保存到本地文件
     */
    public int saveLogToFile(final String jsonString) {
        String logPath = LogToFile.getDefLogPath();
        int result = -1;
        if (logPath != null) {
            FileWriter fos = null;
            try {
                fos = new FileWriter(logPath, true);
                fos.append(jsonString);
                fos.flush();
                result = 0;
                Log.d(THIS_FILE, "flushTrace done");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                    Log.d(THIS_FILE, "flushTrace close");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public String getLogPath() {
        return LogToFile.getDefLogPath();
    }

    @Override
    public int reportToServer(final String jsonString) {
        return reportToServer("auto", "log" + System.currentTimeMillis(), jsonString);
    }

    @Override
    public int reportToServer(final String reportType, final String logName, final String jsonString) {
        return 0;
    }
}

package com.cy.omniknight.tools.fluency;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

public class MessageDispatchTimeStackPrinter {
    private long mTimeBound;
    private static MessageDispatchTimeStackPrinter printer = new MessageDispatchTimeStackPrinter();
    private static PrintTask mTask = new PrintTask();
    private Handler mLogHandler;
    private MessageDispatchTimeStackPrinter() {
        HandlerThread mLogThread = new HandlerThread("log");
        mLogThread.start();
        mLogHandler = new Handler(mLogThread.getLooper());
    }

    private static class PrintTask implements Runnable {

        @Override
        public void run() {
            StackTraceElement[] elements = Looper.getMainLooper().getThread().getStackTrace();
            StringBuilder builder = new StringBuilder();
            for (int i = 0;i<elements.length;i++) {
                builder.append(elements[i].toString().concat("\r\n"));
            }

            Log.i("cyTest", builder.toString());
        }
    }

    public static MessageDispatchTimeStackPrinter getInstance(long timeBound) {
        printer.mTimeBound = timeBound;
        return printer;
    }

    public void startPrint() {
        mLogHandler.postDelayed(mTask,mTimeBound);
    }

    public void stopPrint() {
        mLogHandler.removeCallbacks(mTask);
    }
}

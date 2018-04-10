package com.cy.omniknight.tools.fluency;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;
import android.view.Choreographer;

public class UiThreadBlockWatcher {

    public static final int TYPE_LOOPER = 0;
    public static final int TYPE_CHOREOGRAPHER = 1;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void install(final long timeBound, int type) {
        switch (type) {
            case TYPE_LOOPER:
                Looper.getMainLooper().setMessageLogging(new Printer() {
                    @Override
                    public void println(String x) {
                        if (x.contains(">>>>> Dispatching to ")) {
                            MessageDispatchTimeStackPrinter.getInstance(timeBound).startPrint();
                        } else if (x.contains("<<<<< Finished to ")) {
                            MessageDispatchTimeStackPrinter.getInstance(timeBound).stopPrint();
                        }
                    }
                });
                break;
            case TYPE_CHOREOGRAPHER:
                Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
                    @Override
                    public void doFrame(long frameTimeNanos) {
                        Log.i("cyTest", frameTimeNanos + "");
                        Choreographer.getInstance().postFrameCallback(this);
                        /*MessageDispatchTimeStackPrinter.getInstance(timeBound).stopPrint();
                        MessageDispatchTimeStackPrinter.getInstance(timeBound).startPrint();
                        Choreographer.getInstance().postFrameCallback(this);*/
                    }
                });
                break;
            default:
                break;
        }
    }
}

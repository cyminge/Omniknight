package com.cy.statistics;

import android.content.Context;

import com.cy.omniknight.tools.AppUtils;
import com.cy.omniknight.tools.ObjectUtils;
import com.cy.omniknight.tools.Utils;
import com.cy.omniknight.tools.receiver.ConnectivityController;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * Created by cy on 18-8-29.
 */

public final class StatisticsManager implements ICommonStatistics {

    private static final String TAG = "StatisticsManager";

    private static StatisticsManager sStatisticsManager;

    private Stack<String> mStack;

    private boolean mIsBooted;

    private Statistics mStatistics;

    private StatisticsManager() {
        mStack = new Stack<>();
        mStatistics = new Statistics();
        Utils.getApp().registerActivityLifecycleCallbacks(new StatisticsActivityLifecycleCallbacks(this));
    }

    public static StatisticsManager getInstance() {
        if (sStatisticsManager == null) {
            synchronized (Statistics.class) {
                if (sStatisticsManager == null) {
                    sStatisticsManager = new StatisticsManager();
                }
            }
        }
        return sStatisticsManager;
    }

    public static void init() {
        getInstance();
    }

    public void onActivityCreate() {
        if (!mIsBooted) {
            mIsBooted = true;
            StatisticsManager.getInstance().send(StatisticsKey.BOOT);
        }
    }

    /**
     * 入栈
     *
     * @param source
     */
    public void pushSource(String source) {
        if (hasSource(source)) {
            return;
        }

        mStack.push(source);
    }

    private boolean hasSource(String source) {
        return mStack.contains(source);
    }

    /**
     * 出栈
     */
    public void popSource() {
        if (mStack.isEmpty()) {
            return;
        }

        try {
            mStack.pop();
        } catch (EmptyStackException e) {
        }
    }

    public void removeFirstSource() {
        if (mStack.isEmpty()) {
            return;
        }

        try {
            mStack.removeElementAt(0);
        } catch (EmptyStackException e) {
        }
    }

    /**
     * 取出不移除
     *
     * @return
     */
    private String peekSource() {
        if (mStack.isEmpty()) {
            return "";
        }

        try {
            return mStack.peek();
        } catch (EmptyStackException e) {
            return "";
        }
    }

    /**
     * 获取上一级来源
     *
     * @return
     */
    public String getPreSource() {
        if (mStack.isEmpty()) {
            return "";
        }

        try {
            if (mStack.size() == 1 || mStack.peek().contains(StatisticsKey.CONN_SYMBOL)) {
                return getPreSource(mStack.peek());
            } else {
                // 这里有同步问题，会出现数组越界，现在通过catch exception的方式解决，后面是否要加同步方法？
                return getCurSource(mStack.elementAt(mStack.size() - 2));
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取上一级来源
     *
     * @param source
     * @return
     */
    public String getPreSource(String source) {
        if (source == null) {
            return null;
        }

        String preSource = source;
        int index = source.indexOf(StatisticsKey.CONN_SYMBOL);
        if (index > 0) {
            preSource = source.substring(0, index);
        }
        return preSource;
    }

    /**
     * 获取当前来源
     *
     * @return
     */
    public String getCurSource() {
        return getCurSource(peekSource());
    }

    /**
     * 获取当前来源
     *
     * @param source
     * @return
     */
    public String getCurSource(String source) {
        if (source == null) {
            return null;
        }

        String curSource = source;
        int index = source.lastIndexOf(StatisticsKey.CONN_SYMBOL);
        if (index > 0) {
            curSource = source.substring(index + 1, curSource.length());
        }
        return curSource;
    }

    public void send(String action) {
        send(action, getPreSource());
    }

    public void send(String action, Map<String, String> para) {
        send(action, getPreSource(), getCurSource(), para);
    }

    public void send(String action, String preSource) {
        send(action, preSource, getCurSource());
    }

    public void send(String action, String preSource, String currSource) {
        send(action, preSource, currSource, null);
    }

    public void send(String action, String preSource, String currSource, Map<String, String> params) {
        try {
            HashMap<String, Object> map = getDataMap(Utils.getApp(), currSource, preSource, params);
//            if (LogUtil.isDebug()) {
//                print(action, map);
//            }
            mStatistics.onEvent(Utils.getApp(), action, StatisticsKey.getLabel(action), map);
        } catch (Exception e) {
//            LogUtil.w(TAG, "Send Statistics Error --> " + e.getMessage());
        }
    }

    private HashMap<String, Object> getDataMap(Context context, String currSource, String preSource, Map<String, String> dataMap) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(StatisticsKey.CURR_SOURCE, currSource);
        map.put(StatisticsKey.PRE_SOURCE, preSource);
        StringBuffer clientPkg = new StringBuffer(AppUtils.getAppPackageName()).append("_").append(AppUtils.getAppVersionName());
        map.put(StatisticsKey.CLIENT_PKG, clientPkg.toString());
//        map.put(StatisticsKey.ACCOUNT_NAME, getAccount());
//        map.put(StatisticsKey.ACCOUNT_ID, getUUID());
        map.put(StatisticsKey.NETWORK, ConnectivityController.getNetworkType());
//        map.put(StatisticsKey.THIRD_ENTRY_FLAG, StatisSourceManager.getInstance().getThirdEntryStatisFlag());
//        map.put(StatisticsKey.DESKTOP_REMIND, StatisSourceManager.getInstance().getDesktopRemindStatisFlag());
        if (ObjectUtils.isNotEmpty(dataMap)) {
            map.putAll(dataMap);
        }
        return map;
    }

    private void print(String action, HashMap<String, Object> map) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        String currSource = "";
        String preSource = "";
        String clientPkg = "";
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.equals(StatisticsKey.CURR_SOURCE)) {
                currSource = value == null ? "" : String.valueOf(value);
                continue;
            } else if (key.equals(StatisticsKey.PRE_SOURCE)) {
                preSource = value == null ? "" : String.valueOf(value);
                continue;
            } else if (key.equals(StatisticsKey.CLIENT_PKG)) {
                clientPkg = value == null ? "" : String.valueOf(value);
                continue;
            }

            stringBuffer.append(key).append("=").append(value);
            if (iterator.hasNext()) {
                stringBuffer.append("&");
            }
        }
//        LogUtil.i(TAG, "==>> action=" + action + "-> currSource=" + currSource + "&preSource=" + preSource + "&clientPkg=" + clientPkg + stringBuffer.toString() + "&");
    }

    @Override
    public void onResume(Context context) {
        mStatistics.onResume(context);
    }

    @Override
    public void onPause(Context context) {
        mStatistics.onPause(context);
    }

    @Override
    public void onPageStart(Context context, String pageName) {
        mStatistics.onPageStart(context, pageName);
    }

    @Override
    public void onPageEnd(Context context, String pageName) {
        mStatistics.onPageEnd(context, pageName);
    }
}

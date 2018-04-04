package com.gionee.threadbus.scheduler.controller;

/**
 * 调整线程池大小的规则 Created by JLB6088 on 2017/7/17.
 */

public final class Ruler {

    // network state
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_GOOD = 1;
    public static final int NETWORK_TYPE_COMMON = 2;
    public static final int NETWORK_TYPE_POOR = 3;

    // the dependence of change pool size.
    private boolean mRequiresBatteryChange;
    private boolean mRequiresActivityLifecycleChange;
    private boolean mRequiresCpuChange;
    private boolean mRequiresNetworkChange;

    // current state
    private int mNetworkType;
    private boolean mIsLowBattery;
    private boolean mIsOnForeground;
    private boolean mIsCpuBusy;

    private Ruler(Builder builder) {
        mRequiresBatteryChange = builder.mRequiresBatteryChange;
        mRequiresActivityLifecycleChange = builder.mRequiresActivityLifecycleChange;
        mRequiresCpuChange = builder.mRequiresCpuChange;
        mRequiresNetworkChange = builder.mRequiresNetworkChange;
    }

    public boolean isRequiresNetworkChange() {
        return mRequiresNetworkChange;
    }

    public boolean isRequiresBatteryChange() {
        return mRequiresBatteryChange;
    }

    public boolean isRequiresActivityLifecycleChange() {
        return mRequiresActivityLifecycleChange;
    }

    public boolean isRequiresCpuChange() {
        return mRequiresCpuChange;
    }

    public boolean setNetworkType(int networkType) {
        if (mNetworkType == networkType) {
            return false;
        }

        mNetworkType = networkType;
        return true;
    }

    public boolean setIsLowBattery(boolean isLowBattery) {
        if (mIsLowBattery == isLowBattery) {
            return false;
        }

        mIsLowBattery = isLowBattery;
        return true;
    }

    public boolean setIsOnForeground(boolean isOnForeground) {
        if (mIsOnForeground == isOnForeground) {
            return false;
        }

        mIsOnForeground = isOnForeground;
        return true;
    }

    public boolean setIsCpuBusy(boolean isCpuBusy) {
        if (mIsCpuBusy == isCpuBusy) {
            return false;
        }

        mIsCpuBusy = isCpuBusy;
        return true;
    }

    public boolean isLowBattery() {
        return mIsLowBattery;
    }

    public int getNetworkType() {
        return mNetworkType;
    }

    public boolean isOnForeground() {
        return mIsOnForeground;
    }

    public boolean isCpuBusy() {
        return mIsCpuBusy;
    }

    @Override
    public String toString() {
        return "Ruler-->isLowBattery:" + mIsLowBattery + ", isOnForeground:" + mIsOnForeground
                + ", isCpuBusy:" + mIsCpuBusy + ", networkType:" + mNetworkType;
    }

    public static final class Builder {
        // Requirements.
        private boolean mRequiresBatteryChange = true;
        private boolean mRequiresActivityLifecycleChange = true;
        private boolean mRequiresCpuChange = true;
        private boolean mRequiresNetworkChange = true;

        public Builder setRequiredNetwork(boolean requiresNetwork) {
            mRequiresNetworkChange = requiresNetwork;
            return this;
        }

        public Builder setRequiresBattery(boolean requiresBattery) {
            mRequiresBatteryChange = requiresBattery;
            return this;
        }

        public Builder setRequiresActivityLifecycle(boolean requiresActivityLifecycle) {
            mRequiresActivityLifecycleChange = requiresActivityLifecycle;
            return this;
        }

        public Builder setRequiresCpu(boolean requiresCpu) {
            mRequiresCpuChange = requiresCpu;
            return this;
        }

        public Ruler build() {
            return new Ruler(this);
        }
    }

}

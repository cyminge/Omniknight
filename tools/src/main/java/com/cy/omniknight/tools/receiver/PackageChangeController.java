package com.cy.omniknight.tools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by JLB6088 on 2017/7/12.
 */

public final class PackageChangeController extends ReceiverStateController {

    private PackageChangeTracer mPackageChangeTracer;

    public PackageChangeController(Context context) {
        super(context);
    }

    @Override
    public void startTracking(StateChangedListener stateChangedListener) {
        mStateChangedListenerArray.add(stateChangedListener);

        if (null == mPackageChangeTracer) {
            mPackageChangeTracer = new PackageChangeTracer();
            mPackageChangeTracer.startTracking();
        }
    }

    @Override
    public void stopTracking(StateChangedListener stateChangedListener) {
        mStateChangedListenerArray.remove(stateChangedListener);
        if(mStateChangedListenerArray.size() == 0) {
            if (null != mPackageChangeTracer) {
                mPackageChangeTracer.stopTracking();
                mPackageChangeTracer = null;
            }
        }

    }

    public class PackageChangeTracer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            onPackageChanged(intent);
        }

        private void startTracking() {
            IntentFilter filter = getFilter();
            mContext.registerReceiver(this, filter);
        }

        private IntentFilter getFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            filter.addAction(Intent.ACTION_PACKAGE_INSTALL);
            filter.addDataScheme("package");
            return filter;
        }

        private void stopTracking() {
            mContext.unregisterReceiver(this);
        }
    }

    public void onPackageChanged(Intent intent) {
        if(null == mStateChangedListenerArray || mStateChangedListenerArray.isEmpty()) {
            return;
        }

        synchronized (mStateChangedListenerArray) {
            for(int i=0, size = mStateChangedListenerArray.size(); i< size; i++) {
                mStateChangedListenerArray.get(i).onStateChanged(ReceiverManager.CHANGE_TYPE_PACKAGE, intent.getAction(), intent.getData().getSchemeSpecificPart());
            }
        }
    }
}

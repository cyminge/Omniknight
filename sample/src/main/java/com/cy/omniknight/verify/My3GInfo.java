package com.cy.omniknight.verify;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;

/*
 * tel.getNetworkOperator()
 3G中国是460固定的,
 中国移动的是 46000
 中国联通的是 46001
 中国电信的是 46003
 *获取国别
 tel.getSimCountryIso()
 */

public class My3GInfo {
	private WifiManager mWifi;
	private Context mContext;
	private Handler mHandler;
	private ImageView img;
	private String STRNetworkOperator[] = { "46000", "46001", "46003" };
	private int mark = -1;
	private int position;
	private int img3g[] = {};
	private boolean is3Ghave = false;
	private TelephonyManager tel;
	
	public My3GInfo(Context context, ImageView img) {
		mContext = context;
		this.img = img;
		init();
	}

	public My3GInfo(Context context) {
		mContext = context;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		/* 可 参考一下相关的方法，得到自己想要的参数来处理自己的ui
		 public void initListValues(){            
		         tel.getDeviceId());//获取设备编号
		         tel.getSimCountryIso());//获取SIM卡国别
		         tel.getSimSerialNumber());//获取SIM卡序列号    
		         (simState[tm.getSimState()]);//获取SIM卡状态
		         (tel.getDeviceSoftwareVersion()!=null?tm.getDeviceSoftwareVersion():"未知"));    //获取软件版本
		         tel.getNetworkOperator());//获取网络运营商代号
		         tel.getNetworkOperatorName());//获取网络运营商名称
		         (phoneType[tm.getPhoneType()]);//获取手机制式
		         tel.getCellLocation().toString());//获取设备当前位置
		    }*/
		tel = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

		firstView();
		getmark();
		//设置监听事件，监听信号强度的改变和状态的改变
		tel.listen(new PhoneStateMonitor(),
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
						| PhoneStateListener.LISTEN_SERVICE_STATE);
	}

	private void getmark()//得到当前电话卡的归属运营商
	{
		String strNetworkOperator = tel.getNetworkOperator();
		if (strNetworkOperator != null) {
			for (int i = 0; i < 3; i++) {
				if (strNetworkOperator.equals(STRNetworkOperator[i])) {
					mark = i;
					Log.v(TAG, "mark==" + i);
					break;
				}
			}
		} else {
			mark = -1;
		}
	}

	private void firstView() {//第一次自动检测并设置初始状态

		int state = tel.getSimState();
		switch (state) {
		case TelephonyManager.SIM_STATE_UNKNOWN:
			is3Ghave = false;
			break;
		case TelephonyManager.SIM_STATE_READY:
			is3Ghave = true;
			break;
		case TelephonyManager.SIM_STATE_ABSENT:
			is3Ghave = false;
			break;
		default:
			break;
		}
	}

	private String TAG = "WHLOG";

	class PhoneStateMonitor extends PhoneStateListener {
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {//3g信号强度的改变
			super.onSignalStrengthsChanged(signalStrength);
			Log.i(TAG, "onSignalStrengthsChanged");
			mSignalStrength = signalStrength;
			updateSignalStrength();
		}

		public void onServiceStateChanged(ServiceState serviceState) {//3g状态的改变
			super.onServiceStateChanged(serviceState);
			Log.i(TAG, "onServiceStateChanged");
			updateSignalStrength();
		}
		
	}

	SignalStrength mSignalStrength = null;

	void updateSignalStrength() {
		boolean isGsmSignal = true;
		int signalDbm = 0;
		int signalAsu = 0;
		int signalDbmEvdo = 0;
		if (null != mSignalStrength) {
			isGsmSignal = mSignalStrength.isGsm();
			Log.d(TAG, "isGsmSignal:" + isGsmSignal);
			if (isGsmSignal) {
				int signalStrength = mSignalStrength.getGsmSignalStrength();
				Log.d(TAG, "signalStrength:" + signalStrength);
				int signalStrengthValue = 0;
				if (signalStrength != 99)
					signalStrengthValue = signalStrength * 2 - 113;
				else
					signalStrengthValue = signalStrength;
				Log.i(TAG, "signalStrengthValue:" + signalStrengthValue);
			} else {
				signalDbm = mSignalStrength.getCdmaDbm();
				signalDbmEvdo = mSignalStrength.getEvdoDbm();
				Log.i(TAG, "signalDbm:" + signalDbm + ", signalDbmEvdo:" + signalDbmEvdo);
			}
		}
	}

}
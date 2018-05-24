package com.cy.omniknight.socket.transport;


import android.content.Context;

import com.cy.omniknight.socket.message.MCache;
import com.cy.omniknight.socket.message.MEndPoint;
import com.cy.omniknight.socket.message.MMessage;
import com.cy.omniknight.socket.util.MMessageUtil;

import java.net.InetSocketAddress;

public class UdpProtoBufTransPoint implements MTransPoint {

	private LongConnectCenter mLongConnectCenter;
	private MCache mCache;
	private Context mContext;
	private String mAddress;
	private int mPort;
	private boolean mIsSSL;

	public UdpProtoBufTransPoint(Context context, MCache cache, String address, int port, boolean isSSL) {
		mContext = context;
		mCache = cache;
		mAddress = address;
		mPort = port;
		mIsSSL = isSSL;
	}

	@Override
	public MEndPoint genEndPoint(String target) {
		return null;
	}

	@Override
	public String getTypeName() {
		return MMessageUtil.TRANS_UDP;
	}

	@Override
	public int sendMessage(MMessage message) {
		if (message == null) {
			return TransResult.ERR_EMPTY_MSG;
		}
		mLongConnectCenter.sendMessage(message);
		return 0;
	}

	@Override
	public boolean isValidEndPoint(MEndPoint endPoint) {
		return true;
	}

	@Override
	synchronized public void initital() {
		/* 线程未创建 */
		if (null == mLongConnectCenter) {
			mLongConnectCenter = new LongConnectCenter(mContext, mAddress, mPort, mIsSSL);
		}
		mLongConnectCenter.startWork();
//		/* 线程未启动，启动线程 */
//		if (!mLongConnectCenter.isAlive()) {
//			mLongConnectCenter.startWork();
//		} else {
//			mLongConnectCenter.connect(mAddress);
//		}

//		mLongConnectCenter.startWork();
//		mLongConnectCenter.keepEstablish();
	}

	@Override
	synchronized public void deinitial() {
		if(null != mLongConnectCenter) {
			mLongConnectCenter.stopWork();
			mLongConnectCenter = null;
		}


	}

	@Override
	public boolean isEnable() {
		if (null == mLongConnectCenter) {
			return false;
		}

		if (!mLongConnectCenter.isAlive()) {
			return false;
		}

		if (!mLongConnectCenter.isEstablished()) {
			return false;
		}

		return true;
	}

	@Override
	public void establishTrigger() {
		if (null == mLongConnectCenter) {
			initital();
		} else {
			mLongConnectCenter.keepEstablish();
		}
	}

}

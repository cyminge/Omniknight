package com.cy.omniknight.tools.network;

public class NetworkEvent {

	public static final int NWK_NONE = -1;

	public static final int NWK_WIFI = 1;
	public static final int NWK_MOBILE = 2;
	public static final int NWK_UNKNOWED = 3;

	// 信号状态
	public static final int NWK_NORMAL = 10;
	public static final int NWK_WEAK = 11;

	private int netState;

	public NetworkEvent(int netState) {
		this.netState = netState;
	}

	public int getNetState() {
		return netState;
	}

}

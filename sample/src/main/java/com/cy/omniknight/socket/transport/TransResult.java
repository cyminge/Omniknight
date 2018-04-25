package com.cy.omniknight.socket.transport;

/**
 * 传输结果描述
 * 
 * @author liuzf1986
 * @time 2014-2-11
 */
public class TransResult {
	public static final int ERR_NORMAL = 0;
	public static final int ERR_EMPTY_MSG = -1;
	/* 找不到对应的传输节点实例，或者节点不可用 */
	public static final int ERR_INVALID_TRANSTYPE = -2;
	public static final int ERR_INVALID_ENDPOINT = -3;
	public static final int ERR_SEND_TIMEOUT = -4;
	public static final int ERR_UNSUPPORT_FORMAT = -5;
	// 不符合穿行化，一样的消息，或串行消息
	public static final int ERR_MSG_SERILIZATION = -6;  
}

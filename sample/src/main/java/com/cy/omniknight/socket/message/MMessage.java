package com.cy.omniknight.socket.message;

import java.io.Serializable;

/**
 */
public class MMessage implements Serializable {
	
	public static enum MResponseDependent {
		/* 无返回 */
		NO_RESPONSE,
		/* 无返回，触发上层超时 */
		NO_RESPONSE_TIMEOUT,
		/* 无返回，触发超时，重置长连接 */
		NO_RESPONSE_RESET
	}

	private static final long serialVersionUID = 1L;

	public static final int MESSAGE_SEND_TIMEOUT_WIFI = 4000;
	public static final int MESSAGE_SEND_TIMEOUT_3G = 7000;
	public static final int MESSAGE_SEND_TIMEOUT_2G = 10000;

	
	private static int MESSAGE_SEND_RETRY = 1; // 重发1次，一共2次
	private static int MESSAGE_SEND_TIMEOUT = MESSAGE_SEND_TIMEOUT_3G;

	/**
	 * 设置重发次数
	 * 
	 * @param retry 重发的次数，不包括正常发送次数，即实际一共发送(retry+1)次
	 */
	public static void setGlobalSendRetry(int retry) {
		MESSAGE_SEND_RETRY = retry;
	}

	/**
	 * 设置重发超时时间
	 * 
	 * @param timeMillSec
	 */
	public static void setGlobalSendTimeOut(int timeMillSec) {
		MESSAGE_SEND_TIMEOUT = timeMillSec;
	}

	private String strMsgId; 		           // 消息id
	private MResponseDependent rspDependent;   // 响应依赖级别，表示是否需要返回
	private MEndPoint epTarget; 	           // 传输端点
	private String strProtoName;	           // 传输消息格式类型，对外发送时，该类型需预先注册到MProxy中
	private Serializable msgObj;		       // 消息对象
	private int transTimes;		               // 已经发送的次数
	private long createTime;		           // 消息创建时间，用于消息发送超时判断
	private long transTimeOut = MESSAGE_SEND_TIMEOUT;   // 发送超时时间，毫秒
	private int transRetry = MESSAGE_SEND_RETRY;        // 发送超时重试次数
	private long transExpired = transTimeOut * (transRetry + 1);   // 发送失败时间，毫秒
	private boolean doubleSend = false;        // 为了提高命中率，可以多发送一次消息
	

	/**
	 * * 构造待发送消息
	 * 
	 * @param transType 传输方式
	 * @param target 传输目标
	 * @param msg 格式化的消息内容
	 * @param protocol 序列化工具标识名
	 * @param rspDep 是否通知上层
	 */
	public MMessage(String transType, String target, Serializable msg, String protocol, MResponseDependent rspDep) {
		this(new MEndPoint(transType, target), msg, protocol,rspDep);
	}

	public MMessage(MEndPoint endPoint, Serializable msg, String protocol, MResponseDependent rspDep) {
		this.epTarget = endPoint;
		this.msgObj = msg;
		this.strProtoName = protocol;
		this.transTimes = 0;
		this.createTime = System.currentTimeMillis();
		this.rspDependent = rspDep;
	}
	
	public MResponseDependent getResponseDependent() {
		return this.rspDependent;
	}

	/*--------------------------Gettors/Settors------------------------------- */
	public MEndPoint getEndPoint() {
		return epTarget;
	}

	public String getProtocolName() {
		return strProtoName;
	}

	public Serializable getMessageObject() {
		return msgObj;
	}

	public String getMsgId() {
		return strMsgId;
	}

	public void setMsgId(String msgId) {
		this.strMsgId = msgId;
	}

	public void incTransTimes() {
		transTimes++;
	}

	public int getTransTimes() {
		return this.transTimes;
	}

	public long getCreateTime() {
		return this.createTime;
	}

	public long getTransTimeout() {
		return transTimeOut;
	}

	public void setTransTimeout(long timeOutMills) {
		transTimeOut = timeOutMills;
		transExpired = transTimeOut * (transRetry + 1);
	}

	public long getTransRetry() {
		return transRetry;
	}

	public void setTransRetry(int retry) {
		transRetry = retry;
		transExpired = transTimeOut * (transRetry + 1);
	}

	public long getTransExpired() {
		return transExpired;
	}

	public boolean isExprired() {
		return transTimes > transRetry || (System.currentTimeMillis() - createTime) > transExpired;
	}

	public void setDoubleSend(boolean ds) {
		doubleSend = ds;
	}

	public boolean getDoubleSend() {
		return doubleSend;
	}
}

package com.cy.omniknight.socket.util;

import com.cy.omniknight.socket.message.MMessage;
import com.google.protobuf.GeneratedMessage;

/**
 * 提供产生不同协议，不同传输端点的消息的接口工具类
 * 
 * @author liuzf1986
 * @time 2014-2-18
 */
public class MMessageUtil {
	/* 端点名常量 */
	public static final String TRANS_SMS = "sms";
	public static final String TRANS_TCP = "tcp";
	public static final String TRANS_UDP = "udp";
	/* 协议名常量 */
	public static final String PROTO_JSON = "json";
	public static final String PROTO_AS1 = "as1";
	public static final String PROTO_PROTOBUF = "protobuf";

	public static int requestId = 1; // 客户端发送信息的ID
	
	public static String getTransType() {
		return TRANS_UDP;
	}

	public static MMessage getUdpProbufReq(GeneratedMessage protoMsg, MMessage.MResponseDependent rspDep) {
		return new MMessage(TRANS_UDP, null, protoMsg, PROTO_PROTOBUF, rspDep);
	}
	
//	public static MMessage getUdpProbufReq(int requestSeq, int cmd, byte[] data) {
//		int seq = requestSeq;
//		NetPack.Request req = new NetPack.Request(seq, cmd, data);
//		return getUdpReqProbuf("0", req, false); //不带ack toast
//	}
//
//	public static MMessage getUdpProbufRep(Request req) {
//		int seq = 0; //requestId++;
//		NetPack.Response rep = new NetPack.Response(req);
//		return getUdpReqProbuf(String.valueOf(seq), rep, false); //不带ack toast
//	}
//
//	public static MMessage getUdpProbufRep(int requestSeq, int cmd) {
//		return getUdpProbufRep(requestSeq, cmd, null);
//	}
//
//	public static MMessage getUdpProbufRep(int requestSeq, int cmd, byte[] data) {
//		int seq = 0; //requestId++;
//		NetPack.Response rep;
//		if (null == data) {
//			rep = new NetPack.Response(requestSeq, cmd);
//		} else {
//			rep = new NetPack.Response(requestSeq, cmd, data);
//		}
//		return getUdpReqProbuf(String.valueOf(seq), rep, false); //不带ack toast
//	}
//
//	/* 生成 Message */
//	/*------------------------------------------------------ */
//	private static MMessage getUdpReqProbuf(String messageId, Serializable msgObj, boolean needToast) {
//		return new MMessage(messageId, TRANS_UDP, null, msgObj, PROTO_PROTOBUF, needToast);
//	}
//
//	private static MMessage getTcpProbuf(String messageId, Serializable msgObj, boolean needToast) {
//		return new MMessage(messageId, TRANS_TCP, null, msgObj, PROTO_PROTOBUF, needToast);
//	}

}

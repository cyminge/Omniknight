package com.cy.omniknight.socket.message;

import java.io.Serializable;
import java.util.Collection;

public class MCSerFilter implements MCacheFilter {
	public static final int ERR_MSG_NULL = -1;
	public static final int ERR_MSG_EXIST = -2;

	@Override
	public int sendFilter(MCache cache, MMessage msg) {
		if (null == msg || null == msg.getMessageObject()) {
			return ERR_MSG_NULL;
		}

		Collection<MMessage> existMessages = cache.listMessage();
		Serializable msgObj = msg.getMessageObject();

		/* detect if cache has same message already */
		for (MMessage existMsg : existMessages) {
			Serializable serExistMsg = existMsg.getMessageObject();
			if (null == serExistMsg) {
//				MinaLog.e("Cache null msg exist");
				continue;
			}

			if (msgObj.equals(serExistMsg)) {
//				if (msgObj instanceof GeneratedMessage) {
//					MinaLog.i("--> [" + existMsg.getMsgId() + "] 重复消息(cache size=" + existMessages.size() + ") : "
//				+ ConstantsProtocal.getProtoObjDesc((GeneratedMessage) msgObj));
//				} else {
//					MinaLog.i("--> 重复消息 : 未知协议 " + msgObj);
//				}
				return ERR_MSG_EXIST;
			}

			/* for specific message : GroupUserActionReq, serialize the request */
//			if (msgObj instanceof GroupUserActionReq && serExistMsg instanceof GroupUserActionReq) {
//				GroupUserActionReq oldReq = (GroupUserActionReq) msgObj;
//				GroupUserActionReq newReq = (GroupUserActionReq) serExistMsg;
//				if (oldReq.getGroupId() == newReq.getGroupId()) { // 同一个群的操作
//					// 处理进退群重复操作
//					String oldAction = null;
//					if (GlobalDefine.CHAT_ACTION_CMD_ENTER == oldReq.getCmd()) {
//						oldAction = "进群";
//					} else if (GlobalDefine.CHAT_ACTION_CMD_LEAVE == oldReq.getCmd()) {
//						oldAction = "退群";
//					}
//					if (null != oldAction) {
//						String newAction = null;
//						if (GlobalDefine.CHAT_ACTION_CMD_ENTER == oldReq.getCmd()) {
//							newAction = "进群";
//						} else if (GlobalDefine.CHAT_ACTION_CMD_LEAVE == oldReq.getCmd()) {
//							newAction = "退群";
//						}
//						if (null != newAction) {
//							existMsg.setTransRetry(0); // 旧请求不再重发
//
//							MinaLog.e("{{{{{ 重复切群");
//							MinaLog.e("cached: " + ConstantsProtocal.getProtoObjDesc((GeneratedMessage) serExistMsg));
//							MinaLog.e("new: " + ConstantsProtocal.getProtoObjDesc((GeneratedMessage) msgObj));
//							MinaLog.e("}}}}}");
//						}
//					}
//				}
//			}
		}

		return 0;
	}

	@Override
	public int receiveFilter(MCache cache) {
		return 0;
	}

}

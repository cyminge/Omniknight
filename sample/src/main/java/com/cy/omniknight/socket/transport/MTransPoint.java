package com.cy.omniknight.socket.transport;

import com.cy.omniknight.socket.message.MEndPoint;
import com.cy.omniknight.socket.message.MMessage;

/**
 * 数据传输接口，实现类注册到MProxy中，负责消息传输。可在MMessageFactory中添加生成消息接口，方便使用。
 * 传输节点状态改变，必须主动通知MProxy。
 * 消息发送成功后，必须主动通知MProxy。
 * 传输节点建立后，各自保持连接。
 * 
 */
public interface MTransPoint {

	/**
	 * 传输断点是否有效
	 * 
	 * @return
	 */
	public boolean isEnable();

	/**
	 * 构造端点指定描述
	 * 
	 * @param target 端点描述
	 * @return
	 */
	public MEndPoint genEndPoint(String target);

	/**
	 * 获取该传输节点的传输类型，如短信方式"sms",网络传输方式较多，如udp，tcp的push方式，http方式等
	 * 
	 * @return
	 */
	public String getTypeName();

	/**
	 * 端点发送传输消息接口
	 * 
	 * @param message 消息内容
	 * @return 发送成功，返回0
	 */
	public int sendMessage(MMessage message);

	/**
	 * 判断端点是否符合规范
	 * 
	 * @param endPoint 端点
	 * @return 端点合法，返回true，否则返回false
	 */
	public boolean isValidEndPoint(MEndPoint endPoint);

	/**
	 * 端点初始化
	 */

	public void initital();

	/**
	 * 反初始化
	 */
	public void deinitial();

	/**
	 * 断开时尝试重连
	 */
	public void establishTrigger();
}

package com.cy.omniknight.socket.transport;

import java.io.Serializable;

public interface MSerializer {

	/**
	 * 获取序列化工具的标志名
	 * 
	 * @return
	 */
	public String getName();

//	/**
//	 * 将消息序列化成二进制格式数据
//	 * 
//	 * @param msgObj 格式化消息内容
//	 * @return
//	 */
//	public byte[] serialize2ByteArray(Object msgObj);

	/**
	 * 将消息序列化为ascii文本
	 * 
	 * @param msgObj 格式化消息
	 * @return
	 */
	public Serializable getTransmittable(Serializable msgObj);
}

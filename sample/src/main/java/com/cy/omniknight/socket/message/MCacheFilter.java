package com.cy.omniknight.socket.message;

public interface MCacheFilter {
	/**
	 * 发送filter.
	 * @param cache
	 * @return 0成功，负数表示错误码
	 */
	public int sendFilter(MCache cache, MMessage msg);
	
	
	/**
	 * 接收filter
	 * @param cache
	 * @return 0成功，负数表示错误码
	 */
	public int receiveFilter(MCache cache);
}

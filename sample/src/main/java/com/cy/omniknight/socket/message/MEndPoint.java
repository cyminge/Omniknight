package com.cy.omniknight.socket.message;

import java.io.Serializable;

/**
 * 端点
 */
public class MEndPoint implements Serializable {
	private static final long serialVersionUID = 3412456146973040608L;

	private String mStrTransType;
	private String mStrTarget;

	public String getTransType() {
		return mStrTransType;
	}

	public String getTarget() {
		return mStrTarget;
	}

	public MEndPoint(String transType, String target) {
		this.mStrTransType = transType;
		this.mStrTarget = target;
	}
}

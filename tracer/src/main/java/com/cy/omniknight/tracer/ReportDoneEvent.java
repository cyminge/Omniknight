package com.cy.omniknight.tracer;

public class ReportDoneEvent {
	public boolean isSuccess = false;
	public int code = 0;

	public ReportDoneEvent(boolean success, int code) {
		this.isSuccess = success;
		this.code = code;
	}
}

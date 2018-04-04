package com.cy.omniknight.tracer.export;

public interface IMaxLogFrame {
	/* triggered when log frame has max cached log items
	 * if getLogPath returns not null, it will save to log to local file.
	 * 
	 * Note, implementation class must NOT use Traser's methods to avoid dead circle call.
	 */
	public String getLogPath();

	public int saveLogToFile(final String jsonString);

	/**
	 * triggered after save log to local file
	 * 
	 * @param jsonString
	 * @return 0 if report to server success
	 */
	public int reportToServer(final String jsonString);

	public int reportToServer(final String reportType, final String logName, final String jsonString);
}

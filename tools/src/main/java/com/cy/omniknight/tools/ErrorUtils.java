package com.cy.omniknight.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ErrorUtils {

	public static String getExceptionInfo(Throwable ex) {
		Writer errorWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(errorWriter);
		ex.printStackTrace(pw);
		pw.close();
		return errorWriter.toString();
	}
}

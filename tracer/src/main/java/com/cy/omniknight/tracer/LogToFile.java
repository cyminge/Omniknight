package com.cy.omniknight.tracer;

import android.os.Environment;
import com.cy.omniknight.tracer.util.Utils;

import java.io.*;

public class LogToFile {
	private OutputStreamWriter osw = null;

	public LogToFile(String fileName) {
		try {
			osw = new OutputStreamWriter(new FileOutputStream(fileName, true));
		} catch (FileNotFoundException e) {
			Tracer.handleCatchedException("cyTest", e);
		}
	}

	public void log(String logString) {
		if (osw != null) {
			try {
				osw.append(logString);
				osw.append("\r\n");
				osw.flush();
			} catch (IOException e) {
				Tracer.handleCatchedException("cyTest", e);
			}
		}
	}

	public void log(String tag, String logString) {
		Tracer.i(tag, logString);
		if (osw != null) {
			try {
				osw.append(logString);
				osw.append("\r\n");
				osw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Tracer.e("cyTest", "寫入文件異常");
			}
		}
	}

	public void close() {
		if (null != osw) {
			try {
				osw.flush();
				osw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			osw = null;
		}
	}

	public static String getDefLogPath() {
		try {
			File file = Environment.getExternalStorageDirectory();
			String dir = file.getPath();
			if (Utils.isEmpty(dir)) {
				dir = "";
			}
			return (dir + "/" + Utils.formatCurrDay() + ".log");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "wkl_def.log";
	}
	
}

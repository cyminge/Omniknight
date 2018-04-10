package com.cy.omniknight.tools.reserved;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class UtilOfPreUnavailable {

	private static final String TAG = "ScreenData";
	private Context mContext;

	public UtilOfPreUnavailable(Context context) {
		mContext = context;
	}

	/**
	 * 这个方法里的获取cpu信息的方式应该具有可行性，目前还无法使用。
	 */
	public void getCPUUsageRates2() {
		Process m_process = null;
		String str2 = "";
		String[] cpuInfo = { "", "" }; // 1-cpu型号 //2-cpu频率
		String[] arrayOfString;
		InputStream in = null;
		try {
			m_process = Runtime.getRuntime().exec("/system/bin/top -n 1");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		StringBuilder sbread = new StringBuilder();
		in = m_process.getInputStream();

		byte[] re = new byte[1024];
		try {
			while (in.read(re) != -1)
				str2 = str2 + new String(re);
		} catch (IOException e) {

			e.printStackTrace();
		}
		Log.i(TAG, "Str2 = " + str2);

	}

	/**
	 * 網絡速度 ：還需測試
	 * 
	 * @return
	 */
	public static long getNetworkSpeed() {
		ProcessBuilder cmd;
		long readBytes = 0;
		BufferedReader rd = null;
		try {
			String[] args = { "/system/bin/cat", "/proc/net/dev" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			rd = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			int linecount = 0;
			while ((line = rd.readLine()) != null) {
				linecount++;
				if (line.contains("wlan0") || line.contains("eth0")) {
					// L.E("获取流量整条字符串",line);
					String[] delim = line.split(":");
					if (delim.length >= 2) {
						String[] numbers = delim[1].trim().split(" ");// 提取数据
						readBytes = Long.parseLong(numbers[0].trim());//
						break;
					}
				}
			}
			rd.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		Log.d(TAG, "readBytes = " + readBytes / (1024 * 1024));

		return readBytes;
	}

	/**
	 * 目前无法投入使用
	 * 
	 * @param cmd
	 * @param workdirectory
	 * @return
	 * @throws IOException
	 */
	public static String run2(String[] cmd, String workdirectory) throws IOException {
		String result = "";
		try {
			ProcessBuilder builder = new ProcessBuilder(cmd);
			BufferedReader bufferedReader = null;
			// 设置一个路径
			if (workdirectory != null) {
				builder.directory(new File(workdirectory));
				builder.redirectErrorStream(true);
				Process process = builder.start();
				bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 8192);
				while (bufferedReader.readLine() != null)
					result = result + bufferedReader.readLine();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * cpu利用率
	 * @return
	 */
	public float readUsage() {

		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();
			String[] toks = load.split(" ");
			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			try {
				Thread.sleep(360);
			} catch (Exception e) {
			}
			reader.seek(0);
			load = reader.readLine();
			reader.close();
			toks = load.split(" ");
			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			
			Log.d(TAG, "cpu利用率："+(int) (100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1))));
			
			return (int) (100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 0;
	}

}

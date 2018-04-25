package com.cy.omniknight.socket.protocol;


//import com.tencent.mm.model.storage.SysInfoStorage;

public class FlowControl {
	private final static String LOG_TAG = "network.FlowControl";
//	private static SysInfoStorage sysStg =  SysInfoStorage.getInstance();
	
	//flow record
	private final static int RECORD_UNIT_LEN = 1024;
	private static int writeLen = 0;
	private static int receiveLen;
	
	//flow control
	private final static int FLOW_CONTROL_SPEED = 1;
	private final static int CAPCITY = 10000;
	private static int lastTime;
	private static int left;
	
	public static boolean dealWithWrite(int len){
		writeLen += len;
		if (writeLen > RECORD_UNIT_LEN){
//			int oldLen = sysStg.getInt(SysInfoStorage.NET_WRITE_LEN);
//			Log.d(LOG_TAG, "dealWithWrite sum:" + (oldLen + RECORD_UNIT_LEN));
//			sysStg.setInt(SysInfoStorage.NET_WRITE_LEN, oldLen + RECORD_UNIT_LEN);
			writeLen -= RECORD_UNIT_LEN;
		}
		return true;
	}
	
	public static void dealWithRead(int len){
		receiveLen += len;
		if (receiveLen > RECORD_UNIT_LEN){
//			int oldLen = sysStg.getInt(SysInfoStorage.NET_RECEIVE_LEN);
//			Log.d(LOG_TAG, "dealWithRead sum:" + (oldLen + RECORD_UNIT_LEN));
//			sysStg.setInt(SysInfoStorage.NET_RECEIVE_LEN, oldLen + RECORD_UNIT_LEN);
			receiveLen -= RECORD_UNIT_LEN;
		}
	}
	
/*	public static boolean dealWithNetData(int len){
		Date data = new Date();
		long delta_T = data.getTime() - lastTime;
		left -= delta_T * FLOW_CONTROL_SPEED;
		if (left < 0)
			left = 0;
		
		if (left + len > CAPCITY)
			return false;
		
		left += len;
		return true;
	}*/
}

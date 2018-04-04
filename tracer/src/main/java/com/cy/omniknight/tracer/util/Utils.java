package com.cy.omniknight.tracer.util;

import android.annotation.SuppressLint;
import android.content.Context;

import com.cy.omniknight.tracer.DeviceInfo;

import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatCurrDay() {
        return formatCurrDate("yyyy-MM-dd");
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatCurrDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }
    
    public static String closeIO(Closeable c) {
    	if(null == c) {
    		return null;
    	}
    	
    	try {
			c.close();
		} catch (IOException e) {
			return "close exception :" + e.toString();
		} finally {
			c = null;
		}
    	
    	return null;
    }
    
    // 获取设备详情
    public static JSONObject getDeviceInformation(Context context) {
        if (context == null) {
            return null;
        }

        DeviceInfo deviceInfo = new DeviceInfo();
        JSONObject devInfoJson = new JSONObject();
        deviceInfo.parseDeviceInfo(context, devInfoJson);
        return devInfoJson;
    }
}

package com.cy.javabasics;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class myClass {

    private static final int AD_SHOW_TYPE_BANNER = (1 << 0);
	private static final int AD_SHOW_TYPE_NOTIFICATION = (1 << 1);
	private static final int AD_SHOW_TYPE_PTP = (1 << 2);
	private static final int AD_SHOW_TYPE_APP_DIALOG = (1 << 3);
	private static final int AD_SHOW_TYPE_ICON = (1 << 10);
//	private static final int AD_SHOW_TYPE_LAUNCH = (1 << 5);
//	private static final int AD_SHOW_TYPE_LOCK = (1 << 6);
//	private static final int AD_SHOW_TYPE_SYS_DIALOG = (1 << 7);
//    public static void main(String[] args) {
//
////        System.out.println(AD_SHOW_TYPE_BANNER);
////        System.out.println(AD_SHOW_TYPE_NOTIFICATION);
////        System.out.println(AD_SHOW_TYPE_PTP);
////        System.out.println(AD_SHOW_TYPE_APP_DIALOG);
////        System.out.println(AD_SHOW_TYPE_ICON);
////        System.out.println("============================");
////
////        int showType = AD_SHOW_TYPE_BANNER | AD_SHOW_TYPE_NOTIFICATION;
////        System.out.println(showType);
////
////        System.out.println(AD_SHOW_TYPE_BANNER&showType);
////        System.out.println(AD_SHOW_TYPE_APP_DIALOG&showType);
////
////        System.out.println(1|2|3|4);
//
//        String aa = "fdasfs_fdasfas";
//        System.out.println(aa.substring(0, aa.indexOf("_")));
//    }

    public static void main(String[] args) {

//        String param = "{\"url\":\"http://www.baidu.com\"}";
//
//        Gson gson = new Gson();
//        Param param1 = gson.fromJson(param, Param.class);
//        System.out.println(param1.url);

//        String startTimeStr = 2018+"-"+10+"-"+01+" "+00+":"+00+":"+00;
//        String endTimeStr = 2018+"-"+10+"-"+31+" "+23+":"+59+":"+59;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date startData = sdf.parse(startTimeStr);
//            Date endData = sdf.parse(endTimeStr);
//            long startTime = startData.getTime();
//            long endTime = endData.getTime();
//
//            System.out.println("startData:"+startData);
//            System.out.println("endData:"+endData);
//            System.out.println("startTime:"+startTime);
//            System.out.println("endTime:"+endTime);
//
//            Date currentDate = new Date();
//            System.out.println("currentDate:"+currentDate);
//            System.out.println("currentDate.time1:"+currentDate.getTime());
//            System.out.println("currentDate.time2:"+System.currentTimeMillis());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        System.out.println("0x2D92B5:"+0x2D92B5);
        System.out.println("0x100000:"+0x100000);

    }

    class Param {
        String url;
    }
}

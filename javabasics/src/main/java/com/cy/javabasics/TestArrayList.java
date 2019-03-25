package com.cy.javabasics;

import java.util.regex.Pattern;

public class TestArrayList {

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

//        String aa = "START u0 {act=android.intent.action.VIEW flg=0x10000000 cmp=com.tencent.mm/.plugin.webview.ui.tools.WebViewUI (has extras)} from uid 10008 pid 29198 on display 0\n";
//        String aa = "START u0 {dat=gngamehall://ActivityDetailView?packageName=XXX&gameId=9136&contentId=146&from=XXX flg=0x10000000 cmp=gn.com.android.gamehall/.detail.EventDetailActivity} from uid 10206 pid 21309 on display 0";
        String aa = "START u0 {act=android.intent.action.VIEW dat=gngamehall://ForumView?packageName=XXX&from=XXX flg=0x10000000 cmp=gn.com.android.gamehall/.ThirdAppEntryActivity} from uid 10206 pid 21309 on display 0";


        boolean isMatch1 = sDeepLinkPattern1.matcher(aa).find();
        boolean isMatch2 = sDeepLinkPattern2.matcher(aa).find();

        System.out.println("isMatch1:"+isMatch1);
        System.out.println("isMatch2:"+isMatch2);
    }

    private static final Pattern sDeepLinkPattern1 = Pattern.compile(".* \\{act=android.intent.action.VIEW dat=.*");
    private static final Pattern sDeepLinkPattern2 = Pattern.compile(".*dat=.+://.+");

    class Param {
        String url;
    }
}

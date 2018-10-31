package com.cy.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cy on 18-8-29.
 */

public class StatisticsKey {

    public static String getLabel(String action) {
        String label = sActionLabelMap.get(action);
        return label;
    }

    private static Map<String, String> sActionLabelMap = new HashMap(200);

    public static final String ACTION = add("action", "");
    public static final String PRE_SOURCE = add("preSource", "");
    public static final String CURR_SOURCE = add("currSource", "");
    public static final String CLIENT_PKG = add("clientPkg", "");
    public static final String ACCOUNT_NAME = add("userName", "");
    public static final String ACCOUNT_ID = add("uuid", "");
    public static final String NETWORK = add("network", "");

    public static final String CONN_SYMBOL = add("@", "");

    public static final String SOURCE = add("source", "");
    public static final String EXTERNAL_JUMP_SIGN = add("_ToPCalendar", "");

    public static final String VISIT = add("visit", "");
    public static final String BOOT = add("boot", "");
    public static final String LAUNCHER = add("launcher", "");

    public static final String PRIMARY_TAB = add("primaryTab", "");
    public static final String SECOND_TAB = add("secondTab", "");

    //start add by denny
    //1000
    public static final String S_1000_C_CANCEL_EDIT_EVENT = add("1000_c_cancel_edit_event", "舍弃提醒");
    public static final String S_1000_C_SAVE_EDIT_EVENT = add("1000_c_save_edit_event", "保存提醒");
    public static final String S_1000_EDIT_EVENT_START_TIME = add("1000_edit_event_start_time", "编辑开始时间");
    public static final String S_1000_EDIT_EVENT_NOTE = add("1000_edit_event_note", "编辑备注");
    public static final String S_1000_EDIT_EVENT_REPEAT_TYPE = add("1000_edit_event_repeat_type", "选择重复类型");
    public static final String S_1000_C_EDIT_EVENT_REMIND_TYPE = add("1000_c_edit_event_remind_type", "选择提醒类型");


    //almanac related  1004
    public static final String S_1004_LUNAR_LAST_DAY = add("1004_lunar_last_day", "上一天");
    public static final String S_1004_LUNAR_NEXT_DAY = add("1004_lunar_next_day", "下一天");
    public static final String S_1004_L_G = add("1004_l_g", "黄历吉日挑选入口");
    public static final String S_1004_L_G_S = add("1004_l_g_s", "吉日挑选名称统计");
    public static final String S_1004_L_MSG_EXPOSURE = add("1004_l_msg_exposure", "黄历信息流曝光");
    public static final String S_1004_L_MSG_CLICK = add("1004_l_msg_click", "黄历信息流点击");
    public static final String S_1004_L_TOOLS_TITILE = add("1004_l_tools_标题", "黄历工具点击");


    //1005
    public static final String S_1005_S_REMIND = add("1005_s_remind", "进入提醒界面");
    public static final String S_1005_S_LUNAR = add("1005_s_lunar", "首页进入黄历");
    public static final String S_1005_T_LUNAR = add("1005_t_lunar", "tab进入黄历");
    public static final String S_1005_TOOLS = add("1005_tools", "首页工具按名称统计");
    public static final String S_1005_S_MONTH = add("1005_s_month", "启动月视图");


    //1006
    public static final String S_1006_S_SETTING = add("1006_s_setting", "启动设置界面");
    public static final String S_1006_START_REMIND = add("1006_start_remind", "启动新建界面");
    public static final String S_1006_C_EVENT = add("1006_c_event", "首页点击查看全部日程");
    public static final String S_1006_REMIND_R = add("1006_remind_r", "提醒tab进入提醒界面");
    public static final String S_1006_REMIND_TODO = add("1006_remind_todo", "提醒tab进入待办");
    public static final String S_1006_REMIND_RE = add("1006_remind_re", "提醒tab进入记事");


    //1007
    public static final String S_1007_S_FOCUS = add("1007_s_focus", "首页进入添加关注界面");
    public static final String S_1007_ADD_FOCUS = add("1007_add_focus", "按各卡片名称统计添加关注次数");
    public static final String S_1007_DEL_FOCUS = add("1007_add_focus", "按各卡片名称统计取消关注次数");
    public static final String S_1007_HISTORY = add("1007_s_history", "首页进入历史今天列表");
    public static final String S_1007_H_DETAILS = add("1007_s_h_details", "访问历史今天详情");
    //end add by zhangdi

    //1008
    public static final String S_1008_S_E_REMIND = add("1008_s_e_remind", "事件提醒开关");
    public static final String S_1008_UPDATE = add("1008_update", "检测更新");
    public static final String S_1008_S_HONGBAO_REMIND = add("1008_s_hongbao_remind", "选择红包提醒");
    public static final String S_1008_S_WEEK = add("1008_s_week", "切换周首日，按名称");
    public static final String S_1008_S_TED_SMS_ASSISTANT = add("1008_s_ted_sms_assistant", "泰迪短信助手");
    //end add by denny

    //1000
    public static final String S_1000_M_M = add("1000_m_m", "滑动月视图");
    public static final String S_1000_M_TODAY = add("1000_m_today", "月视图点击返回今天");
    public static final String S_1000_W_M = add("1000_w_m", "滑动周视图");
    public static final String S_1000_W_TODAY = add("1000_w_today", "周视图点击返回今天");
    public static String add(String action, String label) {
        sActionLabelMap.put(action, label);
        return action;
    }
}

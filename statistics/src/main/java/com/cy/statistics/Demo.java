package com.cy.statistics;

/**
 * 统计调用例子
 *
 * @Created by chenls on 2018/7/19.
 */
public class Demo {

    public static void main(String[] args) {
        StatisticsManager.getInstance().send(StatisticsKey.ACTION);
    }
}

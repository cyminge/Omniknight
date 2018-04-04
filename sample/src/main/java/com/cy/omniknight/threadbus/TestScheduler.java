package com.cy.omniknight.threadbus;

/**
 * Created by JLB6088 on 2017/5/23.
 */

public class TestScheduler {

//    /**
//     * 测试无限制线程池
//     */
//    public void testUnlimitedScheduler() {
//        Log.e("cyTest", "startd time:"+System.currentTimeMillis());
//        int count = 10;
//        for(int i=0; i<count; i++) {
//            post1("http://game.gionee.com/api/Local_Home/newRecomendFirstPageList");
//        }
//        for(int i=0; i<count; i++) {
//            post1("http://game.gionee.com/api/Local_Home/textActivityad");
//        }
//        for(int i=0; i<count; i++) {
//            post1("http://game.gionee.com/Api/Local_Home/slideAd");
//        }
//        for(int i=0; i<count; i++) {
//            post1("http://game.gionee.com/api/Local_Home/dailyRecommend");
//        }
//    }
//
//    public void testSingleSortableSchedler() {
//        Log.e("cyTest", "startd time:"+System.currentTimeMillis());
//        int count = 10;
//        for(int i=0; i<count; i++) {
//            post2("http://game.gionee.com/api/Local_Home/newRecomendFirstPageList");
//        }
//        for(int i=0; i<count; i++) {
//            post2("http://game.gionee.com/api/Local_Home/textActivityad");
//        }
//        for(int i=0; i<count; i++) {
//            post2("http://game.gionee.com/Api/Local_Home/slideAd");
//        }
//        for(int i=0; i<count; i++) {
//            post2("http://game.gionee.com/api/Local_Home/dailyRecommend");
//        }
//    }
//
//    public void test2SortableSchedler() {
//        Log.e("cyTest", "startd time:"+System.currentTimeMillis());
//        int count = 20;
//        for(int i=0; i<count; i++) {
//            post3("http://game.gionee.com/api/Local_Home/newRecomendFirstPageList");
//        }
//        for(int i=0; i<count; i++) {
//            post3("http://game.gionee.com/api/Local_Home/textActivityad");
//        }
//        for(int i=0; i<count; i++) {
//            post3("http://game.gionee.com/Api/Local_Home/slideAd");
//        }
//        for(int i=0; i<count; i++) {
//            post3("http://game.gionee.com/api/Local_Home/dailyRecommend");
//        }
//    }
//
//    public void test4SortableSchedler() {
//        Log.e("cyTest", "startd time:"+System.currentTimeMillis());
//        int count = 10;
//        for(int i=0; i<count; i++) {
//            post4("http://game.gionee.com/api/Local_Home/newRecomendFirstPageList");
//        }
//        for(int i=0; i<count; i++) {
//            post4("http://game.gionee.com/api/Local_Home/textActivityad");
//        }
//        for(int i=0; i<count; i++) {
//            post4("http://game.gionee.com/Api/Local_Home/slideAd");
//        }
//        for(int i=0; i<count; i++) {
//            post4("http://game.gionee.com/api/Local_Home/dailyRecommend");
//        }
//
//    }
//
//    public void test8SortableScheduler() {
//        Log.e("cyTest", "startd time:"+System.currentTimeMillis());
//        int count = 10;
//        for(int i=0; i<count; i++) {
//            post5("http://game.gionee.com/api/Local_Home/newRecomendFirstPageList");
//        }
//        for(int i=0; i<count; i++) {
//            post5("http://game.gionee.com/api/Local_Home/textActivityad");
//        }
//        for(int i=0; i<count; i++) {
//            post5("http://game.gionee.com/Api/Local_Home/slideAd");
//        }
//        for(int i=0; i<count; i++) {
//            post5("http://game.gionee.com/api/Local_Home/dailyRecommend");
//        }
//    }
//
//    public void test16SortableScheduler() {
//        Log.e("cyTest", "startd time:"+System.currentTimeMillis());
//        int count = 10;
//        for(int i=0; i<count; i++) {
//            post6("http://game.gionee.com/api/Local_Home/newRecomendFirstPageList");
//        }
//        for(int i=0; i<count; i++) {
//            post6("http://game.gionee.com/api/Local_Home/textActivityad");
//        }
//        for(int i=0; i<count; i++) {
//            post6("http://game.gionee.com/Api/Local_Home/slideAd");
//        }
//        for(int i=0; i<count; i++) {
//            post6("http://game.gionee.com/api/Local_Home/dailyRecommend");
//        }
//    }
//
//    private void post1(final String url) {
//        TaskRunnable taskRunnable = new TaskRunnable() {
//            @Override
//            public void runTask() {
//                String result= JsonUtils.postData(url);
//                Log.d("cyTest", "finish time:"+System.currentTimeMillis()+"--> result="+(result == "gamehall_fail"));
//            }
//        };
//        SchedulerFactory.getUnlimitedScheduler().schedule(taskRunnable);
//    }
//
//    private void post2(final String url) {
//        TaskRunnable taskRunnable = new TaskRunnable() {
//            @Override
//            public void runTask() {
//                String result= JsonUtils.postData(url);
//                Log.d("cyTest", "finish time:"+System.currentTimeMillis()+"--> result="+(result == "gamehall_fail"));
//            }
//        };
//        SchedulerFactory.getSingleSortableScheduler().schedule(taskRunnable);
//    }
//
//    private void post3(final String url) {
//        TaskRunnable taskRunnable = new TaskRunnable() {
//            @Override
//            public void runTask() {
//                String result= JsonUtils.postData(url);
//                Log.d("cyTest", "finish time:"+System.currentTimeMillis()+"--> result="+(result == "gamehall_fail"));
//            }
//        };
//        sortableScheduler2.schedule(taskRunnable);
//    }
//
//    private void post4(final String url) {
//        TaskRunnable taskRunnable = new TaskRunnable() {
//            @Override
//            public void runTask() {
//                String result= JsonUtils.postData(url);
//                Log.d("cyTest", "finish time:"+System.currentTimeMillis()+"--> result="+(result == "gamehall_fail"));
//            }
//        };
//        sortableScheduler4.schedule(taskRunnable);
//    }
//
//    private void post5(final String url) {
//        TaskRunnable taskRunnable = new TaskRunnable() {
//            @Override
//            public void runTask() {
//                String result= JsonUtils.postData(url);
//                Log.d("cyTest", "finish time:"+System.currentTimeMillis()+"--> result="+(result == "gamehall_fail"));
//            }
//        };
//        sortableScheduler8.schedule(taskRunnable);
//    }
//
//    private void post6(final String url) {
//        TaskRunnable taskRunnable = new TaskRunnable() {
//            @Override
//            public void runTask() {
//                String result= JsonUtils.postData(url);
//                Log.d("cyTest", "finish time:"+System.currentTimeMillis()+"--> result="+(result == "gamehall_fail"));
//            }
//        };
//        sortableScheduler16.schedule(taskRunnable);
//    }
//
//    SortableScheduler sortableScheduler16= new SortableScheduler(16);
//    SortableScheduler sortableScheduler8 = new SortableScheduler(8);
//    SortableScheduler sortableScheduler4 = new SortableScheduler(4);
//    SortableScheduler sortableScheduler2 = new SortableScheduler(2);

}

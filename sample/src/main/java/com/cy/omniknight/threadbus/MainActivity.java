package com.cy.omniknight.threadbus;

import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cy.threadbus.ScheduleListener;
import com.cy.threadbus.ScheduledTask;
import com.cy.threadbus.SchedulerConfiguration;
import com.cy.threadbus.SchedulerFactory;
import com.cy.threadbus.TaskCallable;
import com.cy.threadbus.TaskRunnable;
import com.cy.threadbus.core.IScheduler;
import com.cy.threadbus.core.PriorityThreadFactory;
import com.cy.threadbus.utils.Constant;
import com.cy.threadbus.utils.DeviceUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.*;

import static com.cy.threadbus.SchedulerFactory.getSingleDelayedScheduler;
import static com.cy.threadbus.ThreadBus.newAssembler;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ThreadBusSample";

    /**
     * TaskRunnable是一个任务的基础类，有三个参数：String threadName, int threadPriority, int sortPriority
     * threadName是设置执行当前任务的线程名，便于出问题的时候可跟踪。
     * threadPriority是设置执行当前任务的线程优先级，优先级的选择参考android.os.Process.java
     * sortPriority是设置当前的插队优先级，当线程池是自定义线程池或者可插队线程池时，该属性有效。
     */
    class TestTaskRunnable extends TaskRunnable {

        public TestTaskRunnable(String threadName, int threadPriority, int sortPriority) {
            super(threadName, threadPriority, sortPriority);
        }

        @Override
        public void runTask() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "===>> 无返回值任务测试！-- ThreadName:" + getThreadName() + ", threadPriority:"
                    + getThreadPriority() + ",  sortPriority:" + getTaskSortPriority());
        }
    }

    /**
     * TaskCallable是一个有返回值任务的基础类，有三个参数：String threadName, int threadPriority, int sortPriority
     * threadName是设置执行当前任务的线程名，便于出问题的时候可跟踪。
     * threadPriority是设置执行当前任务的线程优先级，优先级的选择参考android.os.Process.java
     * sortPriority是设置当前的插队优先级，当线程池是自定义线程池或者可插队线程池时，该属性有效。
     */
    class TestTaskCallable extends TaskCallable<String> {

        public TestTaskCallable(String threadName, int threadPriority, int sortPriority) {
            super(threadName, threadPriority, sortPriority);
        }

        @Override
        protected String callTask() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "===>> 有返回值任务测试！-- ThreadName:" + getThreadName() + ", threadPriority:"
                    + getThreadPriority() + ",  sortPriority:" + getTaskSortPriority();
        }
    }

    /**
     * 任务执行完毕的回调，在这里处理结果
     */
    static class MySchedulerListener implements ScheduleListener {

        private WeakReference<MainActivity> weakReference;

        public MySchedulerListener(MainActivity mainActivity) {
            weakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void onScheduleDone(ScheduledTask scheduledTask) {
            if(weakReference == null || weakReference.get() == null) {
                return;
            }

            try {
                Log.d(TAG, "onScheduleDone-result:"+scheduledTask.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 测试TaskRunnable调用流程
     * @param view
     */
    public void onClickOne(View view) {
        TestTaskRunnable runnable = new TestTaskRunnable("测试TaskRunnable调用流程", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
        newAssembler().create(runnable).scheduleOn(SchedulerFactory.getUnlimitedScheduler()).start();
    }

    /**
     * 测试TaskCallable调用流程
     * 两种方式防止ScheduleListener内存泄露
     * 1. 用静态类的方式：MySchedulerListener schedulerListener = new MySchedulerListener(this);
     * 2. 取消该任务：scheduledTask.cancel(true);
     *
     * {@link ScheduledTask#get()}是一个阻塞方法，如果是在主线程调用代码执行，建议通过{@link ScheduleListener}回调方式异步获取数据。
     * @param view
     */
    public void onClickTwo(View view) {
        TestTaskCallable callable = new TestTaskCallable("测试TaskCallable调用流程", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
        MySchedulerListener schedulerListener = new MySchedulerListener(this);
        ScheduledTask scheduledTask = newAssembler().create(callable, schedulerListener)
                .scheduleOn(getSingleDelayedScheduler())
                .startForResult();
        Log.d(TAG, "开始执行");
        // 下面的代码中get()是一个阻塞方法，如果是在主线程调用的话，不要用该方式获取结果，请用ScheduleListener回调的方式；
//        try {
//            Log.d(TAG, "blocking-result:"+scheduledTask.get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        // end
    }

    /**
     * 测试可插队线程池
     * @param view
     */
    public void onClickThree(View view) {
        TestTaskRunnable runnable11 = new TestTaskRunnable("测试可插队线程池 11", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
        TestTaskCallable callable22 = new TestTaskCallable("测试可插队线程池 22", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
        TestTaskRunnable runnable33 = new TestTaskRunnable("测试可插队线程池 33", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_LOW);
        TestTaskCallable callable44 = new TestTaskCallable("测试可插队线程池 44", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_HIGH);
        TestTaskRunnable runnable55 = new TestTaskRunnable("测试可插队线程池 55", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_HIGH);
        TestTaskCallable callable66 = new TestTaskCallable("测试可插队线程池 66", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_NORMAL);

        MySchedulerListener schedulerListener = new MySchedulerListener(this);
        newAssembler().create(callable22, schedulerListener).scheduleOn(SchedulerFactory.getSingleSortableScheduler()).startForResult();
        newAssembler().create(runnable11).scheduleOn(SchedulerFactory.getSingleSortableScheduler()).start();
        newAssembler().create(callable44, schedulerListener).scheduleOn(SchedulerFactory.getSingleSortableScheduler()).startForResult();
        newAssembler().create(runnable33).scheduleOn(SchedulerFactory.getSingleSortableScheduler()).start();
        newAssembler().create(callable66, schedulerListener).scheduleOn(SchedulerFactory.getSingleSortableScheduler()).startForResult();
        newAssembler().create(runnable55).scheduleOn(SchedulerFactory.getSingleSortableScheduler()).start();

        Log.d(TAG, "开始执行");
    }

    /**
     * 测试自定义线程池
     * @param view
     */
    public void onClickFour(View view) {
        ThreadFactory threadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);

        SchedulerConfiguration.Builder builder = new SchedulerConfiguration.Builder();
        builder.confPoolSize(DeviceUtil.getDeviceCPUCounts())
                .confSortRule(Constant.SORT_RULE_BY_USER_DEFINED)
                .confThreadFactory(threadFactory)
                .confThreadPoolType(Constant.THREAD_POOL_TYPE_IO);

        SchedulerConfiguration configuration = builder.builder();
        IScheduler iScheduler = SchedulerFactory.create(configuration);

        TestTaskCallable callable = new TestTaskCallable("测试自定义Scheduler", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
        MySchedulerListener schedulerListener = new MySchedulerListener(this);
        ScheduledTask<String> scheduledTask = iScheduler.schedule(callable, schedulerListener);

        Log.d(TAG, "开始执行");
    }

    /**
     * 测试延时执行任务
     * @param view
     */
    public void onClickFive(View view) {
        TestTaskRunnable runnable = new TestTaskRunnable("测试延时执行任务 11", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
        newAssembler().create(runnable)
                .scheduleOn(getSingleDelayedScheduler())
                .delay(5L, TimeUnit.SECONDS)
                .start();

        TestTaskCallable callable = new TestTaskCallable("测试延时执行任务 22", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
        MySchedulerListener schedulerListener = new MySchedulerListener(this);
        ScheduledTask scheduledTask = newAssembler().create(callable, schedulerListener)
                .scheduleOn(getSingleDelayedScheduler())
                .delay(2L, TimeUnit.SECONDS)
                .startForResult();
        Log.d(TAG, "开始执行");
    }

    /**
     * 测试周期执行任务
     * 注意：带返回值的任务不支持周期执行
     * @param view
     */
    public void onClickSix(View view) {
        TestTaskRunnable runnable11 = new TestTaskRunnable("测试周期执行任务 11", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
        TestTaskRunnable runnable22 = new TestTaskRunnable("测试周期执行任务 22", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
        newAssembler().create(runnable11)
                .scheduleOn(getSingleDelayedScheduler())
                .delay(1, TimeUnit.SECONDS)
                .period(1)
                .start();
        newAssembler().create(runnable22)
                .scheduleOn(getSingleDelayedScheduler())
                .delay(2, TimeUnit.SECONDS)
                .period(2)
                .start();

        Log.d(TAG, "开始执行");

//        ThreadFactory DEFAULT_WORKER_THREAD_FACTORY = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND + 2);
//        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, DEFAULT_WORKER_THREAD_FACTORY);
//        scheduledThreadPoolExecutor.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
    }


    /**
     * 测试多任务同时返回
     * @param view
     */
    public void onClickSeven(View view) {
        ArrayList<Callable<String>> resultTaskList = new ArrayList<>();
        TaskCallable<String> resultTask11 = new TaskCallable<String>("多任务测试--11 !!") {
            @Override
            protected String callTask() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "多任务测试--11 !!";
            }
        };

        TaskCallable<String> resultTask22 = new TaskCallable<String>("多任务测试--22 !!") {
            @Override
            protected String callTask() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "多任务测试--22 !!";
            }
        };

        TaskCallable<String> resultTask33 = new TaskCallable<String>("多任务测试--33 !!") {
            @Override
            protected String callTask() {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "多任务测试--33 !!";
            }
        };

        TaskCallable<String> resultTask44 = new TaskCallable<String>("多任务测试--44 !!") {
            @Override
            protected String callTask() {
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "多任务测试--44 !!";
            }
        };

        resultTaskList.add(resultTask11);
        resultTaskList.add(resultTask22);
        resultTaskList.add(resultTask33);
        resultTaskList.add(resultTask44);

        ArrayList<ScheduledTask<String>> scheduledList = newAssembler().groupCreate(resultTaskList).scheduleOn(SchedulerFactory.getBatchScheduler()).startForMultiResult();

        int waitTime = 5000;
        long deltaTime = 0;
        long startTime;

        for (Future<String> ss : scheduledList) {
            try {
                startTime = System.currentTimeMillis();
                waitTime -= deltaTime;
                waitTime = Math.max(waitTime, 0);

                Log.d(TAG, System.currentTimeMillis() / 1000 +  "------------------> " + " : 结果：ss.get()-->" + ss.get(waitTime, TimeUnit.MILLISECONDS));
                deltaTime = System.currentTimeMillis() - startTime;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (CancellationException e) {
                e.printStackTrace();
            }
        }

        // 用完调用shutdown()，快速回收资源
        SchedulerFactory.getBatchScheduler().shutdown();
    }

    public void onClickEight(View view) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

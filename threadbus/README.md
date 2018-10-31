this code repository is to encapsulate Java thread pool, Let the thread pool calls become simple and easy to use, And Java thread pool for unified management.

接入说明：
1. 线程池的初始化
    ThreadBus.init(this); // 线程池入口函数

2. 创建生成一个任务
    TestTaskRunnable runnable = new TestTaskRunnable("测试TaskRunnable调用流程", Process.THREAD_PRIORITY_FOREGROUND, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
    ThreadBus.newAssembler().create(runnable).scheduleOn(SchedulerFactory.getUnlimitedScheduler()).start();

3.  SchedulerFactory提供了几种默认的线程池，同时开放了自定义线程池的入口。

注意事项：
1. 每个任务都是TaskRunnable或者TaskCallable的实现类
2. 调用方式统一 ThreadBus.newAssembler().create({**}).scheduleOn({**).start();// startForResult\startForMutilResult











 * 待处理问题：
 * 1. 单任务分解合并的 -----------------  ForkJoin框架不适合放到这个里面，需要用另外一套封装逻辑 -----------------------------------------------------------
 * 2. 核心线程数是否要支持回收
 * 3. 验证线程池存活时间
 * 4. 需不需要提供只用于计算或者网络访问的线程池
 * 5. 线程池大小调整触发：网络、cpu、内存、前台、后台、屏灭屏亮、电量
 * 6. 先在购物大厅使用

 * 7. 验证动态改变线程大小功能是否正常
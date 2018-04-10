package com.cy.omniknight.verify.threadpool;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
 
/**
 * 动态获取线程池
 *
 * author: mingyuan.wang
 * date: 2015/08/18
 * version 1.0
 */
public class DynamicExecutorFactory {
//    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicExecutorFactory.class);
//    private static final int DEFAULT_THRESHOLD = 4;
//    private static AtomicDouble atomicDouble = new AtomicDouble(0L); // 小于1
// 
//    static {
//        backgroundUpdate();
//    }
// 
//    /**
//     *
//     * @param useLoad 是否根据负载动态生成线程池
//     * @param expiredNum 期望的线程池大小
//     * @return
//     */
//    public static ExecutorService getExecutorService(boolean useLoad,int expiredNum){
//        int threads;
//        if (useLoad) {
//            threads = getThreadNum(expiredNum);
//        } else {
//            threads = expiredNum <= DEFAULT_THRESHOLD ? expiredNum : DEFAULT_THRESHOLD;
//        }
//        LOGGER.info("[DynamicExecutorFactory]创建线程数threads={}，expiredNum={}",threads,expiredNum);
//        ExecutorService executorService = Executors.newFixedThreadPool(threads,new NamedThreadFactory("risk-parallel",false));
//        return executorService;
//    }
// 
//    private static int getThreadNum(int expiredNum){
//        if( expiredNum <= 2) return expiredNum;
// 
//        int threadNum =  expiredNum * (int)atomicDouble.get();
//        return threadNum < 1 ? 1:threadNum;
//    }
// 
//    /**
//     * 原则：削峰填谷
//     * 根据4个方面的系统信息动态决定线程池大小：
//     * 1、CPU负载 ，包括系统全局的cpu负载以及jvm实例的cpu负载
//     * 2、内存使用情况
//     * 3、系统线程使用情况，当前活动线程数与线程峰值
//     * 4、swap情况,系统颠簸会导致页面置换频繁
//     */
//    private static void backgroundUpdate(){
// 
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("dynamic-load-schedule",true));
//        final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
//        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
//        final ThreadMXBean thread = ManagementFactory.getThreadMXBean();
// 
//        executorService.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//                double systemCpuLoad = 0;
//                double processCpuLoad = 0;
//                double usage = 0;
//                double threadRadio = 0;
//                double swapSpaceRadio = 0;
//                try {
//                    //1、获取CPU负载 ，from 0-1 ，系统颠簸前（后），processCpuLoad升(降)，1-systemCpuLoad降(升)，此涨(消)彼消(涨)；
//                    systemCpuLoad = operatingSystemMXBean.getSystemCpuLoad();
//                    processCpuLoad = operatingSystemMXBean.getProcessCpuLoad();
// 
//                    //2、获取内存使用情况 ，from 0-1，系统颠簸时，内存不足，1-usage降低
//                    MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
//                    usage = (double)memoryUsage.getUsed() / (double)memoryUsage.getCommitted();
// 
//                    //3、获取系统线程数,系统颠簸时，threadCount与peakThreadCount接近，threadRadio降低
//                    double threadCount = Double.valueOf(thread.getThreadCount() + "");
//                    double peakThreadCount = Double.valueOf(thread.getPeakThreadCount() + "");
//                    threadRadio = 0L;
//                    if(threadCount < peakThreadCount){
//                        threadRadio = 1 - threadCount / peakThreadCount;
//                    }else {
//                        threadRadio = 1- peakThreadCount / threadCount;
//                    }
// 
//                    //4、获取swap情况,系统颠簸会导致页面置换频繁,swapSpaceRadio减小
//                    long freeSwapSpaceSize = operatingSystemMXBean.getFreeSwapSpaceSize();
//                    long totalSwapSpaceSize = operatingSystemMXBean.getTotalSwapSpaceSize();
//                    swapSpaceRadio = (double)freeSwapSpaceSize / (double)totalSwapSpaceSize;
// 
//                    atomicDouble.set((1-systemCpuLoad)*0.2 + (1-processCpuLoad)*0.2
//                            + (1-usage)*0.1 + threadRadio*0.3 + swapSpaceRadio*0.2);
// 
//                } catch (Exception e) {
//                    LOGGER.error("[DynamicExecutorFactory] backgroundUpdate error =",e);
//                } finally {
//                    StringBuilder stringBuilder = new StringBuilder();
//                    stringBuilder.append("(1-systemCpuLoad)*0.2=").append(1-systemCpuLoad)
//                            .append("; (1-processCpuLoad)*0.2=").append(1-processCpuLoad)
//                            .append("; (1-usage)*0.1=").append(1-usage).append("; threadRadio*0.3=").append(threadRadio)
//                            .append("; swapSpaceRadio*0.2=").append(swapSpaceRadio).append("; load=").append(atomicDouble.get());
//                    LOGGER.info("[DynamicExecutorFactory]"+stringBuilder.toString());
//                }
//            }
//        },10,3, TimeUnit.SECONDS);
//    }
}

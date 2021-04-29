package com.majm.concurrent.designpattern.workerthread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池中线程执行的任务 如果有依赖关系,可能导致死锁
 *
 * @author majunmin
 * @description
 * @datetime 2021/3/6 4:50 下午
 * @since
 */
public class ThreadPoolDeadLock {

    public static void main(String[] args) throws InterruptedException {
        //L1、L2 阶段共用的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        //L1 阶段的闭锁
        CountDownLatch l1 = new CountDownLatch(2);
        for (int i = 0; i < 2; i++) {
            System.out.println("L1");
            // 执行 L1 阶段任务
            threadPool.execute(() -> {
                //L2 阶段的闭锁
                CountDownLatch l2 = new CountDownLatch(2);
                // 执行 L2 阶段子任务
                for (int j = 0; j < 2; j++) {
                    threadPool.execute(() -> {
                        System.out.println("L2");
                        l2.countDown();
                    });
                }
                // 等待 L2 阶段任务执行完
                try {
                    l2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                l1.countDown();
            });
        }

        // 等着 L1 阶段任务执行完
        l1.await();
        System.out.println("end");
    }
}

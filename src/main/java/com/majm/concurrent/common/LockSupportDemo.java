package com.majm.concurrent.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/17 4:36 下午
 * @since
 */
public class LockSupportDemo {


    /**
     * LockSupport.park() :   让当前线程 从 RUNNING 状态 转为  WAIT, 释放CPU执行权限
     * LockSupport.unpark(Thread t): 让 t 有 wait 状态 转为 Ready状态
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            System.out.println("hello1");
            LockSupport.park();
            System.out.println("hello2");
        });

        Thread t2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LockSupport.unpark(t1);

            System.out.println("unpark thread t1...");
        });

        t2.start();
        t1.start();


        t1.join();
        t2.join();


        testParkAndUnpark();

    }

    /**
     * 原理
     * 1. park  unpark 无需事先获取锁, 跟锁状态压根没关系
     * 2. 没什么等待队列一说,unpark 会精准唤醒某一个线程
     * 3. park unpark 没有顺序性要求, 可以先调用 unpark()
     * <p>
     * 线程有一个计数器,初始值为0,
     * park():
     * Lock.park() :  如果这个值为0,就将这个线程挂起,状态修改为 WAITING,如果这个值为 1,就将这个值改为 0,其余什么也不做
     * Lock.unpark(thread): 将某个线程的值 修改为 1
     */
    private static void testParkAndUnpark() {

        // 例子1
        LockSupport.unpark(Thread.currentThread()); // 1
        LockSupport.park(); // 0
        System.out.println("可以运行到这");
        // 例子2
        LockSupport.unpark(Thread.currentThread()); // 1
        LockSupport.unpark(Thread.currentThread()); // 1
        LockSupport.park(); // 0
        System.out.println("可以运行到这");
        // 例子3
        LockSupport.unpark(Thread.currentThread()); // 1
        LockSupport.unpark(Thread.currentThread()); // 1
        LockSupport.park(); // 0
        LockSupport.park(); // WAITING
        System.out.println("不可以运行到这");
    }
}

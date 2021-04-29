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
     * LockSupport.park(); 让当前线程 从 RUNNING 状态 转为  WAIT, 释放CPU执行权限
     * LockSupport.unpark(Thread t) : 让 t 有wait状态 转为 Ready状态
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


    }
}

package com.majm.concurrent.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/14 11:18 上午
 * @since
 */
public class UnsafeTestDemo {

    public static void main(String[] args) throws NoSuchFieldException {

        Thread mainThread = Thread.currentThread();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LockSupport.unpark(mainThread);
        }).start();

        // 挂起当前线程  使当前线程进入  WAITING 状态
        LockSupport.park();
        System.out.println("jello");

    }
}

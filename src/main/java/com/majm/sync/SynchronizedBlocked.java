package com.majm.sync;

import java.util.concurrent.TimeUnit;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-30 22:06
 * @since
 */
public class SynchronizedBlocked implements Runnable {

    public SynchronizedBlocked() {
        //该线程已持有当前实例锁
        new Thread(this::f).start();
    }

    private synchronized void f() {
        System.out.println("call f()");
        while (true) {
            Thread.yield();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (Thread.interrupted()) {
                System.out.println("中断线程!!");
                break;
            } else {
                f();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedBlocked sync = new SynchronizedBlocked();
        Thread t = new Thread(sync);
        //启动后调用f()方法,无法获取当前实例锁处于等待状态
        t.start();
        TimeUnit.SECONDS.sleep(1);
        //中断线程,无法生效
        t.interrupt();
    }
}

package com.majm.concurrent.common;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * https://www.cnblogs.com/hy928302776/p/3255641.html
 * <p>
 * Object.wait 将当前线程加入到 waitset, 并释放cpu执行权限
 *
 * @author majunmin
 * @description
 * @datetime 2021/3/17 6:32 下午
 * @since
 */
public class ObjectWait {

    private static final Object lock = new Object();

    public static void main(String[] args) {
//        demo1();
        demo2();
    }

    private static void demo2() {
        Instant now = Instant.now();

        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println(Thread.currentThread().getName());
                    lock.wait();
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println(Thread.currentThread().getName() + ": " + Duration.between(now, Instant.now()).toMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println(Thread.currentThread().getName());
                    lock.wait();
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println(Thread.currentThread().getName() + ": " + Duration.between(now, Instant.now()).toMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t3 = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println(Thread.currentThread().getName());
                    lock.wait();
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println(Thread.currentThread().getName() + ": " + Duration.between(now, Instant.now()).toMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();


        /**
         * obj.notifyAll()则能全部唤醒t1,t2,t3，但是要继续执行obj.wait()的下一条语句，必须获得obj锁，
         * 因此，t1,t2,t3 只有一个有机会获得锁继续执行，例如t1，其余的需要等待 t1 释放obj锁之后才能继续执行
         */
        new Thread(() -> {
            synchronized (lock) {

                try {
                    TimeUnit.SECONDS.sleep(3);
                    lock.notifyAll();
                    // 执行 Object#notify() 后不会释放锁,而是等待同步代码块执行完毕后释放锁
                    System.out.println(Thread.currentThread().getName() + ": " + Duration.between(now, Instant.now()).toMillis());
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private static void demo1() {
        new Thread(() -> {
            System.out.println("线程 1 尝试获取锁");
            synchronized (lock) {
                System.out.println("线程1 获取了锁资源...");
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("线程1 wait()  ");
                    lock.wait();
                    System.out.println(Thread.currentThread().getState());
                    System.out.println("线程1执行完成...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            System.out.println("线程 2 尝试获取锁");
            synchronized (lock) {
                System.out.println("线程2 获取了锁资源...");
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("线程2 notify()  ");
                    // 唤醒 等待线程  并不释放锁资源
                    lock.notify();
                    System.out.println("线程2执行完成...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

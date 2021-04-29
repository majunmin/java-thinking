package com.majm.concurrent.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author majunmin
 * @description
 * @datetime 2021/2/22 9:42 下午
 * @since
 */
public class ReentrantDemo {

    public static void main(String[] args) throws InterruptedException {

        ReentrantLock reentrantLock = new ReentrantLock(); // 可重入锁,  AQS 独占模式
        // Condition 是与 AQS 独占锁(排它锁)相关的
        Condition condition = reentrantLock.newCondition();

        // java.lang.IllegalMonitorStateException
        // reentrantLock.unlock();

        Thread t1 = new Thread(() -> {

            reentrantLock.lock();
            try {
                System.out.println("t1 get reentrantLock and wait");
                TimeUnit.SECONDS.sleep(2);

                condition.await(); // 阻塞当前线程, 让出锁 等待被唤醒  == 类似 Thread.yield()
                System.out.println("t1 continue ...");

                TimeUnit.SECONDS.sleep(2);
                System.out.println("t1 return");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        });

        Thread t2 = new Thread(() -> {
            reentrantLock.lock();
            try {
                System.out.println("t2 get reentrantLock and signal");
                TimeUnit.SECONDS.sleep(2);

                condition.signal(); // 不阻塞
                System.out.println("t2 return");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        });

        t1.start();

        t2.start();

        t1.join();
        t2.join();

    }
}

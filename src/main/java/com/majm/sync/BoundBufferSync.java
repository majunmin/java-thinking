package com.majm.sync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-30 22:40
 * @since
 */
public class BoundBufferSync {

    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private final static int CAPACITY = 50;
    public Object[] items = new Object[CAPACITY];

    private int count;
    private int putPtr;
    private int takePtr;

    public void put(Object x) throws InterruptedException {
//        synchronized (lock) {
            synchronized (notFull) {
                // 队列满了 就等待,释放锁等待被唤醒
                while (count == CAPACITY) {
                    notFull.wait();
                }
            }

            items[putPtr] = x;
            if (++putPtr == CAPACITY) {
                putPtr = 0;
            }
            count++;

            synchronized (notEmpty) {
                notEmpty.notify();
            }
//        }
    }

    /**
     *
     * @return
     * @throws InterruptedException
     */
    public Object take() throws InterruptedException {
//        synchronized (lock) {
            synchronized (notEmpty) {
                while (count == 0) {
                    notEmpty.wait();
                }
            }

            Object res = items[takePtr];
            if (++takePtr == CAPACITY) {
                takePtr = 0;
            }
            count--;
            synchronized (notFull) {
                notFull.notify();
            }
            return res;
//        }
    }

    public static void main(String[] args) throws InterruptedException {
        BoundBufferSync boundBuffer = new BoundBufferSync();

        new Thread(() -> {

            try {
                for (int i = 0; i < 100; i++) {
                    System.out.println("put" + i);
                    boundBuffer.put(i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    System.out.println(boundBuffer.take());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        TimeUnit.SECONDS.sleep(4);
    }
}

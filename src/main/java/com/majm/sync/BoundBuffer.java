package com.majm.sync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 简易的一个 阻塞队列实现 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-30 22:17
 * @since
 */
public class BoundBuffer {

    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    private final static int CAPACITY = 50;
    public Object[] items = new Object[CAPACITY];

    private int count;
    // 保证队列是先进先出的
    private int putPtr;
    private int takePtr;

    public void put(Object x) throws InterruptedException {
        lock.lock();
        try {
            // 队列满了 就等待,释放锁等待被唤醒
            while (count == CAPACITY) {
                notFull.await();
            }

            items[putPtr] = x;
            if (++putPtr == CAPACITY) {
                putPtr = 0;
            }
            count++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }

    }

    public Object take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            Object res = items[takePtr];
            if (++takePtr == CAPACITY) {
                takePtr = 0;
            }
            count--;
            notFull.signal();
            return res;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BoundBuffer boundBuffer = new BoundBuffer();


        new Thread(() -> {

            try {
                for (int i = 0; i < 100; i++) {
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

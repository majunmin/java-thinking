package com.majm.concurrent;


import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class ReentrantDemoTest {


    @Test
    public void reentrantDemo() {
        //初始化一个 ReentrantLock  fair | nonfair
        ReentrantLock lock = new ReentrantLock(true);

        lock.lock();
        try {
            try {
                // 3.支持多种加锁方式，比较灵活; 具有可重入特性
                if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 4.手动释放锁
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }
}
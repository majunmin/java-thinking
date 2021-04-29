package com.majm.concurrent;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author majunmin
 * @description
 * @datetime 2021/2/24 11:39 下午
 * @since
 */
public class ConditionTest {

    @Test
    public void testConditionDemo1() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock(); // 可重入锁,  AQS 独占模式
        Condition condition = lock.newCondition(); // Condition 是与 AQS 独占锁(排它锁相关的)

        Thread t1 = new Thread(() ->{

            lock.lock();
            try {
                System.out.println("t1 get lock and wait");
                TimeUnit.SECONDS.sleep(2);

                condition.await(); // 阻塞当前线程, 让出锁 等待被唤醒  == 类似 Thread.yield()
                System.out.println("t1 continue ...");

                TimeUnit.SECONDS.sleep(2);
                System.out.println("t1 return");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        Thread t2 = new Thread(() ->{
            lock.lock();
            try {
                System.out.println("t2 get lock and signal");
                TimeUnit.SECONDS.sleep(2);

                condition.signal(); // 不阻塞
                System.out.println("t2 return");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }


}

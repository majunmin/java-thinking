package com.majm.concurrent.designpattern.guardedsuspension;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * Guarded Suspension 模式
 * 线程间通信
 *
 * Condition
 * @author majunmin
 * @description
 * @datetime 2021/3/6 2:22 下午
 * @since
 */
public class GuardedObject<T> {

    // 受保护对象
    T obj;

    final Lock lock = new ReentrantLock();

    final Condition done = lock.newCondition();

    final int timeout = 1;

    // 获取受保护对象
    T get(Predicate<T> p) {
        lock.lock();
        try {
            // MESA 管程推荐写法
            while (!p.test(obj)) {
                done.await(timeout, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        // 返回非空受保护对象
        return obj;
    }

    // 事件通知方法
    void onChanged(T obj) {
        lock.lock();
        try {
            this.obj = obj;
            done.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

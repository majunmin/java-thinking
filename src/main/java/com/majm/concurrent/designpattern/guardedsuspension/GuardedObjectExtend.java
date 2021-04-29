package com.majm.concurrent.designpattern.guardedsuspension;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/6 2:30 下午
 * @since
 */
public class GuardedObjectExtend<T> {


    private T obj;
    private final Lock lock = new ReentrantLock();

    private final Condition done = lock.newCondition();

    private final int timeout = 2;

    private final static Map<Object, GuardedObjectExtend> gos = new ConcurrentHashMap<>();

    // 静态方法创建 GuardedObject
    public static <K> GuardedObjectExtend create(K key) {
        GuardedObjectExtend go = new GuardedObjectExtend();
        gos.put(key, go);
        return go;
    }

    public static <K, T> void fireEvent(K key, T obj) {
        GuardedObjectExtend go = gos.remove(key);
        if (Objects.nonNull(go)) {
            go.onChanged(go);
        }
    }


    // 获取受保护对象
    T get(Predicate<T> p) {
        lock.lock();
        try {
            // MESA 管程推荐写法  自旋锁
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

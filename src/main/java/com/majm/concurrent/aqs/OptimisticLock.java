package com.majm.concurrent.aqs;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger
 * AtomicLong
 * <p>
 * 利用了 unSafe  CAS操作
 *
 * @author majunmin
 * @description
 * @datetime 2021/2/22 4:23 下午
 * @since
 */
public class OptimisticLock {

    private static volatile int a = 0;
    private AtomicInteger a2 = new AtomicInteger();

    public static void main(String[] args) {
        OptimisticLock lockDemo = new OptimisticLock();
        lockDemo.phase1();
    }

    /**
     * 这个 是有问题的
     * a++ 不是一个原子操作  (JVM内存模型)
     * - 从主存读取 a 的值 到线程的内存
     * - 执行 +1 操作
     * - 把a的值写回主存
     */
    public void phase1() {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    a++;
                    System.out.println(a);
                }
            }).start();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(a);
    }


    /**
     * AtomicInteger 原子类 提供了  获取并加一的  的原子操作方法
     * <p>
     * AtomicInteger 提供的原子操作比 加锁方式效率高些,其内部使用乐观锁 CAS自旋实现
     */
    public void phase2() {
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {

                    System.out.println(a2.incrementAndGet());
                }
            }).start();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(a);
    }
}


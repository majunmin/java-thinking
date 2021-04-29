package com.majm.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/14 11:43 上午
 * @since
 */
public class PhantomReferenceDemo {

    public static final int CAPACITY = 5 * 1024 * 1024;

    public static void main(String[] args) throws InterruptedException {
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();

        // 虚引用必须要和引用队列一起使用，他的get方法永远返回null
        Reference<byte[]> reference = new PhantomReference<>(new byte[CAPACITY], referenceQueue);

        System.out.println(referenceQueue.poll());
        System.out.println(referenceQueue.poll());
        System.out.println(referenceQueue.poll());

//        System.gc();

        TimeUnit.SECONDS.sleep(3);

//        System.out.println(referenceQueue.poll());

        byte[] bytes = new byte[1024 * 1024 * 6];
    }
}

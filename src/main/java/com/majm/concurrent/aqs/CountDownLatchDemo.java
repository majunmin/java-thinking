package com.majm.concurrent.aqs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/14 1:34 下午
 * @since
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
//        demo1();
        testDemo1();
    }

    private static void demo1() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        IntStream.rangeClosed(1, 3).mapToObj(i -> new Thread(() -> {
            System.out.println("t" + i + " start --------");
            try {
                TimeUnit.SECONDS.sleep(2);
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t" + i + " end --------");
        })).forEach(Thread::start);


        latch.await();
    }

    public static void testDemo1() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        Thread t1 = new Thread(() -> {
            System.out.println("t1 await");
            try {
                latch.await();
                System.out.println("t1 acquire lock");
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread t2 = new Thread(() -> {
            System.out.println("t2 await");
            try {
                latch.await();
                System.out.println("t2 acquire lock");
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        IntStream.rangeClosed(1, 3).mapToObj(i -> (Runnable) () ->{
            latch.countDown();
        }).map(Thread::new).forEach(Thread::start);

        t1.start();
        t2.start();

        TimeUnit.SECONDS.sleep(6);
    }
}

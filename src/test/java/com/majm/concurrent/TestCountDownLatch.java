package com.majm.concurrent;

import com.majm.MD5;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author majunmin
 * @description
 * @datetime 2021/2/23 12:06 上午
 * @since
 */
public class TestCountDownLatch {

    @Test
    public void testCountDownLatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        Thread thread1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        });
        Thread thread2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        });

        thread1.start();
        thread2.start();

        latch.await();

        System.out.println("count down latch end !");
    }


    @Test
    public void testDemo(){
        String encryptS = MD5.encode("1297_5fc5351e-da3d-4686-9ae6-490e994f072a_1614582606925_20200404_20200404_1_1000");
        System.out.println(encryptS);
    }


    /**
     * 第一种横向遍历的方式 的好耗时  要小于 第二种纵向遍历的方式
     *
     * CPU 高速缓存行  CacheLine
     *
     * 一个cpu缓存行大小通常在 64字节（这取决于 CPU）,他有效引用主内存中的一个地址
     *
     *
     *
     */
    @Test
    public void testx(){
        long[][] arr = new long[1024 * 1024][8];

        Instant now = Instant.now();
        for (int i = 0; i < 1024 * 1024; i++) {
            for (int j = 0; j < 8; j++) {
                arr[i][j] = 1;
            }
        }
        System.out.println(Duration.between(now, Instant.now()).toMillis());

        now = Instant.now();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                arr[j][i] = 1;
            }
        }
        System.out.println(Duration.between(now, Instant.now()).toMillis());
    }
}

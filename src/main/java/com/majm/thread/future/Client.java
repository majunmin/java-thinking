package com.majm.thread.future;

import java.util.concurrent.TimeUnit;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 13:32
 * @since
 */
public class Client {

    public static void main(String[] args) throws InterruptedException {
        FutureService<Void, Void> fs1 = FutureService.newService();
        Future<?> future1 = fs1.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("hello");
        });
        Object o = future1.get();

        FutureService<String, Integer> fs2 = FutureService.newService();
        Future<?> future = fs2.submit((in) -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return in.length();
        }, "hello world");
        System.out.println(future.get());


    }
}

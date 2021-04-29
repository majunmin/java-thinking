package com.majm.thread;

import java.util.concurrent.TimeUnit;

/**
 * 一句话功能简述 </br>
 * 当一个线程处于被阻塞状态或者试图执行一个阻塞操作时，使用Thread.interrupt()方式中断该线程，注意此时将会抛出一个InterruptedException的异常，
 * 同时中断状态将会被复位(由中断状态改为非中断状态)
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-30 17:52
 * @since
 */
public class InteruptDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                while (true) {
                    // Thread.interrupted()
                    // 当前线程的中断标志位，同时会重置中断标志位
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("线程中断标志位 为 true");
                    }
                    TimeUnit.SECONDS.sleep(2);
                }
            } catch (Exception e) {
                System.out.println("Interruted When Sleep");
                boolean interupt = Thread.interrupted();
                System.out.println("interupt : " + interupt);
            }
        });


        t1.start();
        System.out.println(t1.isInterrupted());
        TimeUnit.SECONDS.sleep(2);

        t1.interrupt();

        TimeUnit.SECONDS.sleep(100);


    }
}

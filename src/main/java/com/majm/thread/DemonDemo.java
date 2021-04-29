package com.majm.thread;

import java.util.concurrent.TimeUnit;

/**
 * 一句话功能简述 </br>
 *
 *
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 07:46
 * @since
 */
public class DemonDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while(true){
                System.out.println("hello");
            }
        });
        t1.setDaemon(true);
        t1.start();

        TimeUnit.SECONDS.sleep(2);
    }
}

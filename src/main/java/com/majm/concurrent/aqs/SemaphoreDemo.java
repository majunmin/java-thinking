package com.majm.concurrent.aqs;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/20 2:40 下午
 * @since
 */
public class SemaphoreDemo {

    private static final int THREAD_COUNT = 30;

    private static ExecutorService threadPool = new ThreadPoolExecutor(30, 30, 500, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(500),
            new BasicThreadFactory.Builder().namingPattern("pool-%s").build(),
            new ThreadPoolExecutor.DiscardPolicy());

    private static Semaphore s = new Semaphore(10);

    public static void main(String[] args) throws InterruptedException {
        toiletRaceDemo();
//        TimeUnit.SECONDS.sleep(60);
    }

    public static void toiletRaceDemo() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPool.submit(new Employee(i + "", s));
        }

        threadPool.shutdown();
    }
}

class Employee implements Runnable {
    private String id;
    private Semaphore semaphore;
    private static Random ran = new Random();

    public Employee(String id, Semaphore semaphore) {
        this.id = id;
        this.semaphore = semaphore;
    }


    @Override
    public void run() {
        try {
            semaphore.acquire();
            System.out.println(this.id + "is using toliet");
            TimeUnit.SECONDS.sleep(2);
            System.out.println(this.id + "is leaving");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            semaphore.release();
        }

    }
}

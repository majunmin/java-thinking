package com.majm.concurrent.completable;

import com.sun.xml.internal.ws.util.CompletedFuture;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/6 9:37 下午
 * @since
 */
public class CompletableFutureDemo {

    public static void main(String[] args) {
//        // 任务 1：洗水壶 -> 烧开水
//        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
//            System.out.println("T1: 洗水壶...");
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//
//        // 任务 2：洗茶壶 -> 洗茶杯 -> 拿茶叶
//        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("T2: 洗茶壶...");
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            System.out.println("T2: 洗茶杯...");
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            System.out.println("T2: 拿茶叶...");
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return " 龙井 ";
//        });
//
//        // 任务 3 : 任务1 和任务2 执行结束后执行: 泡茶
//        CompletableFuture<String> f3 = f1.thenCombine(f2, (t, tf) -> {
//            System.out.println("T1: 拿到茶叶:" + tf);
//            System.out.println("T1: 泡茶...");
//            return " 上茶:" + tf;
//        });
//        System.out.println(f3.join());

        CompletableFutureDemo demo = new CompletableFutureDemo();
        demo.testDemo1();

    }

    public void testDemo1(){
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 50, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(500),
                new BasicThreadFactory.Builder().namingPattern("pool-%d").build(),
                new ThreadPoolExecutor.DiscardPolicy());

        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "majm";
        }, threadPool).exceptionally(e -> {
            System.out.println(e.getMessage());
            return "case" + "jim";
        });

        cf.thenAccept(t -> {
            System.out.println("hello: " + t);
        });

        threadPool.shutdown();

        System.out.println("shut down");


        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // f1 f2 f3 并行执行
        // join 等待  f1 f2 f3 执行完成
        CompletableFuture.allOf(f1,f2,f3).join();
    }
}

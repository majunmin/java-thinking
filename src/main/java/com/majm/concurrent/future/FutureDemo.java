package com.majm.concurrent.future;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/5 9:11 下午
 * @since
 */
public class FutureDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        Task task = new Task();
        Future<?> result = threadPool.submit(task);
        threadPool.shutdown();

        TimeUnit.SECONDS.sleep(3);

        System.out.println("main thread is executor...");

        System.out.println(result.get());
    }
}

class Task implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("子线程在进行计算");
       TimeUnit.SECONDS.sleep(3);
        int sum = 0;
        for (int i = 0; i <= 100; i++) {
            sum += i;
        }
        return sum;
    }
}

package com.majm.concurrent.future;


import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class FutureTaskDemoTest {

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        // create futureTask
        FutureTask<Integer> futureTask = new FutureTask<>(() -> 1 + 2);

        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        threadPool.submit(futureTask);

        Integer result = futureTask.get();
        System.out.println(result);
    }

    /**
     * 使用FutureTask 可以很容易获取 子线程执行结果
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test2() throws ExecutionException, InterruptedException {
        // create futureTask
        FutureTask<Integer> futureTask = new FutureTask<>(() -> 1 + 2);

        new Thread(futureTask).start();

        Integer result = futureTask.get();
        System.out.println(result);
    }

    @Test
    public void test3() {
        boolean flag = true;
        if (flag) System.out.println("Hello, Java!");
        if (flag == true) System.out.println("Hello, JVM!");
    }
}
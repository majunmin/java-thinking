package com.majm.thread.future;


import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 提交并执行任务 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 10:12
 * @since
 */
public class FutureServiceImpl<IN, OUT> implements FutureService<IN, OUT> {

    ExecutorService threadPool = new ThreadPoolExecutor(1, 10, 5, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            new BasicThreadFactory.Builder().namingPattern("FUTURE-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());


    @Override
    public Future<?> submit(Runnable r) {

        FutureTask<Void> future = new FutureTask<>();

        threadPool.submit(() -> {
            r.run();
            future.finish(null);
        });
        return future;

    }

    @Override
    public Future<OUT> submit(Task<IN, OUT> task, IN input) {
        FutureTask<OUT> future = new FutureTask<>();
        threadPool.submit(() -> {
            OUT result = task.get(input);
            future.finish(result);
        });
        return future;
    }
}

package com.majm.thread.future;


/**
 * FutureService 接口 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 10:10
 * @since
 */
public interface FutureService<IN, OUT> {

    Future<?> submit(Runnable r);

    Future<OUT> submit(Task<IN, OUT> task, IN input);

    /**
     * 高层不应该依赖于底层
     * @param <T>
     * @param <R>
     * @return
     */
    static <T, R> FutureService<T, R> newService() {
        return new FutureServiceImpl<T, R>();
    }
}

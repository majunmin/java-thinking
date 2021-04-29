package com.majm.thread.future;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 10:10
 * @since
 */
public class FutureTask<T> implements Future<T> {

    // 计算结果
    private T result;

    // 任务是否完成
    private boolean isDone;

    private final Object mutex = new Object();


    /**
     * 阻塞调用
     * return when  @link{isDone} is true
     *
     * @return
     */
    @Override
    public T get() throws InterruptedException {
        synchronized (mutex) {
            // mutex 被中断后  还能继续阻塞
            while (!isDone) {
                mutex.wait();
            }
            return result;
        }
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    /**
     * 完成通知
     */
    public void finish(T result) {
        synchronized (mutex) {
            // 多次调用  finish() 的幂等性
            // balking 模式
            if (isDone) {
                return;
            }

            // 计算完成 填充结果 和标志位 isDone, 唤醒处于阻塞状态的 Future#get()
            this.result = result;
            this.isDone = true;
            mutex.notifyAll();
        }
    }
}

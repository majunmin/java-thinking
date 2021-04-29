package com.majm.thread.future;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 10:09
 * @since
 */
public interface Future<T> {

    T get() throws InterruptedException;

    boolean isDone();
}

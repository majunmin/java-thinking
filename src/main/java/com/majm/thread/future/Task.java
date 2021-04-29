package com.majm.thread.future;

/**
 * 函数式接口 </br>
 *
 * 有输入和输出的任务
 * 通常用于一个计算任务
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 13:05
 * @since
 */
@FunctionalInterface
public interface Task<IN, OUT> {

    OUT get(IN input);
}

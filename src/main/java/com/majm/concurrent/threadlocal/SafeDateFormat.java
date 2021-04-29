package com.majm.concurrent.threadlocal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * sdfTL 的初始化 如果是 通过  ThreadLocal#set()
 * 那么通过 get()返回的对象是 共享变量(多线程拿到的对象是同一个)
 * @author majunmin
 * @description
 * @datetime 2021/3/6 1:10 下午
 * @since
 */
public class SafeDateFormat {

    private static final ThreadLocal<SimpleDateFormat> sdfTL =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd hh:MM:ss"));

    static DateFormat get(){
        // 不同线程执行下面代码返回的 df是不同的
        return sdfTL.get();
    }


}

package com.majm.concurrent.designpattern.workerthread;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 避免频繁的创建和销毁线程 - 线程池
 *
 * 1. 创建有界队列来接收任务
 * 2. 当请求量大于有界队列的容量时，就需要合理地拒绝请求`AbortPolicy`,创建线程时合理的指明拒绝策略
 * 3. 为线程池赋予一个合理的名字,和业务相关,便于排查问题
 *
 * @author majunmin
 * @description
 * @datetime 2021/3/6 4:40 下午
 * @since
 */
public class ThreadPoolDemo {

    private static final ThreadPoolExecutor theadPool = new ThreadPoolExecutor(50, 500, 60L, TimeUnit.SECONDS,
            //创建有界队列
            new LinkedBlockingQueue<>(1000),
            // 命名的 threadFactory
            new BasicThreadFactory.Builder().namingPattern("demo-threadPool-%s").build(),
            // 建议根据业务需求实现 RejectedExecutionHandler
            new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) throws IOException {


        final ServerSocketChannel ssc =
                ServerSocketChannel.open().bind(new InetSocketAddress(8080));
        // 处理请求
        try {
            while (true) {
                // 接收请求
                SocketChannel sc = ssc.accept();
                // 将请求处理任务提交给线程池
                theadPool.execute(() -> {
                    try {
                        // 读 Socket
                        ByteBuffer rb = ByteBuffer
                                .allocateDirect(1024);
                        sc.read(rb);
                        // 模拟处理请求
                        Thread.sleep(2000);
                        // 写 Socket
                        ByteBuffer wb =
                                (ByteBuffer) rb.flip();
                        sc.write(wb);
                        // 关闭 Socket
                        sc.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } finally {
            ssc.close();
            theadPool.shutdown();
        }

    }
}

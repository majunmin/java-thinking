package com.majm.concurrent.designpattern.threadpermessage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * OpenJDK Loom项目  Fiber 纤程实现(轻量级进程)
 * https://dev.to/psychoir/openjdk-loom-and-structured-concurrency-2e0e
 * @author majunmin
 * @description
 * @datetime 2021/3/6 4:36 下午
 * @since
 */
public class FiberEcho {

    public static void main(String[] args) throws IOException {
//        final ServerSocketChannel ssc =
//                ServerSocketChannel.open().bind(new InetSocketAddress(8080));
//        // 处理请求
//        try {
//            while (true) {
//                // 接收请求
//                final SocketChannel sc = ssc.accept();
//                Fiber.schedule(() -> {
//                    try {
//                        // 读 Socket
//                        ByteBuffer rb = ByteBuffer.allocateDirect(1024);
//                        sc.read(rb);
//                        // 模拟处理请求- 挂起当前线程
//                        LockSupport.parkNanos(2000 * 1000000);
//                        // 写 Socket
//                        ByteBuffer wb = (ByteBuffer) rb.flip();
//                        sc.write(wb);
//                        // 关闭 Socket
//                        sc.close();
//                    } catch (Exception e) {
//                        throw new UncheckedIOException(e);
//                    }
//                });
//            }//while
//        } finally {
//            ssc.close();
//        }
    }

}

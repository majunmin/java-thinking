package com.majm.concurrent.designpattern.threadpermessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/6 4:29 下午
 * @since
 */
public class CommonEcho {


    public static void main(String[] args) throws IOException {
        final ServerSocketChannel ssc =
                ServerSocketChannel.open().bind(new InetSocketAddress(8080));

        try {
            while (true) {
                //接收请求
                SocketChannel sc = ssc.accept();
                //为每一个请求 创建一个线程  ε=(´ο｀*)))唉  开销贼大
                new Thread(() -> {
                    try {
                        ByteBuffer rb = ByteBuffer.allocateDirect(1024);
                        sc.read(rb);
                        // 模拟处理请求
                        TimeUnit.SECONDS.sleep(2);

                        ByteBuffer wb = (ByteBuffer) rb.flip();
                        sc.write(wb);
                        // close socket
                        sc.close();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } finally {
            ssc.close();
        }
    }
}

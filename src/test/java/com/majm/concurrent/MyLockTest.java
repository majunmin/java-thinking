package com.majm.concurrent;

import com.majm.concurrent.aqs.MyLock;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class MyLockTest {

    private int a;

    @Test
    public void lock() {
        MyLock lock = new MyLock();

        IntStream.range(0, 50).forEach(j -> {
            new Thread(() -> {
                for (int i = 0; i < 1000; i++) {
                    lock.lock();
                    a++;
                    lock.unLock();
                }
            }).start();
        });

        IntStream.range(0, 50).forEach(j -> {
            new Thread(() -> {
                for (int i = 0; i < 1000; i++) {
                    lock.lock();
                    a--;
                    lock.unLock();
                }
            }).start();
        });


        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(a);
    }

    @Test
    public void unLock() {

        String[] arr = new String[]{"com.xxx", "5fc123d-rf56-4rg6-8u6t-4909094f852a", "1614582606925", "20200404", "20200404", "1", "50"};
        Arrays.sort(arr);
        String join = String.join("_", arr);
        System.out.println(join);
    }
}

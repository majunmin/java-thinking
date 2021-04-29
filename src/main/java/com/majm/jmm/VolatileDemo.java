package com.majm.jmm;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/6 11:15 上午
 * @since
 */
public class VolatileDemo {
    int x = 0;
    volatile boolean v = false;

    public void writer() {
        x = 42;
        v = true;
    }


    public void reader() {
        if (v) {
            System.out.println(x);
        }
    }

    public static void main(String[] args) {
        VolatileDemo demo = new VolatileDemo();

        demo.writer();
        demo.reader();
    }
}

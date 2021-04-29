package com.majm.performance;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/20 7:28 下午
 * @since
 */
public class FalseShareing implements Runnable {

    public static int THREAD_COUTN = 4;
    public static final long ITERATIONS = 500_000_000L;
    public final int arrayIndex;

    private static VolatileLong[] longs = new VolatileLong[THREAD_COUTN];

    static {
        for (int i = 0; i < longs.length; i++) {
            longs[i] = new VolatileLong();
        }
    }

    public FalseShareing(final int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public static void main(final String[] args) throws Exception {
        Instant now = Instant.now();
        runTest();
        System.out.println(Duration.between(now, Instant.now()).toMillis());
    }

    private static void runTest() {

        List<Thread> threadList = IntStream.range(0, THREAD_COUTN)
                .mapToObj(FalseShareing::new).map(Thread::new)
                .collect(Collectors.toList());
        threadList.forEach(Thread::start);
        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void run() {
        long i = ITERATIONS + 1;
        while (0 != --i) {
            longs[arrayIndex].value = i;
        }
    }


    public static class VolatileLong {
        public volatile long value = 0L;
        public long p1, p2, p3, p4, p5, p6, p7;
    }

}
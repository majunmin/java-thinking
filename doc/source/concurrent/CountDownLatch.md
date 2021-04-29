# CountDownLatch


> Aqs 同步队列实现, 通过 AQS 的 共享模式实现
>  AQS.state 记录的就是 CountDownLatch 传入的 N
> 每次调用 countDown()执行的就是 将 state -1 操作,当state == 0 时,那个将 state 减到0的线程就会负责唤醒所有调用了 await() 的线程
> 

CountDownLatch#await()
CountDownLatch#countDown()

AQS.state: 含义是调用 countDown() 的次数


```java
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            // 典型的操作 while + cas(volatile)
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }
```
package com.majm.concurrent.aqs;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 可重入
 *
 * @author majunmin
 * @description
 * @datetime 2021/2/23 11:26 上午
 * @since
 */
public class MyLock {

    private Sync sync;

    public static class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int acquires) {
            Thread curThread = Thread.currentThread();
            // 该锁是支持可重入的
            // getExclusiveOwnerThread() 获取当前持有 排它锁的线程
            int c = getState();
            if (c == 0) {
                //  state = 0 表示 没有线程持有锁
                // 原子的设置 state状态
                if (compareAndSetState(0, 1)) {
                    setExclusiveOwnerThread(curThread);
                    return true;
                }
            } else if (getExclusiveOwnerThread() == curThread) {
                int nextc = c + acquires;
                setState(nextc);
                return true;
            }

            return false;
        }

        @Override
        protected boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException();
            }

            boolean free = false;
            // c == 0 表示当前没有线程持有锁, 所以将拥有锁的线程置为null
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }

        public void showLockThread() {
            if (getExclusiveOwnerThread() != null) {
                System.out.println("CurrentThread: " + getExclusiveOwnerThread().getName());
            }
        }
    }

    public MyLock() {
        this.sync = new Sync();
    }

    public void lock() {
        sync.acquire(1);
    }

    public void unLock() {
        sync.release(1);
    }

    public void showLockThread(){
        sync.showLockThread();
    }
}

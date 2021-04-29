package com.majm.concurrent.aqs;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/15 7:32 下午
 * @since
 */
public class ReadWriteLock {

    public static void main(String[] args) {
        ReentrantReadWriteLock.ReadLock readLock = new ReentrantReadWriteLock().readLock();


        readLock.lock();
        readLock.unlock();

    }
}

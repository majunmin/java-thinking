package com.majm.concurrent.threadlocal;

import com.majm.common.Person;
import org.junit.Before;
import org.junit.Test;

public class ThreadLocalDemoTest {

    private static final ThreadLocal<Person> THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Person person = new Person();
        person.setAge(18);
        return person;
    });

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testDemo1() throws InterruptedException {

        Person p0 = THREAD_LOCAL.get();
        System.out.println(Thread.currentThread() + ":" + p0.getAge());

        Thread t1 = new Thread(() -> {
            Person p1 = THREAD_LOCAL.get();
            System.out.println(Thread.currentThread() + ":" + p1.getAge());
            p1.setAge(100);
        });
        Thread t2 = new Thread(() -> {
            Person p2 = THREAD_LOCAL.get();
            System.out.println(Thread.currentThread() + ":" + p2.getAge());
            p2.setAge(100);
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

}



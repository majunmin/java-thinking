package com.majm.unsafe;

import com.majm.common.Person;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-02 00:11
 * @since
 */
public class UnsafeDemo {

    public static void main(String[] args) throws InstantiationException, NoSuchFieldException, IllegalAccessException {
        // 抛出异常 SecurityException
        // Unsafe unsafe = Unsafe.getUnsafe();
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);

        // 通过Field得到该Field对应的具体对象，传入null是因为该Field为static的
        Unsafe unsafe = (Unsafe) field.get(null);
        System.out.println(unsafe.pageSize());

        // allocateInstance 创建对象 但是不调用 Constructor
        Person p = (Person) unsafe.allocateInstance(Person.class);
        System.out.println(p);
    }
}

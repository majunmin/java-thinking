package com.majm.collection;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/12 1:26 下午
 * @since
 */
public class HashMapDemo {

    private static Map<String, String> hashMap;

    public static void main(String[] args) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.putIfAbsent("hello1", "world1");
        hashMap.putIfAbsent("hello1", "worldx");
        hashMap.putIfAbsent("hello2", null);
        hashMap.putIfAbsent("hello2", "wordz");

        System.out.println(hashMap.get("hello1"));
        System.out.println(hashMap.get("hello2"));

//        threadSafeTest();


        ConcurrentHashMap<String, String> cMap = new ConcurrentHashMap<>();
        cMap.put("key1", "value1");
//        cMap.put("key1", null); // java.lang.NullPointerException

        testLinkedHashMapInsert();
        testHashMapInsert();

        testLinkedHashOperate();

    }


    public static void threadSafeTest() {
        hashMap = new HashMap<>(1);

        IntStream.rangeClosed(1, 100).mapToObj(i -> new Thread(() -> {
            for (int j = 0; j < 50; j++) {
                hashMap.put("key" + j * i, "value " + j);
            }
        })).forEach(Thread::start);
    }


    public static void testLinkedHashMapInsert() {
        Map<String, String> map = new LinkedHashMap<>();
        Instant start = Instant.now();
        for (int i = 0; i < 20000; i++) {
            map.put("key" + i, "value" + "i");
        }

        System.out.println("cost time : " + Duration.between(start, Instant.now()).toMillis());
    }

    /**
     * hello1 : value1
     * hello2 : valuex
     * hello3 : value3
     * hello4 : value4
     * linked hash map ------
     * hello1 : value1
     * hello3 : value3
     * hello4 : value4
     * hello2 : valuex
     */
    public static void testLinkedHashOperate() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("hello1", "value1");
        map.put("hello2", "value2");
        map.put("hello3", "value3");
        map.put("hello4", "value4");
        map.put("hello2", "valuex");

        map.forEach((key, value) ->{
            System.out.println(key + " : " + value);
        });

        System.out.println("linked hash map ------");

        // accessOrder = true : 遍历按照访问顺序打印
        map = new LinkedHashMap<>(16, 0.75f, true);
        map.put("hello1", "value1");
        map.put("hello2", "value2");
        map.put("hello3", "value3");
        map.put("hello4", "value4");
        map.put("hello2", "valuex");

        map.forEach((key, value) ->{
            System.out.println(key + " : " + value);
        });
    }



    public static void testHashMapInsert() {
        Map<String, String> map = new HashMap<>();
        Instant start = Instant.now();
        for (int i = 0; i < 20000; i++) {
            map.put("key" + i, "value" + "i");
        }

        System.out.println("cost time : " + Duration.between(start, Instant.now()).toMillis());
    }
}

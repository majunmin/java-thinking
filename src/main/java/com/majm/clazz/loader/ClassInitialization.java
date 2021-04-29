package com.majm.clazz.loader;

import java.util.Random;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 16:16
 * @since
 */
public class ClassInitialization {

    public static Random rand = new Random();

    public static void main(String[] args) {
        //字面常量获取方式获取Class对象
        Class initable = Initable.class;
        System.out.println("After creating Initable ref");

        // **不触发类初始化**
        System.out.println(Initable.staticFinal);
        //会触发类初始化
        System.out.println(Initable.staticFinal2);

        //会触发类初始化
        System.out.println(Initable2.staticNonFinal);
        //forName方法获取Class对象
        try {
            Class initable3 = Class.forName("com.majm.clazz.Initable3");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("After creating Initable3 ref");
        System.out.println(Initable3.staticNonFinal);
    }


    /**
     * Class 与 泛型
     *   * Class<?>总是优于直接使用Class，至少前者在编译器检查时不会产生警告信息
     *   * 通配符
     *   * 运行时擦除
     */
    public static void testGeneric() {
        Class<Integer> integerClass = int.class;
        Class intClass = int.class;
//        Class<Number> numberGenClass = int.class;
        Class<?> intClassGeneric = int.class;
        Class<? extends Number> intClassGeneric2 = int.class;
        Class<? super Integer> intClassGeneric3 = int.class;

        // 泛型 在编译期间  进行限制
//        integerClass = double.class;
        intClass = double.class;
        intClassGeneric = double.class;
        intClassGeneric2 = double.class;
//        intClassGeneric3 = double.class;
        intClassGeneric3 = Number.class;


    }

    static class A{

    }
}


class Initable {
    //编译期静态常量
    static final int staticFinal = 47;

    //非编期静态常量
    static final int staticFinal2 =
            ClassInitialization.rand.nextInt(1000);

    static {
        System.out.println("Initializing Initable");
    }
}

class Initable2 {
    //静态成员变量
    static int staticNonFinal = 147;

    static {
        System.out.println("Initializing Initable2");
    }
}

class Initable3 {
    //静态成员变量
    static int staticNonFinal = 74;

    static {
        System.out.println("Initializing Initable3");
    }
}

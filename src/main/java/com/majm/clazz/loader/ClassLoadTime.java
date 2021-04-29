package com.majm.clazz.loader;

/**
 * 1. 获取Class对象引用的方式3种，通过继承自Object类的getClass方法，Class类的静态方法forName以及字面常量的方式”.class”。
 * 2. 其中实例类的getClass方法和Class类的静态方法forName都将会触发类的初始化阶段，而字面常量获取Class对象的方式则不会触发初始化。
 * 3. 初始化是类加载的最后一个阶段，也就是说完成这个阶段后类也就加载到内存中(Class对象在加载阶段已被创建)，此时可以对类进行各种必要的操作了（如new对象，调用静态成员等），注意在这个阶段，才真正开始执行类中定义的Java程序代码或者字节码。
 * @author majunmin
 * @description
 * @datetime 2021-03-31 15:59
 * @since
 */
class Candy {
    static {
        System.out.println("Loading Candy");
    }
}

class Gum {
    static {
        System.out.println("Loading Gum");
    }
}

class Cookie {
    static {
        System.out.println("Loading Cookie");
    }
}

public class ClassLoadTime {
    public static void main(String[] args) {
        System.out.println("inside main");
        new Candy();

        try {
            Class.forName("com.majm.clazz.loader.Gum");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 字面量不会触发 类的初始化
        Class<?> cookieClass = Cookie.class;
    }
}

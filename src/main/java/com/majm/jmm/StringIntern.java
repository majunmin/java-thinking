package com.majm.jmm;

/**
 * JDK8 </br>
 *
 * String#intern()
 *
 * JDK7 以后,intern()不需要拷贝字符串的实例到永久代了,字符串常量池已经移到堆中
 * 那么只需要在常量池中记录一下首次出现的实例引用即可, 因此 String#intern() 返回的引用和 首次创建的那个字符串的引用是同一个
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-08 23:03
 * @since
 */
public class StringIntern {

    public static void main(String[] args) {
        String str = "java";
        String str1 = new StringBuilder("ja").append("va").toString();
        System.out.println(str1 == str1.intern());
        System.out.println(str == str1.intern());

        String str2 = new String("计算机") + new String("软件");
        System.out.println(str2 == str2.intern());

        String str3 = new StringBuilder("计算").append("机软件").toString();
        System.out.println(str3 == str3.intern());


    }
}

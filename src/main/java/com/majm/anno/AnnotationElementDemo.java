package com.majm.anno;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-01 14:22
 * @since
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationElementDemo {

    enum Status {NORMAL, FIXED}

    Status status() default Status.NORMAL;

    String name() default "";

    Class<?> testCase() default Void.class;

    boolean showSupport() default false;

    //注解嵌套
    Reference reference() default @Reference(next = true);

    //数组类型
    long[] value();
}

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface Reference {
    boolean next() default false;
}

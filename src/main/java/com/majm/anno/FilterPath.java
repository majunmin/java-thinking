package com.majm.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * // 反编译后的代码
 *Compiled from "FilterPath.java"
 * public interface com.majm.anno.FilterPath extends java.lang.annotation.Annotation {
 *   public abstract java.lang.String value();
 * }
 *
 *
 */
@Inherited
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = FilterPaths.class)
public @interface FilterPath {
    String value() default "";
}

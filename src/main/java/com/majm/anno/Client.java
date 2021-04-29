package com.majm.anno;

import lombok.extern.slf4j.Slf4j;
import sun.reflect.annotation.AnnotationType;

import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import java.util.logging.Filter;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-01 23:09
 * @since
 */
@Slf4j
@FilterPath("/web/update")
@FilterPath("/web/add")
@FilterPath("/web/delete")
public class Client {

    public static void main(String[] args) {
        AnnotatedElement element = Client.class;

        FilterPath[] annotationsByType = element.getAnnotationsByType(FilterPath.class);
        FilterPath[] declaredAnnotationsByType = element.getDeclaredAnnotationsByType(FilterPath.class);

        for (FilterPath filterPath : annotationsByType) {
            System.out.println(filterPath.value());
        }

        System.out.println("-----------------------");

        for (FilterPath filterPath : declaredAnnotationsByType) {
            System.out.println(filterPath.value());
        }

        System.out.println("-----------------------");

        // null
        System.out.println(element.getAnnotation(FilterPath.class));
        // @com.majm.anno.FilterPaths(value=[@com.majm.anno.FilterPath(value=/web/update), @com.majm.anno.FilterPath(value=/web/add), @com.majm.anno.FilterPath(value=/web/delete)])
        System.out.println(element.getAnnotation(FilterPaths.class));

        log.info("-----------------------");


        testCase();
    }

    public static void testCase(){

        // 判断注解上是否有元注解  @Inherited
        System.out.println(AnnotationType.getInstance(FilterPath.class).isInherited());

    }
}

@FilterPath("/cc/dd")
class Parent {

}

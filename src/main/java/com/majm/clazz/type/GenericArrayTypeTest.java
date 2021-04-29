package com.majm.clazz.type;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 22:24
 * @since
 */
@Slf4j
public class GenericArrayTypeTest<T> {

    public void testGenericArrayType(List<String>[] pTypeArray, T[] vTypeArray
            , List<String> list, String[] strings, GenericArrayTypeTest[] test) {
    }

    public static void testGenericArrayType() {
        Method[] methods = GenericArrayTypeTest.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("main")) {
                continue;
            }
            log.info("declare Method: {} ", method);

            Type[] types = method.getGenericParameterTypes();
            for (Type type : types) {
                if (type instanceof ParameterizedType) {
                    log.info("ParameterizedType type: {}", type);
                } else if (type instanceof GenericArrayType) {
                    log.info("GenericArrayType type: {}", type);

                    /**
                     * 获取泛型数组中元素的类型，要注意的是：无论从左向右有几个[]并列，
                     * 这个方法仅仅脱去最右边的[]之后剩下的内容就作为这个方法的返回值。
                     */
                    Type componentType = ((GenericArrayType) type).getGenericComponentType();
                    log.info("genericComponentType : {}", componentType);
                } else if (type instanceof WildcardType) {
                    log.info("WildcardType: {}", type);
                } else if (type instanceof TypeVariable) {
                    log.info("TypeVariable: {}", type);
                } else {
                    log.info("Type: {}", type);
                }
            }
        }
    }
}

/**
 * @34 - declare Method: public void com.majm.clazz.type.GenericArrayTypeTest.testGenericArrayType(java.util.List[],java.lang.Object[],java.util.List,java.lang.String[],com.majm.clazz.type.GenericArrayTypeTest[])
 * @41 - GenericArrayType type: java.util.List<java.lang.String>[]
 * @44 - genericComponentType : java.util.List<java.lang.String>
 * @41 - GenericArrayType type: T[]
 * @44 - genericComponentType : T
 * @39 - ParameterizedType type: java.util.List<java.lang.String>
 * @50 - Type: class [Ljava.lang.String;
 * @50 - Type: class [Lcom.majm.clazz.type.GenericArrayTypeTest;
 * @34 - declare Method: public static void com.majm.clazz.type.GenericArrayTypeTest.testGenericArrayType()
 */

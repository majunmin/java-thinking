package com.majm.clazz.type;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

/**
 * 通配符表达式，或泛型表达式，它虽然是Type的一个子接口，但并不是Java类型中的一种，表示的仅仅是类似 ? extends T、? super K这样的通配符表达式。、
 * ？—通配符表达式，表示通配符泛型，但是WildcardType并不属于Java-Type中的一钟；
 * 例如：List< ? extends Number> 和 List< ? super Integer>；
 * <p/>
 * 1、Type[] getUpperBounds(); //获得泛型表达式上界（上限） 获取泛型变量的上边界（extends）
 * 2、Type[] getLowerBounds(); //获得泛型表达式下界（下限） 获取泛型变量的下边界（super）
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-01 00:03
 * @since
 */
@Slf4j
public class WildCardTypeDemo {

    private List<? extends Number> a;

    private List<? super String> b;

    private List<String> c;

    private List<?>[] d;

    private Class<?> clazz;

    private String str;

    public static void testWilCardType() {
        Field[] declaredFields = WildCardTypeDemo.class.getDeclaredFields();
        for (Field field : declaredFields) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) genericType;
                Type[] typeArguments = type.getActualTypeArguments();
                for (Type typeArgument : typeArguments) {
                    log.info("{}: 获取ParameterizedType: {}", field.getName(), type);
                    if (typeArgument instanceof WildcardType) {
                        printWildCardType(((WildcardType) typeArgument));
                    }
                }
            } else if (genericType instanceof GenericArrayType) {
                log.info("{} GenericArrayType: {}", field.getName(), genericType);
                GenericArrayType type = (GenericArrayType) genericType;
                Type componentType = type.getGenericComponentType();
                if (componentType instanceof WildcardType){
                    WildcardType wildcardType = (WildcardType) componentType;
                    printWildCardType(wildcardType);
                }
            } else if (genericType instanceof TypeVariable){
                TypeVariable<?> type = (TypeVariable<?>) genericType;
                log.info("{} TypeVariable: {}", field.getName(), type);
            } else {
                log.info("{} type: {}", field.getName(), genericType);
            }
        }
    }

    private static void printWildCardType(WildcardType typeArgument) {
        for (Type upperBound : typeArgument.getUpperBounds()) {
            log.info("UpperBound : {}", upperBound);
        }

        for (Type lowerBound : typeArgument.getLowerBounds()) {
            log.info("LowerBound: {}", lowerBound);
        }
    }
}

/**
 * testWilCardType() @64 - log type: interface org.slf4j.Logger
 * testWilCardType() @47 - a: 获取ParameterizedType: java.util.List<? extends java.lang.Number>
 * printWildCardType() @71 - UpperBound : class java.lang.Number
 *
 * testWilCardType() @47 - b: 获取ParameterizedType: java.util.List<? super java.lang.String>
 * printWildCardType() @71 - UpperBound : class java.lang.Object
 * printWildCardType() @75 - LowerBound: class java.lang.String
 *
 * testWilCardType() @47 - c: 获取ParameterizedType: java.util.List<java.lang.String>
 *
 * testWilCardType() @47 - clazz: 获取ParameterizedType: java.lang.Class<?>
 * printWildCardType() @71 - UpperBound : class java.lang.Object
 *
 * testWilCardType() @64 - str type: class java.lang.String
 */

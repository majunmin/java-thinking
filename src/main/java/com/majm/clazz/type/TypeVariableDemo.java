package com.majm.clazz.type;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 * Interface TypeVariable<D extends GenericDeclaration> ，
 * D - the type of generic declaration that declared the underlying type variable.
 * 类型变量是类型变量的公共超接口.类型变量是第一次使用反射方法创建的,如在这个包中指定的
 * 如果类型变量T由类型（即类、接口或注释类型）T引用，并且T由第n个封闭类T（参见JLS.1.2）来声明，
 * 那么T的创建需要T的第i个包围类的分辨率（参见JVMS 5），对于i＝0到n，包含。创建类型变量不能导致其边界的创建。
 * 重复创建类型变量没有任何效果。
 * <p>
 * 可以在运行时实例化多个对象以表示给定的类型变量。即使类型变量只创建一次，
 * 但这并不意味着缓存表示类型变量的实例的任何要求。
 * 但是，表示一个类型变量的所有实例必须是相等的（）。因此，类型变量的用户不能依赖实现该接口的类实例的标识。
 * <p>
 * 泛型的类型变量，指的是List<T>、Map<K,V>中的T，K，V等值，实际的Java类型是TypeVariableImpl
 * （TypeVariable的子类）；
 * 此外，还可以对类型变量加上extend限定，这样会有类型变量对应的上限；值得注意的是，类型变量的上限可以为多个，
 * 必须使用&符号相连接，例如 List<T extends Number & Serializable>；其中，& 后必须为接口；
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 22:59
 * @since
 */
@Slf4j
public class TypeVariableDemo<T extends Number & Serializable, V> {
    // TypeVarialbe
    private T key;

    // TypeVarialbe
    private V value;

    // GenericArrayType V[]  V TypeVariable
    private V[] values;

    // 原始类型
    private String str;

    // ParameterizedType List<T>   T TypeVariable
    private List<T> tList;


    public static void testTypeVariable() {
        Field[] declaredFields = TypeVariableDemo.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if ("log".equals(field.getName())) {
                continue;
            }

            log.info("current fieldName: {}", field.getName());

            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) genericType;
                if (type.getOwnerType() != null) {
                    log.info("{} getOwnType : {}", field.getName(), type.getOwnerType());
                }
                if (type.getRawType() != null) {
                    log.info("{} getRawType: {}", field.getName(), type.getRawType());
                }
            } else if (genericType instanceof GenericArrayType) {
                GenericArrayType type = (GenericArrayType) genericType;
                log.info("GenericArrayType : {}", type);
                Type componentType = type.getGenericComponentType();
                if (componentType instanceof TypeVariable) {
                    TypeVariable<?> typeVariable = (TypeVariable<?>) componentType;
                    printTypeVariable(field.getName(), typeVariable);
                }
            } else if (genericType instanceof TypeVariable) {
                TypeVariable<?> typeVariable = (TypeVariable<?>) genericType;
                printTypeVariable(field.getName(), typeVariable);
            } else {
                log.info("type: {}", genericType);
            }
        }
    }

    /**
     * 1、Type[] getBounds() 类型对应的上限，默认为Object
     * 2、D getGenericDeclaration()  获取声明该类型变量实体，也就是TypeVariableTest<T>中的TypeVariableTest
     * 3、String getName() 获取类型变量在源码中定义的名称；
     *
     * @param fieldName
     * @param typeVariable
     */
    private static void printTypeVariable(String fieldName, TypeVariable typeVariable) {
        for (Type type : typeVariable.getBounds()) {
            log.info(fieldName + ": TypeVariable getBounds " + type);
        }
        log.info("定义Class getGenericDeclaration: " + typeVariable.getGenericDeclaration());
        log.info("getName: " + typeVariable.getName());
    }
}

/**
 *  - current fieldName: key
 *  - key: TypeVariable getBounds class java.lang.Number
 *  - key: TypeVariable getBounds interface java.io.Serializable
 *  - 定义Class getGenericDeclaration: class com.majm.clazz.type.TypeVariableTest
 *  - getName: T
 *
 *  - current fieldName: value
 *  - value: TypeVariable getBounds class java.lang.Object
 *  - 定义Class getGenericDeclaration: class com.majm.clazz.type.TypeVariableTest
 *  - getName: V
 *
 *  - current fieldName: values
 *  - GenericArrayType : V[]
 *  - values: TypeVariable getBounds class java.lang.Object
 *  - 定义Class getGenericDeclaration: class com.majm.clazz.type.TypeVariableTest
 *  - getName: V
 *
 *  - current fieldName: str
 *  - type: class java.lang.String
 *  - current fieldName: {}tList
 *  - tList getRawType: interface java.util.List
 */

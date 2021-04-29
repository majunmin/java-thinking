package com.majm.clazz.type;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-31 21:51
 * @since
 */
@Slf4j
public class ParameterizedTypeDemo {

    private Map<String, ParameterizedTypeDemo> map;
    private Set<String> set;
    private Class<?> clazz;
    private Holder<String> holder;
    private List<String> list;
    private Integer i;
    private Set aSet;
    private List aList;

    private Map.Entry<String, String> entry;

    static class Holder<V> {
    }

    public static void testParameterizedType() {
        Field[] fields = ParameterizedTypeDemo.class.getDeclaredFields();
        for (Field field : fields) {
            if ("log".equals(field.getName())) {
                continue;
            }

            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                log.info("ParameterizedType: {}", parameterizedType);
                for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                    log.info("{}: 获取 ParameterizedType: {}", field.getName(), typeArgument);
                }
                if (parameterizedType.getOwnerType() != null) {
                    log.info("{}:getOwnerType: {}", field.getName(), parameterizedType.getOwnerType());
                } else {
                    log.info("{}:getOwnerType: null", field.getName());
                }
            } else {
                log.info("{} is not parameterizedType", field.getName());
            }
            log.info("-----------------------------------------");
        }
    }
}

/**
 * @47 - map: 获取 ParameterizedType: class java.lang.String
 * @47 - map: 获取 ParameterizedType: class com.majm.clazz.type.ParameterizedTypeDemo
 * @52 - map:getOwnerType: null
 * @47 - set: 获取 ParameterizedType: class java.lang.String
 * @52 - set:getOwnerType: null
 * @47 - clazz: 获取 ParameterizedType: ?
 * @52 - clazz:getOwnerType: null
 * @47 - holder: 获取 ParameterizedType: class java.lang.String
 * @50 - holder:getOwnerType: class com.majm.clazz.type.ParameterizedTypeDemo
 * @47 - list: 获取 ParameterizedType: class java.lang.String
 * @52 - list:getOwnerType: null
 * @55 - i is not parameterizedType
 * @55 - aSet is not parameterizedType
 * @55 - aList is not parameterizedType
 * @47 - entry: 获取 ParameterizedType: class java.lang.String
 * @47 - entry: 获取 ParameterizedType: class java.lang.String
 * @50 - entry:getOwnerType: interface java.util.Map
 */

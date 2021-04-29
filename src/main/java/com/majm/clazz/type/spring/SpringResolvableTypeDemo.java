package com.majm.clazz.type.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-09 08:46
 * @since
 */
@Slf4j
public class SpringResolvableTypeDemo {

    private List<String> listString;
    private List<List<String>> listList;
    private Map<String, Long> maps;
    private Parent<String> parent;

    public Map<String, Long> getMaps() {
        return maps;
    }

    /**
     * @35 - parent type: com.majm.clazz.type.spring.Parent<java.lang.String>
     * @38 - 泛型参数为: class java.lang.String
     */
    public static void doFindParent() {
        Field parent = ReflectionUtils.findField(SpringResolvableTypeDemo.class, "parent");
        ResolvableType resolvableType = ResolvableType.forField(parent);

        log.info("parent type: {}", resolvableType.getType());

        Class<?> resolve = resolvableType.getGeneric(0).resolve();
        log.info("泛型参数为: {}", resolve);
    }

    /**
     * @49 - listString: java.util.List<java.lang.String>
     * @50 - 泛型参数为: class java.lang.String
     */
    public static void doFindListStr() {
        Field listString = ReflectionUtils.findField(SpringResolvableTypeDemo.class, "listString");
        ResolvableType resolvableType = ResolvableType.forField(listString);

        log.info("listString: {}", resolvableType.getType());
        log.info("泛型参数为: {}", resolvableType.getGeneric(0).resolve());
    }


    public static void main(String[] args) {
        doFindParent();
        doFindListStr();
    }
}

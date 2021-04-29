package com.majm.clazz.type.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * spring ResolvableType vs 原生  </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-09 08:24
 * @since
 */
@Slf4j
public class Client {

    public static void main(String[] args) {

        // 获取父类中的泛型
        Type superclass = Children.class.getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) superclass).getActualTypeArguments();
            for (Type argument : actualTypeArguments) {
                log.info("父类ParameterizedType.getActualTypeArguments: {}", argument);
            }
        }

        // 获取接口中的泛型
        Type[] genericInterfaces = Children.class.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType){
                Type[] actualTypeArguments = ((ParameterizedType) genericInterface).getActualTypeArguments();
                for (Type argument : actualTypeArguments) {
                    log.info("父接口ParameterizedType.getActualTypeArguments: {}", argument);
                }
            }
        }

        /**
         * spring resolvableType
         *
         * ResolvableType为所有的java类型提供了统一的数据结构以及API,换句话说，一个ResolvableType对象就对应着一种java类型.
         *  我们可以通过ResolvableType对象获取类型携带的信息
         *  for Example:
         *    1.getSuperType()：获取直接父类型
         *    2.getInterfaces()：获取接口类型
         *    3.getGeneric(int...)：获取类型携带的泛型类型
         *    4.resolve()：Type对象到Class对象的转换
         *
         *    另外，ResolvableType的构造方法全部为私有的，我们不能直接new，只能使用其提供的静态方法进行类型获取：
         *    1.forField(Field)：获取指定字段的类型
         *    2.forMethodParameter(Method, int)：获取指定方法的指定形参的类型
         *    3.forMethodReturnType(Method)：获取指定方法的返回值的类型
         *    4.forClass(Class)：直接封装指定的类型
         */

        ResolvableType resolvableType = ResolvableType.forClass(Children.class);
        ResolvableType superType = resolvableType.getSuperType();
        ResolvableType[] interfaces = resolvableType.getInterfaces();
        ResolvableType[] generics = resolvableType.getGenerics();

        log.info("super: {}", superType.resolveGenerics()[0]);
        for (ResolvableType anInterface : interfaces) {
            log.info("interface: {}", anInterface.resolveGenerics()[0]);
        }
    }
}

package com.majm.clazz.type.spring;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-09 08:22
 * @since
 */
public class Children extends Parent<String> implements IParent<Long> {

}

class Parent<T> {
}


interface IParent<T> {
}



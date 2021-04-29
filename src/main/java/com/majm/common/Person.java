package com.majm.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/1 9:14 下午
 * @since
 */
@Data
public class Person implements Serializable {

    private String name;
    private Integer age;

    public static void main(String[] args) {
        Integer i = 59;
        int j = i;

        String s = "a" + "b" + "c";
        System.out.println(s);

        Arrays.sort(args);

    }
}

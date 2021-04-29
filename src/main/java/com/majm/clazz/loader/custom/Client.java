package com.majm.clazz.loader.custom;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-01 15:50
 * @since
 */
public class Client {

    public static final String rootDir = "/Users/majunmin/IdeaProjects/java-thinking/target/classes";

    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {

        FileClassLoader fileClassLoader = new FileClassLoader(rootDir);
        File file = new File(rootDir);
        URL url = file.toURL();

        Class<?> clazz1 = fileClassLoader.loadClass("com.majm.common.Person");
        FileURLClassLoader loader = new FileURLClassLoader(new URL[]{url});
        Class<?> clazz2 = loader.loadClass("com.majm.common.Person");

        System.out.println(clazz1.hashCode());
        System.out.println(clazz2.hashCode());

        loadClassTest();

    }


    /**
     * result:
     * 985655350   # AppClassLoader
     * 985655350   # AppClassLoader
     * 2096057945  # FileClassLoader1
     * 1830712962  # FileClassLoader2
     *
     * clazz1 == clazz2
     *
     *
     * @throws ClassNotFoundException
     */
    public static void loadClassTest() throws ClassNotFoundException {
        FileClassLoader fileClassLoader1 = new FileClassLoader(rootDir);
        FileClassLoader fileClassLoader2 = new FileClassLoader(rootDir);

        Class<?> clazz1 = fileClassLoader1.loadClass("com.majm.common.Person");
        Class<?> clazz2 = fileClassLoader2.loadClass("com.majm.common.Person");

        System.out.println(clazz1.hashCode());
        System.out.println(clazz2.hashCode());

        Class<?> clazz3 = fileClassLoader1.findClass("com.majm.common.Person");
        Class<?> clazz4 = fileClassLoader2.findClass("com.majm.common.Person");

        System.out.println(clazz3.hashCode());
        System.out.println(clazz4.hashCode());
    }
}

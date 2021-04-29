package com.majm.jvm;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author majunmin
 * @description
 * @datetime 2021/3/7 3:47 下午
 * @since
 */
public class ClassLoaderTest {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {

                try {
                    String filename = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(filename);
                    if (is == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException("name");
                }
            }
        };

        Object obj = myLoader.loadClass("com.majm.jvm.ClassLoaderTest").newInstance();
        System.out.println(obj.getClass());
        System.out.println(obj instanceof com.majm.jvm.ClassLoaderTest); // false
        System.out.println(new ClassLoaderTest() instanceof com.majm.jvm.ClassLoaderTest); // true

    }
}

package com.majm.clazz.loader.custom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * 自定义网络类加载器 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-01 19:23
 * @since
 */
public class NetClassLoader extends ClassLoader {

    /**
     * class 文件 url
     */
    private String url;

    public NetClassLoader(String url) {
        this.url = url;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        byte[] classData = loadClassDataFromNet(name);
        if (Objects.isNull(classData)) {
            throw new ClassNotFoundException();
        }
        return defineClass(name, classData, 0, classData.length);
    }

    private byte[] loadClassDataFromNet(String className) {
        String path = classNameToPath(className);
        byte[] result = null;
        try {
            byte[] buff = new byte[4096];
            URL url = new URL(path);
            InputStream inputStream = url.openStream();
            int limit = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((limit = inputStream.read(buff)) != -1) {
                baos.write(buff, 0, limit);
            }
            result = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String classNameToPath(String className) {
        return url + className.replace(".", "/") + ".class";
    }
}

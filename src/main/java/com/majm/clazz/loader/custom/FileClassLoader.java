package com.majm.clazz.loader.custom;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Objects;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-01 15:46
 * @since
 */
public class FileClassLoader extends ClassLoader {

    private String rootDir;

    public FileClassLoader(String rootDir) {
        this.rootDir = rootDir;
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = getClassData(name);
        if (Objects.isNull(classData)){
            throw new ClassNotFoundException();
        }
        return defineClass(name, classData, 0, classData.length);
    }

    public byte[] getClassData(String className) {
        String classFullPath = classNameToPath(className);

        byte[] buff = new byte[4096];

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             FileInputStream inputStream = new FileInputStream(classFullPath)) {
            int limit = 0;
            while ((limit = inputStream.read(buff)) != -1) {
                baos.write(buff, 0, limit);
            }
            return baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String classNameToPath(String className) {
        String filePath = className.replace(".", "/");
        String classFullPath = rootDir + "/" + filePath + ".class";
        return classFullPath;
    }
}

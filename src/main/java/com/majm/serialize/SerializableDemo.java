package com.majm.serialize;

import com.majm.common.Person;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-03-30 23:55
 * @since
 */
public class SerializableDemo implements Serializable {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // 序列化到文件
        // 如果序列化对象没有实现  Serializable 接口,会抛出异常 NotSerializableException
        File file = new File("./a.txt");
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
        outputStream.writeObject(new Person());
        outputStream.close();

        // 反序列化
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
        Person o = (Person)inputStream.readObject();
        inputStream.close();
    }
}

package com.majm.io;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamDemo {

    public static void main(String[] args) {

        ;
        try (InputStream inputStream = InputStreamDemo.class.getClassLoader().getResourceAsStream("a.txt")) {
            byte[] buffer = new byte[1024];

            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 512);

            int len = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1) {
//            inputStream.read(buffer, 0, len);
                System.out.println(new String(buffer, 0, len));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

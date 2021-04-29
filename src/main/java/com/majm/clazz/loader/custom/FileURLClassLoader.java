package com.majm.clazz.loader.custom;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * FileURLClsssLoader  </br>
 * 继承 URLClassLoader 可以极大的简化代码
 * 相比  FileClassLoader
 *
 * @author majunmin
 * @description
 * @datetime 2021-04-01 16:28
 * @since
 */
public class FileURLClassLoader extends URLClassLoader {


    public FileURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public FileURLClassLoader(URL[] urls) {
        super(urls);
    }

    public FileURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

}

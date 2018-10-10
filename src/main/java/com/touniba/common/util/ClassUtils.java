package com.touniba.common.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Magoo on 2016/7/21.
 */
public class ClassUtils {

    private static ClassLoader DEFAULT_CLASS_LOADER;

    /**
     * Get the File Resources
     *
     * @param name
     * @return
     */
    public static List<URL> getFileResources(String name) {
        List<URL> list = new ArrayList<>();
        getResources(name).forEach(url -> {
            if (url.getProtocol().equalsIgnoreCase("file")) {
                list.add(url);
            }
        });
        return list;
    }

    /**
     * Get the file resource.
     *
     * @param name
     * @return
     */
    public static URL getFileResource(String name) {
        for (URL url : getResources(name)) {
            if (url.getProtocol().equalsIgnoreCase("file")) {
                return url;
            }
        }
        return null;
    }

    /**
     * Get the Jar Resources
     *
     * @param name
     * @return
     */
    public static List<URL> getJarResources(String name) {
        List<URL> list = new ArrayList<>();
        getResources(name).forEach(url -> {
            if (url.getProtocol().equalsIgnoreCase("jar")) {
                list.add(url);
            }
        });
        return list;
    }

    /**
     * Get the resources
     *
     * @param name
     * @return
     */
    public static List<URL> getResources(String name) {
        List<URL> list = new ArrayList<>();
        Enumeration<URL> enumeration = null;
        try {
            enumeration = defaultClassLoader().getResources(name);
        } catch (IOException e) {
            // do nothing
        }
        if (null != enumeration) {
            while (enumeration.hasMoreElements()) {
                URL u = enumeration.nextElement();
                if (null != u) {
                    list.add(u);
                }
            }
        }
        return list;
    }

    /**
     * Get the default ClassLoader
     *
     * @return
     */
    public static synchronized ClassLoader defaultClassLoader() {
        if (null == DEFAULT_CLASS_LOADER) {
            DEFAULT_CLASS_LOADER = Thread.currentThread().getContextClassLoader();
            if (null == DEFAULT_CLASS_LOADER) {
                DEFAULT_CLASS_LOADER = ClassUtils.class.getClassLoader();
            }
        }
        assert null != DEFAULT_CLASS_LOADER;
        return DEFAULT_CLASS_LOADER;
    }


}

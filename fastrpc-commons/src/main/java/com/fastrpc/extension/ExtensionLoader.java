package com.fastrpc.extension;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *  效果: 通过getExtenison可以接口的实现类 接口上必须有SPI注解
 *  指定接口实现类有两种方法
 *  1.getExtension方法中传过参获取实现类
 *  2.如果getExtension没有传参,则从SPI获取默认实现类
 *  下面对应了两个重载的放啊
 *  每一个接口都对应一个ExtensionLoader
 * @author: @zyz
 */
@Slf4j
public class ExtensionLoader<T> {
    private static final String SERVICES_DIRECTORY = "META-INF/services/";
    // 这俩key为Class
    //为每一个接口保存一个加载类 保存ExtensionLoader
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<Class<?>, ExtensionLoader<?>>();
    // 保存所有接口的所有实现类 class 对应的实例对象 保存具体类的对象   未实例的实现类的class类: 实例话的实现类  根据Class获得对象实例
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<Class<?>, Object>();

    private final Class<?> type;

 // 实例缓存，属于某个接口对应的ExtensionLoad实例的缓存
    // 这俩key为String
    //缓存接口配置文件中所有实现类的对应的Class 这些Class都是通过loadClass加载，并没有链接和初始化 name:实现类为链接的的class
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<Map<String, Class<?>>>();
    // 保存name 对应 Class实例化过的类 这个接口所有实现类的实例
    // cachedInstances name: holder  该Name指定接口对应实现类的实例化对象 放到Holder中 根据Name获得对象实例
    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<String, Holder<Object>>();
    private volatile String cachedDefaultName;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    /**
     * 每一个接口维持一个类构造器
     * @param type
     * @param <T>
     * @return
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type)
    {
        if (type==null)
            throw new IllegalArgumentException("Extension type == null");
        if (!type.isInterface())
            throw new IllegalArgumentException("Extenison type ("+type+") is not interface!");
        if (!withExtensionAnnotation(type))
            throw new IllegalArgumentException("Extension type(" + type +
                    ") is not extension, because WITHOUT @" + SPI.class.getSimpleName() + " Annotation!");
        ExtensionLoader<T> loader= (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader==null)
        {
            EXTENSION_LOADERS.putIfAbsent(type,new ExtensionLoader<T>(type));
            loader= (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;

    }

    /**
     * 判断接口是否是SPI注解
     * @param type
     * @return
     */
    public static <T> boolean withExtensionAnnotation(Class<T> type)
    {
        return  (type.isAnnotationPresent(SPI.class));
    }

    /**
     * SPI指定默认实现类接口
     */
    public T getExtension()
    {
        return getExtension("default");
    }
    /**
     *  根据给的名字获取具体的实现类
     * @param name
     * @return
     */
    public T getExtension(String name)
    {
        if (name==null||name.length()==0)
            throw new IllegalArgumentException("Extension name == null");
        //没有指定具体实现类则从SPI中获取指定的实现类
        if("default".equalsIgnoreCase(name))
        {
            return getDefaultExtension();
        }
        // cachedInstances name: holder  该Name指定接口对应实现类的实例化对象 放到Holder中
        Holder<Object> holder=cachedInstances.get(name);
        if(holder == null)
        {
            cachedInstances.putIfAbsent(name,new Holder<Object>());
            holder=cachedInstances.get(name);
        }

        Object instance=holder.get();
        // double check
        if (instance == null)
        {
            synchronized (holder)
            {
                instance=holder.get();
                if (instance==null)
                {
                    instance=createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;

    }

    private T createExtension(String name) {

        Class<?> clazz=getExtensionClasses().get(name);
        if (clazz==null)
        {
            throw new IllegalArgumentException("No such extension "+name);
        }
        // 注入
        T instance= (T) EXTENSION_INSTANCES.get(clazz);
        if (instance==null){
            try {
                // 这样写二次获取的逻辑是 不使用锁来防止多线程重新创建，提高效率，就算对各线程同时创建对象，最后从缓存Map中取到的对象一定是最后保存的，所以重新从缓存中获取了 不加锁提高效率
                EXTENSION_INSTANCES.putIfAbsent(clazz,clazz.newInstance());
                instance= (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
             log.error(e.getMessage());

            }
        }
        return instance;
    }

    /**
     * 加载这个接口所有实现类的Class
     * @return
     */
    private Map<String, Class<?>> getExtensionClasses() {
        Map<String,Class<?>> classes=cachedClasses.get();
        if (classes==null)
        {
            synchronized (cachedClasses)
            {
                classes=cachedClasses.get();
                if (classes==null)
                {
                    // 加载该接口配置的所有扩展类
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    /**
     * 从 .extensions文件中加载实现类
     * @return
     */
    private Map<String, Class<?>> loadExtensionClasses() {
        Map<String,Class<?>> extensionClasses=new HashMap<>();
        loadDirectory(extensionClasses,SERVICES_DIRECTORY);
        return extensionClasses;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir) {
        String fileName= dir+ type.getName();
        Enumeration<URL> urls;
        ClassLoader classLoader=findClassLoader();
        try {
        if (classLoader!=null)
        {
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    java.net.URL resourceURL = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceURL);
                }
            }
        }
        } catch (IOException e) {
            log.error("Exception when load extension class(interface: " +
                    type + ", description file: " + fileName + ").", e);
        }
    }
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, java.net.URL resourceURL) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), "utf-8"));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    final int ci = line.indexOf('#');
                    if (ci >= 0) line = line.substring(0, ci);
                    line = line.trim();
                    if (line.length() > 0) {
                        try {
                            String name = null;
                            int i = line.indexOf('=');
                            if (i > 0) {
                                name = line.substring(0, i).trim();
                                line = line.substring(i + 1).trim();
                            }
                            if (line.length() > 0&&line.length()>0) {
                                loadClass(extensionClasses, classLoader.loadClass(line), name);
                            }
                        } catch (Throwable t) {
                           throw  new IllegalStateException("Failed to load extension class(interface: " + type + ", class line: " + line + ") in " + resourceURL + ", cause: " + t.getMessage(), t);

                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (Throwable t) {
            log.error("Exception when load extension class(interface: " +
                    type + ", class file: " + resourceURL + ") in " + resourceURL, t);
        }
    }

    /**
     * 缓存type接口的所有实现类  如 gzip（key）=全类名(value)
     * @param extensionClasses
     * @param clazz
     * @param name
     */
    private void loadClass(Map<String, Class<?>> extensionClasses, Class<?> clazz, String name) {
        if (!type.isAssignableFrom(clazz))
        {
            throw new IllegalStateException("Error when load extension class(interface: "+type+
                    ", class line: "+ clazz.getName()+"), class "+
                    clazz.getName()+ "is not subtype of interface");
        }

         extensionClasses.put(name,clazz);


    }

    private ClassLoader findClassLoader() {
        return ExtensionLoader.class.getClassLoader();
    }

    /**
     * 加载SPI注解中指定的实现类
     * @return
     */
    private T getDefaultExtension() {

        if (cachedDefaultName==null)
        {
            SPI defaultAnnotation=type.getAnnotation(SPI.class);
            if (defaultAnnotation !=null)
            {
                String value=defaultAnnotation.value();
                if((value=value.trim()).length()>0)
                {
                    cachedDefaultName=value;

                }
            }else{
                throw new IllegalArgumentException("The implementation class must be specified");
            }
        }
         return getExtension(cachedDefaultName);
    }
}

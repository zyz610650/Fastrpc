package com.fastrpc.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author: @zyz
 */
@Slf4j
public class ScannerUtils {

    /**
     * 缓存 key注解标记的所有接口
     */
    static final Map<Class<?>, Set<Class<?>>> cachedAnnotation=new HashMap<>();
    /**
     * 缓存所有已经扫描过的包 保存格式为 注解名&包名
     */
    static final Set<String> cachedDirName=new HashSet<>() ;

    /**
     * 标记遇到File时是否循环扫描
     */
    static final boolean recursive = true;
    /**
     *  加载注解标记过的类或接口
     * @param annotationClazz     指定需要获得的注解
     * @param dir       dir为需要扫描的包名
     * @return
     */
    public static void loadClassOrInterface(Class<?> annotationClazz,String dir) {
        String name=annotationClazz.getSimpleName() + "&" + dir;
        if (cachedDirName.contains(name)) return;

        cachedDirName.add(name);
        String packageDirName = dir.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(annotationClazz,dir, filePath);
                }
            }
        } catch (IOException e) {
            log.error("load annotation fail :" +e);
            cachedDirName.remove(name);
            throw new IllegalStateException(e);
        }

    }

    /**
     * 从文件下获去包下的所有Class
     * @param annotationClazz
     * @param packageName
     * @param packagePath
     */
    public static void findAndAddClassesInPackageByFile(Class<?> annotationClazz,String packageName,String packagePath)
    {
        File dir=new File(packagePath);
        if (!dir.exists()||!dir.isDirectory())
        {
            log.error("path does not exist ："+packagePath);
            throw new IllegalArgumentException("path does not exist ："+packagePath);
        }
        File[] dirFiles=dir.listFiles(   file-> (recursive&&file.isDirectory())|| (file.getName().endsWith(".class")));
        try {
         Set<Class<?>> cachedSet=new HashSet<>();
        for (File file:dirFiles)
        {
            if (file.isDirectory())
            {
                findAndAddClassesInPackageByFile(annotationClazz,packageName+"."+file.getName(),file.getAbsolutePath());
            }else{
                // 若为.class文件
                String className=file.getName().substring(0,file.getName().length()-6);

                Class<?> clazz=Thread.currentThread().getContextClassLoader().loadClass(packageName+"."+className);

                if (clazz.isAnnotationPresent((Class<? extends Annotation>) annotationClazz))
                {
                    cachedSet.add(clazz);
                }
            }
            }
            Set<Class<?>> classes = cachedAnnotation.computeIfAbsent(annotationClazz, aClass -> new HashSet<Class<?>>());
            classes.addAll(cachedSet);
            cachedAnnotation.put(annotationClazz,classes);
        } catch (ClassNotFoundException e) {

            log.error(e.getMessage());
            throw new IllegalArgumentException("The Class file  could not be found ",e);
        }
    }

    /**
     * 获取所有标记annotationClazz注解的类或接口
     * @param annotationClazz
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getClass(Class<?> annotationClazz,String packageName)
    {
        if (!cachedDirName.contains(annotationClazz.getName()+"&"+packageName))
            loadClassOrInterface(annotationClazz,packageName);
        return cachedAnnotation.get(annotationClazz);

    }

    /**
     * 若提前加载过就无需指定扫描包的路径
     * @param annotationClazz
     * @return
     */
    public static Set<Class<?>> getClass(Class<?> annotationClazz)
    {
        return cachedAnnotation.get(annotationClazz);
    }
     public static void main(String[] args) {
         Set<Class<?>> aClass = getClass(Controller.class, "com.fastrpc.service");
         if (aClass!=null)
         for (Class<?> cl:aClass) {
             System.out.println(cl.getName());
         }
     }
}

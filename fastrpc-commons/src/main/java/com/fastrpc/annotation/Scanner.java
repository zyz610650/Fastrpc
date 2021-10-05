package com.fastrpc.annotation;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author: @zyz
 */
@Slf4j
public class Scanner {
    /**
     * 从包package中获取所有的Class
     *
     * @param packageName
     * @return
     */
    public Set<Class<?>> getClasses(String packageName) throws Exception{

        // 第一个class类的集合
        //List<Class<?>> classes = new ArrayList<Class<?>>();
        Set<Class<?>> classes = new HashSet<>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中，以下俩种方法都可以
                    //网上的第一种方法，
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                    //网上的第二种方法
                    //addClass(classes,filePath,packageName);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    public   void addClass(Set<Class<?>> classes, String filePath, String packageName) throws Exception{
        File[] files=new File(filePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile()&&file.getName().endsWith(".class"))||file.isDirectory();
            }
        });
        for(File file:files){
            String fileName=file.getName();
            if(file.isFile()){
                String classsName=fileName.substring(0,fileName.lastIndexOf("."));
                if(! StringUtils.isBlank(packageName)){
                    classsName=packageName+"."+classsName;
                }
                doAddClass(classes,classsName);
            }

        }
    }

    public   void doAddClass(Set<Class<?>> classes, final String classsName) throws Exception{
        ClassLoader classLoader=new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return super.loadClass(name);
            }
        };
        //Class<?> cls= ClassLoader.loadClass(classsName);
        classes.add(classLoader.loadClass(classsName));
    }

    //找也用了Controller注解的类
    private Set<Class<?>> controllers;

    public Set<Class<?>> getControllers() throws Exception{
        if (controllers == null) {
            controllers = new HashSet<>();

            Set<Class<?>> clsList = getClasses("com.fastrpc.service");
            if (clsList != null && clsList.size() > 0) {
                for (Class<?> cls : clsList) {
                    if (cls.getAnnotation(Controller.class) != null) {
                        Map<Class<?>, Object> map = new HashMap<>();
                        controllers.add(cls);
                    }
                }
            }
        }
        return controllers;
    }
    public void getMapping() throws Exception{
        for (Class<?> cls : getControllers()) {
            //***********
            System.out.println(cls);

            Method[] methods = cls.getMethods();
            for (Method method : methods) {
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                if (annotation != null) {
                    String value = annotation.value();//找到RequestMapping的注入value值
                    if (value.equals("/about")) {//判断是不是/about，是的话，就调用about(args)方法
                        method.invoke(cls.newInstance(), "args"); //第二个参数是方法里的参数
                    }
                }
            }
        }
    }
    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath, final boolean recursive, Set<Class<?>> classes) {
        System.out.println(packagePath);
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {

            // 如果是目录 则继续扫描
            if (file.isDirectory()) {

                findAndAddClassesInPackageByFile(packageName + "."
                                + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
//                System.out.println(file.getName());
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);

                Class<?> clazz = null;
                try {
                    clazz = Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (clazz.isInterface())
                {
                    System.out.println(className);
                }

                try {
                    // 添加到集合中去
                    //classes.add(Class.forName(packageName + '.' + className));
                    //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));

                } catch (ClassNotFoundException e) {
                   log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args)throws Exception {
        new Scanner().getMapping();

    }
}
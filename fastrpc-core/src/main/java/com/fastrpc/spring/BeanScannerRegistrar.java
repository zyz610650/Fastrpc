package com.fastrpc.spring;

import com.fastrpc.annotation.RpcScanner;
import com.fastrpc.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author: @zyz
 */
@Slf4j
public class BeanScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final String SPRING_BEAN_BASE_PACKAGE="com.fastrpc";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME="basePackage";
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        log.info("Scan All classes");
        // 这个类是通过@RpcScan导入的，所以AnnotationMetadata里面就包含了这个注解
        AnnotationAttributes rpcScanAnnotationAttributes=AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RpcScanner.class.getName()));
        String[] rpcScanBasePackages=new String[0];
        if (rpcScanAnnotationAttributes!=null)
        {
            //获得RpcScanner注解中的basePackage属性值
            rpcScanBasePackages=rpcScanAnnotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if(rpcScanBasePackages.length == 0)
        {
            //读取RpcScanner注解标注的类的包名
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName()};
        }
        //添加RpcService为Spring需要扫描的注解
        BeanDefinitionScanner rpcServiceScanner=new BeanDefinitionScanner(registry, RpcService.class);
        BeanDefinitionScanner springBeanScanner=new BeanDefinitionScanner(registry, Component.class);
        if(resourceLoader!=null)
        {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }

        // 扫描jar包里的注解
        int springBeanAmount = springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("springBeanScanner扫描的数量 [{}]", springBeanAmount);
        int rpcServiceCount = rpcServiceScanner.scan(rpcScanBasePackages);
        log.info("rpcServiceScanner扫描的数量 [{}]", rpcServiceCount);
    }
}

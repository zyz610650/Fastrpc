package com.fastrpc.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * @author: @zyz
 */
public class BeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    public BeanDefinitionScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annotype) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annotype));
    }

    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }
}

package com.fastrpc.annontation.ioc;

/**
 * @author: @zyz
 */
public interface Ioc {

    public void addBean(Class<?>... clazzs);

    <T> T getBean(Object beanName);
}

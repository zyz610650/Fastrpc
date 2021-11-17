package com.fastrpc;


import com.beanbox.context.suppport.ClassPathXmlApplicationContext;

import lombok.extern.slf4j.Slf4j;


/**
 * @author: @zyz
 */
@Slf4j
public class ApplicationServerMain {

    public static void main(String[] args) throws InterruptedException {

        ClassPathXmlApplicationContext applicationContext=new ClassPathXmlApplicationContext ("classpath:beanbox.xml");
        Thread.sleep (9000000);

    }
}

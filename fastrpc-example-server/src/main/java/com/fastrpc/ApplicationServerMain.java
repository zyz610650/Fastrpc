package com.fastrpc;


import com.beanbox.context.suppport.ClassPathXmlApplicationContext;
import com.fastrpc.annotation.RpcScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author: @zyz
 */
@Slf4j
public class ApplicationServerMain {

    public static void main(String[] args) throws InterruptedException {

        ClassPathXmlApplicationContext applicationContext=new ClassPathXmlApplicationContext ("classpath:beanbox.xml");
        Thread.sleep (10000);

    }
}

package com.fastrpc;

import com.beanbox.context.suppport.ClassPathXmlApplicationContext;
import com.fastrpc.annotation.RpcScanner;
import com.fastrpc.controller.HelloController;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author: @zyz
 */
public class ApplicationClintMain {
   public static void main(String[] args) throws InterruptedException {
      ClassPathXmlApplicationContext applicationContext=new ClassPathXmlApplicationContext ("classpath:beanbox.xml");
      HelloController helloController= (HelloController) applicationContext.getBean("helloController");
      helloController.sayHi();
   }
}
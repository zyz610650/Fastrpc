package com.fastrpc;

import com.beanbox.context.suppport.ClassPathXmlApplicationContext;
import com.fastrpc.annotation.RpcScanner;
import com.fastrpc.controller.HelloController;


/**
 * @author: @zyz
 */
public class ApplicationClintMain {
   public static void main(String[] args) throws InterruptedException {
      ClassPathXmlApplicationContext applicationContext=new ClassPathXmlApplicationContext ("classpath:beanbox.xml");
      HelloController helloController= (HelloController) applicationContext.getBean("helloController");
      helloController.sayHi();
      Thread.sleep(2000);
      helloController.sayHi();
//
//      HelloController helloController1= (HelloController) applicationContext.getBean("helloController");
//      helloController1.sayHi();
   }
}

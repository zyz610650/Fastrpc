package com.fastrpc;

import com.fastrpc.annotation.RpcScanner;
import com.fastrpc.controller.HelloController;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author: @zyz
 */
@RpcScanner(basePackage = "com.fastrpc")
public class ApplicationMain {
   public static void main(String[] args) {
      ConfigurableApplicationContext ctx=new AnnotationConfigApplicationContext(ApplicationMain.class);
      HelloController helloController= (HelloController) ctx.getBean("helloController");
      helloController.sayHi();
   }

}

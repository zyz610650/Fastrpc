package com.fastrpc.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author: @zyz
 */

@Aspect
@Component
public class LogAdvice {

	@Pointcut("@annotation(com.fastrpc.annotation.RpcService)")
	private void logAdvicePointcut(){};

	@Before ("logAdvicePointcut()")
	public void logAdvice(){

		System.out.println ("RpcService注解的advice触发");

	}
}

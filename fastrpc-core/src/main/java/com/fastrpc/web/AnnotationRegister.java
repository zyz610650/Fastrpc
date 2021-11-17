package com.fastrpc.web;

import com.beanbox.beans.annotation.register.AbstractDefaultAnnotationRegistryFactory;
import com.beanbox.beans.annotation.register.AnnotationType;
import com.fastrpc.annotation.RpcService;

/**
 * @author: @zyz
 */
public class AnnotationRegister extends AbstractDefaultAnnotationRegistryFactory {



	@Override
	public void registAnnotations () {
		super.addAnnotation (new AnnotationType (RpcService.class));
	}
}

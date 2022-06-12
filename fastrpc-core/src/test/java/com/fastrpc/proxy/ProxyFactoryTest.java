package com.fastrpc.proxy;

import com.alibaba.fastjson.JSON;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ProxyFactoryTest {

    @Test
    void doGenericServiceParameters() {

        String json="{\"name\":\"zyz\"}";
        String listJson="[\"1\",\"1\",\"1\"]";
        String objArrayJson="[\"1\",\"1\",\"1\"]";

        List<String> res= Lists.newArrayList(json,listJson,objArrayJson);
        Class[] classes=new Class[3];
        classes[0]= UserTest.class;
        classes[1]=List.class;
        classes[2]=Object[].class;
        Object[] objects = ProxyFactory.doGenericServiceParameters(res, classes);
        System.out.println(Arrays.toString(objects));
       assert objects!=null||objects.length!=0;
    }
}
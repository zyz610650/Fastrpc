package com.fastrpc.http;

import com.alibaba.fastjson.JSON;
import com.fastrpc.proxy.UserTest;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenericServiceTest {

    @Test
    void $invoke() {
        // 设置方接口名
        GenericService genericService=new GenericService();
        genericService.setServiceName("com.fastrpc.proxy.UserTest");
        // 可省
        genericService.setGroup("");
        genericService.setVersion("");
        String param = JSON.toJSONString(new UserTest("zyz"));
        // 设置参数类型和参数值
        ArrayList<String> paramType = Lists.newArrayList(UserTest.class.toGenericString());
        ArrayList<String> params = Lists.newArrayList(param);
        assert genericService.$invoke("$invoke",paramType ,params)!=null;
    }
}
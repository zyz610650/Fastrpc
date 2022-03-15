package com.fastrpc.extension;

import java.util.Arrays;

@ConditionOnClass(name = "mzd")
public class Test {
    public static void main(String[] args) {
        Test test=new Test();
        Class<? extends Test> clazz = test.getClass();
        ConditionOnClass conditionOnClass=clazz.getAnnotation(ConditionOnClass.class);
        System.out.println(conditionOnClass.name());

        System.out.println();
        System.out.println(Arrays.toString(clazz.getAnnotations()));
    }
}

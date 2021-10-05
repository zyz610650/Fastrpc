package com.fastrpc.service;

/**
 * @author yvioo
 */
public class StudentServiceImpl implements StudentService{
    @Override
    public String say(String name) {
        return "hello "+name+" from StudentService";
    }
}

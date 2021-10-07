package com.fastrpc.service;

/**
 * @author yvioo
 */
public class UserServiceImpl implements UserService{
    @Override
    public String say(String name) {
        return "hello "+name+" from UserService";
    }
}

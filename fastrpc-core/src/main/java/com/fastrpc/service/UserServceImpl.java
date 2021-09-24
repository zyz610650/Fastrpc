package com.fastrpc.service;

/**
 * @author yvioo
 */
public class UserServceImpl implements UserService{
    @Override
    public String say(String name) {
        return "hello "+name;
    }
}

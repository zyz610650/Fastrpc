package com.fastrpc.service;

import com.fastrpc.annotation.Controller;

/**
 * @author yvioo
 */
@Controller
public class UserServiceImpl implements UserService{
    @Override
    public String say(String name) {
        return "hello "+name+" from UserService";
    }
}

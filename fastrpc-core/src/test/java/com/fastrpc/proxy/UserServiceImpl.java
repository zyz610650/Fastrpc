package com.fastrpc.proxy;

public class UserServiceImpl implements UserService{
    @Override
    public void hello(UserTest userTest) {
        System.out.println("hello : "+userTest.getName());
    }
}

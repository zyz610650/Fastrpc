package com.fastrpc.service;

/**
 * @author yvioo
 */
public class InfoServiceImpl implements InfoService{
    @Override
    public String say(String name) {
        return "hello "+name+" from InfoService";
    }
}

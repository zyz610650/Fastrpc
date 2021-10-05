package com.fastrpc.service;


import com.fastrpc.annotation.Controller;

/**
 * @author yvioo
 */
@Controller
public interface UserService {
    public  String say (String name);
}

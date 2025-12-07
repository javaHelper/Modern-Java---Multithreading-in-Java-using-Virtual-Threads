package org.example.service;

import org.example.util.CommonUtil;

public class HelloWorldService {

    public String hello(){
        CommonUtil.sleep(1000);
        return "Hello";
    }

    public String world(){
        CommonUtil.sleep(1000);
        return " World";
    }
}

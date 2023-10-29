package com.ryf.app.service.impl;

import com.ryf.app.service.HelloService;
import com.ryf.app.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/29
 */
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String username) {
        String concat = StringUtils.concat("ryf learn", "byte-buddy");
        return "hello " + username + ":" + concat;
    }
}

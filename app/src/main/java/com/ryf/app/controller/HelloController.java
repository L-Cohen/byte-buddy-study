package com.ryf.app.controller;

import com.ryf.app.service.HelloService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/29
 */
@RestController
@Controller
public class HelloController {
    @Resource
    private HelloService helloService;

    @GetMapping("/hello")
    public String hello(@RequestParam("username") String username) {
        return helloService.hello(username);
    }
}

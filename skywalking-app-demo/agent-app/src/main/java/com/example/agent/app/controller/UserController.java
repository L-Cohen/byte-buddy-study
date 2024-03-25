package com.example.agent.app.controller;

import com.example.agent.app.entity.User;
import com.example.agent.app.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/30
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public List<User> selectUserList(String name) {
        return userService.selectUserList(name);
    }
}

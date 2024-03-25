package com.example.agent.app.service;

import com.example.agent.app.entity.User;

import java.util.List;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/30
 */
public interface UserService {
    List<User> selectUserList(String name);
}

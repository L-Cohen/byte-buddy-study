package com.example.agent.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/30
 */
@TableName("t_user")
@Data
public class User {
    private Integer id;
    private String username;
    private String password;
}

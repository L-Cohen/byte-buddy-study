package com.ryf.bytebuddy;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/26
 */
public interface UserDao {

    void updateUser(int age);

    int getUserAge();
}

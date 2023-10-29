package com.ryf.bytebuddy;

import java.util.UUID;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/26
 */
public class UserManager1 {
    public static String selectUserName(long userId) {
        return "user:id:" + userId + UUID.randomUUID();
    }

    public void print() {
        System.out.println("1");
    }

    public int selectAge() {
        return 23;
    }
}

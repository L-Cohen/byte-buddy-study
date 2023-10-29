package com.ryf.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/26
 */
public class UserManager3 {

    /**
     * 被@RuntimeType标注方法即被拦截方法，此时方法签名可以与被拦截方法不一致
     * ，@this 表示被拦截的目标对象，static时无效
     */
    @RuntimeType
    public Object aaa(
            // 被拦截的目标对象
            @This Object targetObj,
            // 被拦截的目标对象的方法，只有实例方法，和静态方法
            @Origin Method method,
            // 目标方法的参数
            @AllArguments Object[] args,
            // 被拦截的目标对象
            @Super Object superObject,
            // 用于调用目标对象的方法
            @SuperCall Callable<?> call) {
        System.out.println("targetObj is :" + targetObj);
        System.out.println("method is :" + method.getName());
        System.out.println("super object is :" + superObject);
        System.out.println("args is :" + Arrays.toString(args));
        Object called = null;
        try {
            // 调用目标方法
            called = call.call();
            // 为什么不可以反射调用? 因为反射调用会继续被委托，导致递归调用
            // method.invoke(targetObj, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return called;
    }

    public void print() {
        System.out.println("1");
    }

    public int selectAge() {
        return 23;
    }
}

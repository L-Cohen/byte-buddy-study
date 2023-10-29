package com.ryf.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/26
 */
public class UserManager4 {

    /**
     * 被@RuntimeType标注方法即被拦截方法，此时方法签名可以与被拦截方法不一致
     * ，@this 表示被拦截的目标对象，static时无效
     */
//    @RuntimeType
//    public Object aaa(
//
//            // 目标方法的参数
//            @AllArguments Object[] args,
//            // 用于调用目标对象的方法
//            @Morph MyCallable call) {
//        Object called = null;
//        try {
//            // 调用目标方法
//            if (Objects.nonNull(args) && args.length > 0) {
//                args[0] = Long.parseLong(args[0].toString()) + 1;
//                called = call.call(args);
//            }
//            // 为什么不可以反射调用? 因为反射调用会继续被委托，导致递归调用
//            // method.invoke(targetObj, args);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return called;
//    }

    @RuntimeType
    public void aaa(@This Object targetObj) {
        System.out.println(targetObj + "被实例化了");
    }
}

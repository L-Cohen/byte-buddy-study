package com.ryf.bytebuddy;

import cn.hutool.core.io.FileUtil;
import net.bytebuddy.implementation.bind.annotation.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/26
 */
public class UserManager5 {

    /**
     * 被@RuntimeType标注方法即被拦截方法，此时方法签名可以与被拦截方法不一致
     * ，@this 表示被拦截的目标对象，static时无效
     */
    @RuntimeType
    public Object intercept(
            //
            @Origin Class<?> clazz,
            @Origin Method method,
            // 目标方法的参数
            @AllArguments Object[] args,
            // 用于调用目标对象的方法，配合rebase（静态实例都可以）或subclass增强实例方法时使用
            @SuperCall Callable<?> call) {
        Object called = null;
        System.out.println("target clazz: " + clazz.getName());
        System.out.println("target method: " + method.getName());
        System.out.println("target args: " + Arrays.toString(args));
        try {
            // 调用目标方法
            if (Objects.nonNull(args) && args.length > 0) {
                File file = (File) args[0];
                System.out.println("file: " + file.getName());
                args[0] = new File("E:\\test\\pdf\\test_chart2.png");
                System.out.println(FileUtil.size(new File("E:\\test\\pdf\\test_chart2.png")));
                called = call.call();
            }
            // 为什么不可以反射调用? 因为反射调用会继续被委托，导致递归调用
            // method.invoke(targetObj, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return called;
    }

}

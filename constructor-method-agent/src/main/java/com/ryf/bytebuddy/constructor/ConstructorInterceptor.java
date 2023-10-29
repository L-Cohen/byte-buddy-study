package com.ryf.bytebuddy.constructor;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.util.Arrays;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/29
 */
@Slf4j
public class ConstructorInterceptor {

    @RuntimeType
    public void intercept(@This Object obj, @AllArguments Object[] args) {
        log.info("constructor interceptor, obj: {}, args: {}", obj, Arrays.toString(args));
    }
}

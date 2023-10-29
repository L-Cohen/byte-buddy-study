package com.ryf.bytebuddy;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/29
 */
@Slf4j
public class SpringMvcInterceptor {

    @RuntimeType
    public Object intercept(@Origin Method method,
                            @This Object target,
                            @AllArguments Object[] args,
                            @SuperCall Callable<?> superCall) {
        log.info("before controller:{} method: {} args:{}", target.getClass().getName(), method.getName(), Arrays.toString(args));
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = superCall.call();
            log.info("result call:{}", result);
        } catch (Exception e) {
            log.error("controller exception", e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("{}.{} cost {} ms", target.getClass().getName(), method.getName(), end - start);
        }
        return result;
    }
}

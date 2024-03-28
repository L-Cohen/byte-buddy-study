package com.ryf.apm.agent.core.enhance;

import java.lang.reflect.Method;

/**
 * 静态方法的interceptor 必须实现该接口
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2024/3/26
 */
public interface StaticMethodsAroundInterceptor {
    /**
     * called before target method invocation.
     *
     * @param result change this result, if you want to truncate the method.
     */
    void beforeMethod(Class<?> clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes);

    /**
     * called after target method invocation. Even method's invocation triggers an exception.
     *
     * @param ret the method's original return value.
     * @return the method's actual return value.
     */
    Object afterMethod(Class<?> clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes, Object ret);

    /**
     * called when occur exception.
     *
     * @param t the exception occur.
     */
    void handleMethodException(Class<?> clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes,
                               Throwable t);
}

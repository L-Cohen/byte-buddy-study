package com.ryf.apm.agent.core.enhance;

import com.ryf.apm.agent.core.loader.InterceptorInstanceLoader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 静态方法拦截器
 * @date 2024/3/26
 */
@Slf4j
public class StaticMethodsInter {
    private StaticMethodsAroundInterceptor interceptor;

    public StaticMethodsInter(String methodsInterceptor, ClassLoader classLoader) {
        try {
            interceptor = InterceptorInstanceLoader.load(methodsInterceptor, classLoader);
        } catch (Exception e) {
            log.error("can't load interceptor:{}", methodsInterceptor);
        }
    }

    @RuntimeType
    public Object intercept(@Origin Class<?> clazz, @AllArguments Object[] allArguments, @Origin Method method,
                            @SuperCall Callable<?> zuper) throws Throwable {
        Object result = null;
        try {
            interceptor.beforeMethod(clazz, method, allArguments, method.getParameterTypes());
        } catch (Throwable e) {
            log.error("class[{}] before static method[{}] intercept failure", clazz, method.getName(), e);
        }
        try {
            result = zuper.call();
        } catch (Throwable t) {
            try {
                interceptor.handleMethodException(clazz, method, allArguments, method.getParameterTypes(), t);
            } catch (Throwable t2) {
                log.error("class[{}] handle static method[{}] exception failure", clazz, method.getName(), t2);
            }
            throw t;
        } finally {
            try {
                result = interceptor.afterMethod(clazz, method, allArguments, method.getParameterTypes(), result);
            } catch (Throwable e) {
                log.error("class[{}] after static method[{}] intercept failure", clazz, method.getName(), e);
            }
        }
        return result;
    }
}

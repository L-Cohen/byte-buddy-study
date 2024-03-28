package com.ryf.apm.agent.core.enhance;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2024/3/28
 */
@Slf4j
public class InstMethodsInter {
    private InstanceMethodsAroundInterceptor interceptor;
    private String methodsInterceptor;
    private ClassLoader classLoader;

    public InstMethodsInter(String methodsInterceptor, ClassLoader classLoader) {
    }

    @RuntimeType
    public Object intercept(@This Object obj,
                            @AllArguments Object[] allArguments,
                            @SuperCall Callable<?> zuper,
                            @Origin Method method) throws Throwable {
        EnhancedInstance enhancedInstance = (EnhancedInstance) obj;
        try {
            interceptor.beforeMethod(obj, method, allArguments, method.getParameterTypes());
        } catch (Throwable t) {
            log.error("class[{}] before method[{}] intercept failure", obj.getClass().getName(), method.getName(), t);
        }
        Object result = null;
        try {
            result = zuper.call();
        } catch (Throwable t) {
            try {
                interceptor.handleMethodException(obj, method, allArguments, method.getParameterTypes(), t);
            } catch (Throwable t2) {
                log.error("class[{}] handle method[{}] exception failure", obj.getClass(), method.getName(), t2);
            }
            throw t;
        } finally {
            try {
                result = interceptor.afterMethod(obj, method, allArguments, method.getParameterTypes(), result);
            } catch (Throwable t) {
                log.error("class[{}] after method[{}] intercept failure", obj.getClass().getName(), method.getName(), t);
            }
        }
        return result;
    }
}

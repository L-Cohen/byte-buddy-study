package com.ryf.apm.agent.core.enhance;

import com.ryf.apm.agent.core.loader.InterceptorInstanceLoader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 构造方法拦截器
 * @date 2024/3/28
 */
@Slf4j
public class ConstructorInter {
    private InstanceConstructorInterceptor interceptor;


    public ConstructorInter(String methodsInterceptor, ClassLoader classLoader) {
        try {
            interceptor = InterceptorInstanceLoader.load(methodsInterceptor, classLoader);
        } catch (Exception e) {
            log.error("can't load interceptor:{}", methodsInterceptor);
        }
    }

    @RuntimeType
    public void intercept(@This Object targetObject, @AllArguments Object[] allArguments) {
        try {
            EnhancedInstance enhancedInstance = (EnhancedInstance) targetObject;
            interceptor.onConstruct(enhancedInstance, allArguments);
        } catch (Throwable t) {
            log.error("ConstructorInter failure.", t);
        }
    }
}

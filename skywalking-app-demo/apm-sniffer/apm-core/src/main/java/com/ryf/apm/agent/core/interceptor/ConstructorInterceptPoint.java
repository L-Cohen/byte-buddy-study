package com.ryf.apm.agent.core.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/11/6
 */
public interface ConstructorInterceptPoint {

    /**
     * 拦截的构造方法
     */
    ElementMatcher<MethodDescription> getConstructorMatcher();

    /**
     * 获取被增强方法对应的interceptor名称，必须实现ConstructorMethodsAroundInterceptor
     */
    String getMethodsInterceptor();
}

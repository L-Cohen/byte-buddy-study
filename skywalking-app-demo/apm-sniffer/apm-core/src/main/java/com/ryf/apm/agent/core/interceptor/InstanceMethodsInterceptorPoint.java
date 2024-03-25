package com.ryf.apm.agent.core.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 实例方法的拦截点
 * @date 2023/11/6
 */
public interface InstanceMethodsInterceptorPoint {

    /**
     * 拦截的实例方法
     */
    ElementMatcher<MethodDescription> getMethodsMatcher();

    /**
     * 获取被增强方法对应的interceptor名称
     */
    String getMethodsInterceptor();
}

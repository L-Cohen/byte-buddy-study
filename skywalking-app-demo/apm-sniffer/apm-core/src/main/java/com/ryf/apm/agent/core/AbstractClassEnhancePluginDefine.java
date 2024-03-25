package com.ryf.apm.agent.core;

import com.ryf.apm.agent.core.interceptor.ConstructorInterceptPoint;
import com.ryf.apm.agent.core.interceptor.StaticMethodsInterceptPoint;
import com.ryf.apm.agent.core.match.ClassMatch;
import com.ryf.apm.agent.core.interceptor.InstanceMethodsInterceptorPoint;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 插件抽象顶层父类
 * @date 2023/11/5
 */
public abstract class AbstractClassEnhancePluginDefine {

    /**
     * 获取当前插件要增强的类
     */
    protected abstract ClassMatch enhanceClass();

    /**
     * 实例方法的拦截点获取
     */
    protected abstract InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints();

    /**
     * 获取构造方法的拦截点
     */
    public abstract ConstructorInterceptPoint[] getConstructorsInterceptPoints();

    /**
     * 获取静态方法的拦截点
     */
    public abstract StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints();
}

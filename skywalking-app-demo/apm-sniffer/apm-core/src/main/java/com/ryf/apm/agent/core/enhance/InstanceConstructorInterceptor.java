package com.ryf.apm.agent.core.enhance;

/**
 * 构造方法的interceptor 必须实现该接口
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2024/3/26
 */
public interface InstanceConstructorInterceptor {
    /**
     * Called after the origin constructor invocation.
     * @param objInst 构造器new出来的对象
     * @param allArguments 构造器参数
     */
    void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable;
}

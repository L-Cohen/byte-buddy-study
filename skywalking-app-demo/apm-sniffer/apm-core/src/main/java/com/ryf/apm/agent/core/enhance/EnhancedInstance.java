package com.ryf.apm.agent.core.enhance;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 所有需要增强构造方法和实例方法的字节码都会实现该接口
 * @date 2024/3/28
 */
public interface EnhancedInstance {
    Object getSkyWalkingDynamicField();

    void setSkyWalkingDynamicField(Object value);
}

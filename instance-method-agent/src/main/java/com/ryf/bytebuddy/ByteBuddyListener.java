package com.ryf.bytebuddy;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/29
 */
@Slf4j
public class ByteBuddyListener implements AgentBuilder.Listener {
    /**
     * 当某个类要被加载时的通知
     * @param typeName    The binary name of the instrumented type.
     * @param classLoader The class loader which is loading this type or {@code null} if loaded by the boots loader.
     * @param module      The instrumented type's module or {@code null} if the current VM does not support modules.
     * @param loaded      {@code true} if the type is already loaded.
     */
    @Override
    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        log.info("onDiscovery: typeName={}", typeName);
    }

    /**
     * 当某个类完成Transform进行回调
     * @param typeDescription The type that is being transformed.
     * @param classLoader     The class loader which is loading this type or {@code null} if loaded by the boots loader.
     * @param module          The transformed type's module or {@code null} if the current VM does not support modules.
     * @param loaded          {@code true} if the type is already loaded.
     * @param dynamicType     The dynamic type that was created.
     */
    @Override
    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
        log.info("onTransformation: typeName={}", typeDescription.getActualName());
    }

    /**
     * 当某个类将要被加载，但配置了忽略时的回调
     * @param typeDescription The type being ignored for transformation.
     * @param classLoader     The class loader which is loading this type or {@code null} if loaded by the boots loader.
     * @param module          The ignored type's module or {@code null} if the current VM does not support modules.
     * @param loaded          {@code true} if the type is already loaded.
     */
    @Override
    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
        log.info("onIgnored: typeName={}", typeDescription.getActualName());
    }

    /**
     * bytebuddy，transform异常回调
     * @param typeName    The binary name of the instrumented type.
     * @param classLoader The class loader which is loading this type or {@code null} if loaded by the boots loader.
     * @param module      The instrumented type's module or {@code null} if the current VM does not support modules.
     * @param loaded      {@code true} if the type is already loaded.
     * @param throwable   The occurred error.
     */
    @Override
    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
        log.info("onError: typeName={}", typeName);
    }

    /**
     * 某个类处理完成（transform、ignore、error）时回调
     * @param typeName    The binary name of the instrumented type.
     * @param classLoader The class loader which is loading this type or {@code null} if loaded by the boots loader.
     * @param module      The instrumented type's module or {@code null} if the current VM does not support modules.
     * @param loaded      {@code true} if the type is already loaded.
     */
    @Override
    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        log.info("onComplete: typeName={}", typeName);
    }
}

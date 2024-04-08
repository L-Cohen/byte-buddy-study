package com.ryf.apm.agent.core;

import cn.hutool.core.util.StrUtil;
import com.ryf.apm.agent.core.enhance.EnhanceContext;
import com.ryf.apm.agent.core.interceptor.ConstructorInterceptPoint;
import com.ryf.apm.agent.core.interceptor.InstanceMethodsInterceptorPoint;
import com.ryf.apm.agent.core.interceptor.StaticMethodsInterceptPoint;
import com.ryf.apm.agent.core.match.ClassMatch;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 插件抽象顶层父类
 * @date 2023/11/5
 */
@Slf4j
public abstract class AbstractClassEnhancePluginDefine {

    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

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

    /**
     * 增强类的主入口
     *
     * @param typeDescription 增强的类
     * @param builder         builder
     * @param classLoader     类加载器
     * @param context         上下文
     * @return builder
     */
    public DynamicType.Builder<?> define(TypeDescription typeDescription, DynamicType.Builder<?> builder, ClassLoader classLoader, EnhanceContext context) {
        // 拦截增强类的类名，com.ryf.apm.agent.plugin.mysql.MysqlInstrumentation
        String interceptorDefineClassName = this.getClass().getName();
        // 待增强的类，com.mysql.cj.jdbc.ServerPreparedStatement
        String transformClassName = typeDescription.getTypeName();
        if (StrUtil.isEmpty(transformClassName)) {
            log.info("class name of being intercepted is not defined by {}.", interceptorDefineClassName);
            return null;
        }

        log.info("prepare to enhance class {} by {}.", transformClassName, interceptorDefineClassName);
        DynamicType.Builder<?> newClassBuilder = this.enhance(typeDescription, builder, classLoader, context);
        // 增强完成
        context.initializationStageCompleted();
        log.info("enhance class {} by {} completely.", transformClassName, interceptorDefineClassName);
        return newClassBuilder;
    }

    private DynamicType.Builder<?> enhance(TypeDescription typeDescription, DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader, EnhanceContext context) {
        // 增强静态方法
        newClassBuilder = this.enhanceClass(typeDescription, newClassBuilder, classLoader);

        // 增强实例方法和构造方法
        newClassBuilder = this.enhanceInstance(typeDescription, newClassBuilder, classLoader, context);

        return newClassBuilder;
    }

    /**
     * 增强静态方法
     *
     * @param typeDescription 类信息
     * @param newClassBuilder builder
     * @param classLoader     类加载器
     * @return builder
     */
    protected abstract DynamicType.Builder<?> enhanceClass(TypeDescription typeDescription, DynamicType.Builder<?> newClassBuilder,
                                                           ClassLoader classLoader);

    protected abstract DynamicType.Builder<?> enhanceInstance(TypeDescription typeDescription, DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader, EnhanceContext context);
}

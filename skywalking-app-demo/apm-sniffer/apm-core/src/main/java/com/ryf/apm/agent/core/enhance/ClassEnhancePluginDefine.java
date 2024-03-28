package com.ryf.apm.agent.core.enhance;

import cn.hutool.core.util.StrUtil;
import com.ryf.apm.agent.core.AbstractClassEnhancePluginDefine;
import com.ryf.apm.agent.core.interceptor.ConstructorInterceptPoint;
import com.ryf.apm.agent.core.interceptor.InstanceMethodsInterceptorPoint;
import com.ryf.apm.agent.core.interceptor.StaticMethodsInterceptPoint;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.jar.asm.Opcodes;

import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * 所有的插件都需要直接或简介继承此类
 * 该类完成所有增强操作，静态方法增强，示例方法增强
 * 指定builder.method().interceptor()
 *
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2024/3/26
 */
public abstract class ClassEnhancePluginDefine extends AbstractClassEnhancePluginDefine {
    @Override
    protected DynamicType.Builder<?> enhanceClass(TypeDescription typeDescription, DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader) {
        String enhanceOriginClassName = typeDescription.getTypeName();
        StaticMethodsInterceptPoint[] staticMethodsInterceptPoints = getStaticMethodsInterceptPoints();
        if (Objects.isNull(staticMethodsInterceptPoints) || staticMethodsInterceptPoints.length == 0) {
            return newClassBuilder;
        }

        for (StaticMethodsInterceptPoint staticMethodsInterceptPoint : staticMethodsInterceptPoints) {
            String methodsInterceptor = staticMethodsInterceptPoint.getMethodsInterceptor();
            if (StrUtil.isEmpty(methodsInterceptor)) {
                throw new RuntimeException("no StaticMethodsAroundInterceptor define to enhance class " + enhanceOriginClassName);
            }
            newClassBuilder = newClassBuilder.method(isStatic().and(staticMethodsInterceptPoint.getMethodsMatcher()))
                    .intercept(MethodDelegation.withDefaultConfiguration()
                            .to(new StaticMethodsInter(methodsInterceptor, classLoader))
                    );
        }
        return newClassBuilder;
    }

    @Override
    protected DynamicType.Builder<?> enhanceInstance(TypeDescription typeDescription, DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader, EnhanceContext context) {
        String enhanceOriginClassName = typeDescription.getTypeName();
        ConstructorInterceptPoint[] constructorsInterceptPoints = getConstructorsInterceptPoints();
        InstanceMethodsInterceptorPoint[] instanceMethodsInterceptorPoints = getInstanceMethodsInterceptorPoints();
        boolean existedConstructorInterceptPoint = Objects.nonNull(constructorsInterceptPoints) && constructorsInterceptPoints.length > 0;
        boolean existedMethodsInterceptPoints = Objects.nonNull(instanceMethodsInterceptorPoints) && instanceMethodsInterceptorPoints.length > 0;
        // nothing need to be enhanced in class instance, maybe need enhance static methods.
        if (!existedConstructorInterceptPoint && !existedMethodsInterceptPoints) {
            return newClassBuilder;
        }

        // 为字节码新增属性, 对于同一个typeDescription只需要执行一次
        // 表示typeDescription不是EnhancedInstance的子类或实现类
        if (!typeDescription.isAssignableTo(EnhancedInstance.class)) {
            if (!context.isObjectExtended()) {
                newClassBuilder = newClassBuilder.defineField(CONTEXT_ATTR_NAME,
                                Object.class,
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_VOLATILE)
                        // 指定属性的getter和setter方法
                        .implement(EnhancedInstance.class)
                        .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
                // 完成对象的扩展
                context.extendObjectCompleted();
            }
        }

        // enhance constructMethods
        if (existedConstructorInterceptPoint) {
            for (ConstructorInterceptPoint constructorsInterceptPoint : constructorsInterceptPoints) {
                String methodsInterceptor = constructorsInterceptPoint.getMethodsInterceptor();
                if (StrUtil.isEmpty(methodsInterceptor)) {
                    throw new RuntimeException("no ConstructorMethodsAroundInterceptor define to enhance class " + enhanceOriginClassName);
                }
                newClassBuilder = newClassBuilder.method(constructorsInterceptPoint.getConstructorMatcher())
                        .intercept(SuperMethodCall.INSTANCE
                                .andThen(MethodDelegation
                                        .withDefaultConfiguration()
                                        .to(new ConstructorInter(methodsInterceptor, classLoader)))
                        );
            }
        }

        // enhance instance methods
        if (existedMethodsInterceptPoints) {
            for (InstanceMethodsInterceptorPoint instanceMethodsInterceptorPoint : instanceMethodsInterceptorPoints) {
                String methodsInterceptor = instanceMethodsInterceptorPoint.getMethodsInterceptor();
                if (StrUtil.isEmpty(methodsInterceptor)) {
                    throw new RuntimeException("no InstanceMethodsAroundInterceptor define to enhance class " + enhanceOriginClassName);
                }
                newClassBuilder = newClassBuilder.method(not(isStatic()).and(instanceMethodsInterceptorPoint.getMethodsMatcher()))
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new InstMethodsInter(methodsInterceptor, classLoader))
                        );
            }
        }
        return newClassBuilder;
    }
}

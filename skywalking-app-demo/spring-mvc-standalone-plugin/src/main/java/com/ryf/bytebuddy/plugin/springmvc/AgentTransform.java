package com.ryf.bytebuddy.plugin.springmvc;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/29
 */
@Slf4j
public class AgentTransform implements AgentBuilder.Transformer {

    private static final String MAPPING_PKG_PREFIX = "org.springframework.web.bind.annotation";
    private static final String MAPPING_PKG_SUFFIX = "Mapping";

    /**
     * 当要被拦截的type(ElementMatcher<? super TypeDescription> typeMatcher)匹配之后，会进入此方法
     *
     * @param builder          The dynamic builder to transform.
     * @param typeDescription  要被加载的type信息
     * @param classLoader      The class loader of the instrumented class. Might be {@code null} to represent the bootstrap class loader.
     * @param module           The class's module or {@code null} if the current VM does not support modules.
     * @param protectionDomain The protection domain of the transformed type.
     * @return
     */
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        log.info("clazz name to transform:{}", typeDescription.getCanonicalName());
        // bytebuddy 库类基本为不可变的，修改后要返回修改后的对象，否则会导致修改丢失
        return builder.method(
                        not(isStatic())
                                .and(
                                        isAnnotatedWith(nameStartsWith(MAPPING_PKG_PREFIX)
                                                .and(nameEndsWith(MAPPING_PKG_SUFFIX))))
                )
                .intercept(MethodDelegation.to(new SpringMvcInterceptor()));
    }
}

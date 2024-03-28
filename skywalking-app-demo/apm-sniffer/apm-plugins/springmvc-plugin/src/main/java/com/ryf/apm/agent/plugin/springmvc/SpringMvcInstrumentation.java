package com.ryf.apm.agent.plugin.springmvc;

import com.ryf.apm.agent.core.enhance.ClassEnhancePluginDefine;
import com.ryf.apm.agent.core.interceptor.ConstructorInterceptPoint;
import com.ryf.apm.agent.core.interceptor.InstanceMethodsInterceptorPoint;
import com.ryf.apm.agent.core.interceptor.StaticMethodsInterceptPoint;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description spring-mvc插桩公共类
 * @date 2023/11/6
 */
public abstract class SpringMvcInstrumentation extends ClassEnhancePluginDefine {

    protected static final String CONTROLLER_NAME = "org.springframework.stereotype.Controller";
    protected static final String REST_CONTROLLER_NAME = "org.springframework.web.bind.annotation.RestController";
    private static final String INTERCEPTOR_NAME = "com.ryf.apm.agent.plugin.springmvc.interceptor.SpringMvcInterceptor";
    private static final String MAPPING_PKG_PREFIX = "org.springframework.web.bind.annotation";
    private static final String MAPPING_PKG_SUFFIX = "Mapping";

    @Override
    protected InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints() {
        return new InstanceMethodsInterceptorPoint[] {
                new InstanceMethodsInterceptorPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return not(isStatic())
                                .and(
                                        isAnnotatedWith(nameStartsWith(MAPPING_PKG_PREFIX)
                                                .and(nameEndsWith(MAPPING_PKG_SUFFIX))));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return INTERCEPTOR_NAME;
                    }
                }
        };
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return null;
    }

    @Override
    public StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints() {
        return null;
    }
}

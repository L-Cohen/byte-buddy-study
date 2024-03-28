package com.ryf.apm.agent.plugin.mysql;

import com.ryf.apm.agent.core.enhance.ClassEnhancePluginDefine;
import com.ryf.apm.agent.core.interceptor.ConstructorInterceptPoint;
import com.ryf.apm.agent.core.interceptor.InstanceMethodsInterceptorPoint;
import com.ryf.apm.agent.core.interceptor.StaticMethodsInterceptPoint;
import com.ryf.apm.agent.core.match.ClassMatch;
import com.ryf.apm.agent.core.match.MultiClassNameMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description mysql插件定义
 * @date 2023/11/6
 */
public class MysqlInstrumentation extends ClassEnhancePluginDefine {

    public static final String CLIENT_PS_NAME = "com.mysql.cj.jdbc.ClientPreparedStatement";
    public static final String SERVER_PS_NAME = "com.mysql.cj.jdbc.ServerPreparedStatement";
    private static final String INTERCEPT_CLASS = "com.ryf.apm.agent.plugin.mysql.interceptor.MysqlInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return MultiClassNameMatch.byMultiClassMatch(CLIENT_PS_NAME, SERVER_PS_NAME);
    }

    @Override
    protected InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints() {
        return new InstanceMethodsInterceptorPoint[] {
                new InstanceMethodsInterceptorPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("execute")
                                .or(named("executeQuery"))
                                .or(named("executeUpdate"));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return INTERCEPT_CLASS;
                    }
                }
        };
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints() {
        return new StaticMethodsInterceptPoint[0];
    }
}

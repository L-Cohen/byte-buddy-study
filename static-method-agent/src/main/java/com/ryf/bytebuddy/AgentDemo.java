package com.ryf.bytebuddy;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 实现拦截spring mvc 的controller,并统计耗时
 * @date 2023/10/29
 */
@Slf4j
public class AgentDemo {

    private static final String CLASS_NAME = "com.ryf.app.utils.StringUtil";

    /**
     * 在main方法执行之前会被自动调用，插桩的入口
     *
     * @param arg             传给Java-agent的参数
     * @param instrumentation 插桩
     */
    public static void premain(String arg, Instrumentation instrumentation) {
        log.info("进入preMain：args：{}", arg);
        AgentBuilder builder = new AgentBuilder.Default()
                // 配置忽略拦截的包
                .ignore(nameStartsWith("net.bytebuddy")
                        .or(nameStartsWith("org.slf4j"))
                )
                // 配置哪些类需要拦截，当被拦截的type加载的时候进入此方法
                .type(getTypeMatcher())
                //
                .transform(new AgentTransform());
        // 插桩
        builder.installOn(instrumentation);
        log.info("退出preMain");
    }

    private static ElementMatcher<? super TypeDescription> getTypeMatcher() {
        // 判断名称相等
        // return named(CLASS_NAME);
        // 第二种方式
        return new ElementMatcher.Junction.AbstractBase<NamedElement>() {

            @Override
            public boolean matches(NamedElement target) {
                return CLASS_NAME.equals(target.getActualName());
            }
        };
    }

}

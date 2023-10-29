package com.ryf.bytebuddy;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;

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

    private static final String CONTROLLER_NAME = "org.springframework.stereotype.Controller";
    private static final String REST_CONTROLLER_NAME = "org.springframework.web.bind.annotation.RestController";

    /**
     * 在main方法执行之前会被自动调用，插桩的入口
     * @param arg 传给Java-agent的参数
     * @param instrumentation 插桩
     */
    public static void premain(String arg, Instrumentation instrumentation) {
        log.info("进入preMain：args：{}", arg);
        AgentBuilder builder = new AgentBuilder.Default()
                // 配置忽略拦截的包
                .ignore(nameStartsWith("net.bytebuddy")
                        .or(nameStartsWith("org.slf4j"))
                )
                // 配置哪些类需要拦截
                .type(isAnnotatedWith(named(CONTROLLER_NAME)).or(named(REST_CONTROLLER_NAME)))
                //
                .transform(new AgentTransform())
                .with(new ByteBuddyListener());
        // 插桩
        builder.installOn(instrumentation);
        log.info("退出preMain");
    }
}

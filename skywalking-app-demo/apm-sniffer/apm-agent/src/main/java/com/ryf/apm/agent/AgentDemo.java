package com.ryf.apm.agent;

import com.ryf.apm.agent.core.PluginFinder;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/11/5
 */
@Slf4j
public class AgentDemo {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        PluginFinder pluginFinder;
        try {
            pluginFinder = new PluginFinder(null);
        } catch (Exception e) {
            log.error("init failed", e);
            return;
        }

        AgentBuilder agentBuilder = new AgentBuilder.Default().ignore(
                nameStartsWith("net.bytebuddy.")
                        .or(nameStartsWith("org.slf4j."))
                        .or(nameStartsWith("org.groovy."))
                        .or(nameContains("javassist"))
                        .or(nameContains(".asm."))
                        .or(nameContains(".reflectasm."))
                        .or(nameStartsWith("sun.reflect"))
                        .or(ElementMatchers.isSynthetic()));

        agentBuilder
                // 配置忽略拦截的包
                .ignore(nameStartsWith("net.bytebuddy")
                        .or(nameStartsWith("org.slf4j"))
                )
                .type(pluginFinder.buildMatch())
                .transform(new CustomTransformer());
        agentBuilder.installOn(instrumentation);
    }
}

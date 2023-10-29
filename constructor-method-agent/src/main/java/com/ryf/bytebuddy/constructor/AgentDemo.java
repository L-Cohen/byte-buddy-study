package com.ryf.bytebuddy.constructor;

import net.bytebuddy.agent.builder.AgentBuilder;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/29
 */
public class AgentDemo {
    private static final String CLASS_NAME = "com.ryf.app.service.impl.HelloServiceImpl";

    public static void premain(String arg, Instrumentation instrumentation) {
        AgentBuilder.Identified.Extendable builder = new AgentBuilder.Default()
                .ignore(nameStartsWith("net.bytebuddy")
                        .or(nameStartsWith("org.slf4j"))
                )
                .type(named(CLASS_NAME))
                .transform(new ConstructorTransform());
        builder.installOn(instrumentation);
    }
}

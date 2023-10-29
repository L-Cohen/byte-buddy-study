package com.ryf.agent;

import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;

/**
 * 启动命令：java -javaagent:AgentDemo.jar=k1=v1,k2=v2 -jar xxx.jar
 * k1=v1,k2=v2 就是传给AgentDemo.jar的参数
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/29
 */
@Slf4j
public class AgentDemo {

    /**
     * 在main方法执行之前会被自动调用，插桩的入口
     * @param arg 传给Java-agent的参数
     * @param instrumentation 插桩
     */
    public static void premain(String arg, Instrumentation instrumentation) {
        log.info("进入preMain：args：{}", arg);
        instrumentation.addTransformer(new ClassFileTransformerDemo());
    }
}

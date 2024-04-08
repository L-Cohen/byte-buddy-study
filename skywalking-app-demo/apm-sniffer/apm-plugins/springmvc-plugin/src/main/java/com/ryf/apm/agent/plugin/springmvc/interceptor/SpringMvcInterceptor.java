package com.ryf.apm.agent.plugin.springmvc.interceptor;

import cn.hutool.core.io.FileUtil;
import com.ryf.apm.agent.core.enhance.EnhancedInstance;
import com.ryf.apm.agent.core.enhance.InstanceMethodsAroundInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description spring-mvc拦截器
 * @date 2023/11/6
 */
@Slf4j
public class SpringMvcInterceptor implements InstanceMethodsAroundInterceptor {
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        FileUtil.writeString("test", new File("E:\\test.txt"), StandardCharsets.UTF_8);
        log.info("before spring mvc controller:{} method: {} args:{}", objInst.getClass().getName(), method.getName(), Arrays.toString(allArguments));
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        log.info("spring mvc after method execute ret:{}", ret);
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {
        log.error("spring mvc exception", t);
    }
}

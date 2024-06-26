package com.ryf.apm.agent.plugin.mysql.interceptor;

import com.ryf.apm.agent.core.enhance.EnhancedInstance;
import com.ryf.apm.agent.core.enhance.InstanceMethodsAroundInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description mysql具体拦截实现
 * @date 2023/11/6
 */
@Slf4j
public class MysqlInterceptor implements InstanceMethodsAroundInterceptor {
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes) throws Throwable {
        objInst.setSkyWalkingDynamicField("select * from t_test");
        log.info("before mysql controller:{} method: {} args:{}", objInst.getClass().getName(), method.getName(), Arrays.toString(allArguments));
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        log.info("mysql after method execute ret:{}", ret);
        // 自定义逻辑
        log.info("send sql:{}", objInst.getSkyWalkingDynamicField());
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {
        log.error("mysql exception", t);
    }
}

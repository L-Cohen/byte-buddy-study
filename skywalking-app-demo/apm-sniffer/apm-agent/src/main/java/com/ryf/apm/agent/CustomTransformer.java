package com.ryf.apm.agent;

import com.ryf.apm.agent.core.AbstractClassEnhancePluginDefine;
import com.ryf.apm.agent.core.PluginFinder;
import com.ryf.apm.agent.core.enhance.EnhanceContext;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;
import java.util.List;
import java.util.Objects;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/11/5
 */
@Slf4j
public class CustomTransformer implements AgentBuilder.Transformer {
    private final PluginFinder pluginFinder;

    public CustomTransformer(PluginFinder pluginFinder) {
        this.pluginFinder = pluginFinder;
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                            TypeDescription typeDescription,
                                            // 加载typeDescription这个类的类加载器（加载被拦截类的类加载器）
                                            ClassLoader classLoader,
                                            JavaModule module,
                                            ProtectionDomain protectionDomain) {
        List<AbstractClassEnhancePluginDefine> plugins = pluginFinder.find(typeDescription);
        if (plugins.isEmpty()) {
            log.debug("matched class:{},plugin not found", typeDescription.getActualName());
            return builder;
        }
        EnhanceContext context = new EnhanceContext();
        DynamicType.Builder<?> newBuilder = builder;
        for (AbstractClassEnhancePluginDefine plugin : plugins) {
            DynamicType.Builder<?> postProcessorBuilder = plugin.define(typeDescription, newBuilder, classLoader, context);
            if (Objects.nonNull(postProcessorBuilder)) {
                newBuilder = postProcessorBuilder;
            }
        }
        if (context.isEnhanced()) {
            log.debug("enhance:{} finish", typeDescription.getActualName());
        }
        return newBuilder;
    }
}

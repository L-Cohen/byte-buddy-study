package com.ryf.apm.agent.core;

import cn.hutool.core.collection.CollUtil;
import com.ryf.apm.agent.core.loader.AgentClassLoader;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2024/3/30
 */
@Slf4j
public class PluginBootstrap {

    /**
     * load all plugins
     * 1.find agent.jar location
     * 2.use dynamic classloader to load plugin jar
     *
     * @return plugin definition list.
     */
    public List<AbstractClassEnhancePluginDefine> loadPlugins()  {
        AgentClassLoader.initLoader();
        PluginResourcesResolver resolver = new PluginResourcesResolver();
        List<URL> resources = resolver.getResources();
        if (CollUtil.isEmpty(resources)) {
            log.info("no plugin files (skywalking-plugin.def) found, continue to start application.");
            return new ArrayList<>();
        }

        List<AbstractClassEnhancePluginDefine> plugins = new ArrayList<>();
        for (URL pluginUrl : resources) {
            try {
                PluginCfg.INSTANCE.load(pluginUrl.openStream());
            } catch (Throwable t) {
                log.error("plugin file [{}] init failure.", pluginUrl, t);
            }
            List<PluginDefine> pluginClassList = PluginCfg.INSTANCE.getPluginClassList();

            // 通过全类名反射获取对象，这个对象就是插件定义对象
            for (PluginDefine pluginDefine : pluginClassList) {
                // 使用自定义的classloader
                try {
                    Class<?> pluginDefineClass = Class.forName(pluginDefine.getDefineClass(), Boolean.TRUE, AgentClassLoader.getDefaultLoader());
                    AbstractClassEnhancePluginDefine plugin = (AbstractClassEnhancePluginDefine) pluginDefineClass.newInstance();
                    plugins.add(plugin);
                } catch (Exception e) {
                    log.error("load plugin [{}] failure.", pluginDefine.getDefineClass(), e);
                }
            }
        }
        return plugins;
    }
}

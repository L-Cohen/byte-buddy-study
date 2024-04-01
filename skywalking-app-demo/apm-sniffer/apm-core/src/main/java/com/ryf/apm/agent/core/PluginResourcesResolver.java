package com.ryf.apm.agent.core;

import com.ryf.apm.agent.core.loader.AgentClassLoader;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2024/3/31
 */
@Slf4j
public class PluginResourcesResolver {
    /**
     * 获取插件目录（plugins）下所有jar内的skywalking-plugin.def文件
     *
     * @return skywalking-plugin.def文件的URL
     */
    public List<URL> getResources() {
        List<URL> cfgUrlPaths = new ArrayList<>();
        Enumeration<URL> urls;
        try {
            urls = AgentClassLoader.getDefaultLoader().getResources("skywalking-plugin.def");
            while (urls.hasMoreElements()) {
                URL puginUrl = urls.nextElement();
                cfgUrlPaths.add(puginUrl);
                log.info("find plugin define in :{}", puginUrl);
            }
            return cfgUrlPaths;
        } catch (Exception e) {
            log.error("read resources failure.", e);
        }
        return null;
    }
}

package com.ryf.apm.agent.core;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2024/3/31
 */
@Slf4j
public enum PluginCfg {
    INSTANCE;

    /**
     * 存放所有构造PluginDefine实例
     */
    private final List<PluginDefine> pluginClassList = new ArrayList<>();

    /**
     * 转换skywalking-plugin.def为PluginDefine
     * @param input skywalking-plugin.def
     * @throws IOException io异常
     */
    void load(InputStream input) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String pluginDefine;
            while ((pluginDefine = reader.readLine()) != null) {
                try {
                    if (pluginDefine.trim().isEmpty() || pluginDefine.startsWith("#")) {
                        continue;
                    }
                    PluginDefine plugin = PluginDefine.build(pluginDefine);
                    pluginClassList.add(plugin);
                } catch (Exception e) {
                    log.error("Failed to format plugin({}) define.", pluginDefine, e);
                }
            }
        } finally {
            input.close();
        }
    }

    public List<PluginDefine> getPluginClassList() {
        return pluginClassList;
    }

}

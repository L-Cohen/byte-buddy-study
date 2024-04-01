package com.ryf.apm.agent.core;

import cn.hutool.core.util.StrUtil;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 插件定义
 * @date 2024/3/31
 */
public class PluginDefine {
    /**
     * Plugin name.
     */
    private final String name;

    /**
     * The class name of plugin defined.
     */
    private final String defineClass;

    private PluginDefine(String name, String defineClass) {
        this.name = name;
        this.defineClass = defineClass;
    }

    public static PluginDefine build(String define) throws IllegalArgumentException {
        if (StrUtil.isEmpty(define)) {
            throw new IllegalArgumentException(define);
        }

        String[] pluginDefine = define.split("=");
        if (pluginDefine.length != 2) {
            throw new IllegalArgumentException(define);
        }

        String pluginName = pluginDefine[0];
        String defineClass = pluginDefine[1];
        return new PluginDefine(pluginName, defineClass);
    }

    public String getDefineClass() {
        return defineClass;
    }

    public String getName() {
        return name;
    }
}

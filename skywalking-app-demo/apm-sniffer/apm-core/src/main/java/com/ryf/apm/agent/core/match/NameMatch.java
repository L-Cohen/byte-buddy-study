package com.ryf.apm.agent.core.match;

import lombok.Getter;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 名称匹配(类名)，仅适用于named
 * @date 2023/11/6
 */
@Getter
public class NameMatch implements ClassMatch {
    private final String className;

    private NameMatch(String className) {
        this.className = className;
    }

    public static NameMatch byName(String className) {
        return new NameMatch(className);
    }
}

package com.ryf.apm.agent.core.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 间接匹配，所有非NameMatch的情况
 * @date 2023/11/6
 */
public interface IndirectMatch extends ClassMatch {
    /**
     * 构造type()的参数，如type(named(CLIENT_PS_NAME).or(named(SERVER_PS_NAME)))
     */
    ElementMatcher.Junction<? super TypeDescription> buildJunction();
}

package com.ryf.apm.agent.plugin.springmvc;

import com.ryf.apm.agent.core.match.ClassAnnotationMatch;
import com.ryf.apm.agent.core.match.ClassMatch;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description spring-mvc插桩
 * @date 2023/11/6
 */
public  class ControllerInstrumentation extends SpringMvcInstrumentation {
    @Override
    protected ClassMatch enhanceClass() {
        return ClassAnnotationMatch.byAnnotation(CONTROLLER_NAME);
    }
}

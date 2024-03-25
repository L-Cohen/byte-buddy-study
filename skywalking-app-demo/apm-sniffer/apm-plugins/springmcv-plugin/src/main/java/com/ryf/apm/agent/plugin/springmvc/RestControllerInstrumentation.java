package com.ryf.apm.agent.plugin.springmvc;

import com.ryf.apm.agent.core.match.ClassMatch;
import com.ryf.apm.agent.core.match.ClassAnnotationMatch;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description spring-mvc插桩
 * @date 2023/11/6
 */
public  class RestControllerInstrumentation extends SpringMvcInstrumentation {

    @Override
    protected ClassMatch enhanceClass() {
        return ClassAnnotationMatch.byAnnotation(REST_CONTROLLER_NAME);
    }
}

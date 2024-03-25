package com.ryf.apm.agent.core.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 注解匹配器
 * @date 2023/11/6
 */
public class ClassAnnotationMatch implements IndirectMatch{

    private String[] annotations;

    private ClassAnnotationMatch(String[] annotations) {
        if (annotations == null || annotations.length == 0) {
            throw new IllegalArgumentException("annotations is null");
        }
        this.annotations = annotations;
    }
    @Override
    public ElementMatcher.Junction<? super TypeDescription> buildJunction() {
        ElementMatcher.Junction<? super TypeDescription> junction = null;
        for (String annotation : annotations) {
            if (junction == null) {
                junction = buildEachAnnotation(annotation);
            } else {
                junction = junction.and(buildEachAnnotation(annotation));
            }
        }
        junction = junction.and(not(isInterface()));
        return junction;
    }


    private ElementMatcher.Junction<? super TypeDescription> buildEachAnnotation(String annotation) {
        return isAnnotatedWith(named(annotation));
    }

    public static ClassAnnotationMatch byAnnotation(String... annotations) {
        return new ClassAnnotationMatch(annotations);
    }
}

package com.ryf.apm.agent.core.match;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 注解匹配器
 * @date 2023/11/6
 */
public class ClassAnnotationMatch implements IndirectMatch {

    private final String[] annotations;

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

    @Override
    public boolean isMatch(TypeDescription typeDescription) {
        // annotation:{"a.b.Anno1", "a.b.Anno2"}
        // 获取typeDescription上的注解list
        List<String> annotationList = new ArrayList<>(Arrays.asList(annotations));
        AnnotationList declaredAnnotations = typeDescription.getDeclaredAnnotations();
        for (AnnotationDescription annotation : declaredAnnotations) {
            annotationList.remove(annotation.getAnnotationType().getActualName());
        }
        return annotationList.isEmpty();
    }


    private ElementMatcher.Junction<? super TypeDescription> buildEachAnnotation(String annotation) {
        return isAnnotatedWith(named(annotation));
    }

    public static ClassAnnotationMatch byAnnotation(String... annotations) {
        return new ClassAnnotationMatch(annotations);
    }
}

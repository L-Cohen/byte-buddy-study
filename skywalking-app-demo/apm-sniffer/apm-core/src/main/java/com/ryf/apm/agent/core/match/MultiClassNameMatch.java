package com.ryf.apm.agent.core.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 多个类名匹配相等的情况
 * @date 2023/11/6
 */
public class MultiClassNameMatch implements IndirectMatch{
    /**
     * 要匹配的类名称
     */
    private final List<String> matchClassNames;

    private MultiClassNameMatch(String[] classNames) {
        if (classNames == null || classNames.length == 0) {
            throw new IllegalArgumentException("match class names is null");
        }
        this.matchClassNames = Arrays.asList(classNames);
    }

    @Override
    public ElementMatcher.Junction<? super TypeDescription> buildJunction() {
        ElementMatcher.Junction<? super TypeDescription> junction = null;
        for (String matchClassName : matchClassNames) {
            if (junction == null) {
                junction = named(matchClassName);
            } else {
                junction = junction.or(named(matchClassName));
            }
        }
        return junction;
    }

    public static IndirectMatch byMultiClassMatch(String... classNames) {
        return new MultiClassNameMatch(classNames);
    }
}

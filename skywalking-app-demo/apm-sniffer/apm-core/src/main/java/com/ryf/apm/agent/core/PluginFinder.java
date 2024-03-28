package com.ryf.apm.agent.core;

import com.ryf.apm.agent.core.match.ClassMatch;
import com.ryf.apm.agent.core.match.IndirectMatch;
import com.ryf.apm.agent.core.match.NameMatch;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.isInterface;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 插件查找器，spi机制
 * @date 2023/11/6
 */
public class PluginFinder {

    /**
     * 用于存放ClassMatch为nameMatch匹配器的容器
     * key：全类名
     * value：同一个类可以被多个plugin增强
     */
    private final Map<String, LinkedList<AbstractClassEnhancePluginDefine>> nameMatchDefine = new HashMap<>();
    /**
     * 用于存储间接匹配（ClassMatch为IndirectMatch）启动容器
     */
    private final List<AbstractClassEnhancePluginDefine> signatureMatchDefine = new ArrayList<>();

    /**
     * 对插件进行分类
     *
     * @param plugins 加载到所有插件
     */
    public PluginFinder(List<AbstractClassEnhancePluginDefine> plugins) {
        for (AbstractClassEnhancePluginDefine plugin : plugins) {
            ClassMatch classMatch = plugin.enhanceClass();
            if (classMatch == null) {
                continue;
            }

            if (classMatch instanceof NameMatch) {
                NameMatch nameMatch = (NameMatch) classMatch;
                LinkedList<AbstractClassEnhancePluginDefine> list = nameMatchDefine.computeIfAbsent(nameMatch.getClassName(), key -> new LinkedList<>());
                list.add(plugin);
            } else {
                signatureMatchDefine.add(plugin);
            }
        }
    }


    /**
     * plugin1.junction.or(plugin2.junction)
     */
    public ElementMatcher<? super TypeDescription> buildMatch() {
        ElementMatcher.Junction<? super TypeDescription> junction = new ElementMatcher.Junction.AbstractBase<NamedElement>() {
            @Override
            public boolean matches(NamedElement target) {
                // 当某个类第一次被加载时，都会回调至此
                return nameMatchDefine.containsKey(target.getActualName());
            }
        };
        // 只增强类
        junction = junction.and(not(isInterface()));
        for (AbstractClassEnhancePluginDefine pluginDefine : signatureMatchDefine) {
            ClassMatch classMatch = pluginDefine.enhanceClass();
            if (classMatch instanceof IndirectMatch) {
                IndirectMatch indirectMatch = (IndirectMatch) classMatch;
                junction = junction.or(indirectMatch.buildJunction());
            }
        }
        return junction;
    }

    /**
     * 根据匹配到的类获取插件集合
     *
     * @param typeDescription 当前匹配到的类
     * @return typeDescription对应的插件集合
     */
    public List<AbstractClassEnhancePluginDefine> find(TypeDescription typeDescription) {
        List<AbstractClassEnhancePluginDefine> pluginDefines = new LinkedList<>();
        // 获取全类名
        String typeName = typeDescription.getTypeName();
        if (nameMatchDefine.containsKey(typeName)) {
            // 获取nameMatchDefine插件集合
            LinkedList<AbstractClassEnhancePluginDefine> list = nameMatchDefine.get(typeName);
            pluginDefines.addAll(list);
        }
        // 处理signatureMatchDefine的集合
        for (AbstractClassEnhancePluginDefine pluginDefine : signatureMatchDefine) {
            IndirectMatch indirectMatch = (IndirectMatch) pluginDefine.enhanceClass();
            if (indirectMatch.isMatch(typeDescription)) {
                pluginDefines.add(pluginDefine);
            }
        }
        return pluginDefines;
    }
}

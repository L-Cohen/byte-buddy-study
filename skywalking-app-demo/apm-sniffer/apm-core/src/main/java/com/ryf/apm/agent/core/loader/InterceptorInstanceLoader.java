package com.ryf.apm.agent.core.loader;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 用于加载插件的拦截器
 * @date 2024/3/31
 */
public class InterceptorInstanceLoader {

    /**
     * 加载一个interceptor的实例
     *
     * @param className         全类名
     * @param targetClassLoader 加载拦截器的类加载器,
     *                          想要在插件拦截器中能够访问到被拦截的类，
     *                          需要是同一个类加载器或者是子类类加载器
     *                          被拦截的是类：A -> C1，targetClassLoader -> C1
     * @return InstanceConstructorInterceptor、InstanceMethodsAroundInterceptor、StaticMethodsAroundInterceptor
     */
    public static <T> T load(String className, ClassLoader targetClassLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (targetClassLoader == null) {
            targetClassLoader = InterceptorInstanceLoader.class.getClassLoader();
        }
        AgentClassLoader classLoader = new AgentClassLoader(targetClassLoader);
        return (T) Class.forName(className, true, classLoader).newInstance();
    }
}

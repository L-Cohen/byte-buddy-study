package com.ryf.apm.agent.core.loader;

import cn.hutool.core.util.ArrayUtil;
import com.ryf.apm.agent.core.PluginBootstrap;
import com.ryf.apm.agent.core.boot.AgentPackagePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 用于加载插件和插件的拦截器
 * @date 2024/3/30
 */
@Slf4j
public class AgentClassLoader extends ClassLoader {

    /**
     * The default class loader for the agent.
     * 用于加载插件的定义相关的类（除了插件的拦截器，interceptor），如mysqlInstrumentation
     */
    private static AgentClassLoader DEFAULT_LOADER;
    /**
     * 自定义类加载器的加载路径
     */
    private List<File> classpath;

    private List<Jar> allJars;
    private ReentrantLock jarScanLock = new ReentrantLock();
    private static final String JAR_URL_PREFIX = "jar:file:";

    public AgentClassLoader(ClassLoader parent) {
        super(parent);
        // 获取agent.jar的路径
        File agentDictionary = AgentPackagePath.getPath();
        classpath = new LinkedList<>();
        classpath.add(new File(agentDictionary, "plugins"));
    }

    public static void initLoader() {
        if (DEFAULT_LOADER == null) {
            synchronized (AgentClassLoader.class) {
                DEFAULT_LOADER = new AgentClassLoader(PluginBootstrap.class.getClassLoader());
            }
        }
    }

    public static AgentClassLoader getDefaultLoader() {
        return DEFAULT_LOADER;
    }

    /**
     * 加载插件的类
     * loadCLass --> 自动回调findClass（自定义自己的类加载逻辑） --> defineClass --> findLoadedClass
     *
     * @param name 类全限定名 com.ryf.apm.agent.plugin.mysql.MysqlInstrumentation
     * @return 类
     * @throws ClassNotFoundException 类加载异常
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        List<Jar> allJars = getAllJars();
        String path = name.replace(".", "/").concat(".class");
        log.info("find class path:{}", path);
        for (Jar jar : allJars) {
            log.info("jar :{}", jar.sourceFile.toString());
            JarEntry jarEntry = jar.jarFile.getJarEntry(path);
            if (Objects.isNull(jarEntry)) {
                continue;
            }
            // 通过
            try {
                // jar:file:/D:/work/apm/agent/target/apm-agent-1.0.0-SNAPSHOT.jar!/com/ryf/apm.agent/core/boot/xxx.class
                byte[] data = getClassBytes(jar, path);
                return defineClass(name, data, 0, data.length);
            } catch (IOException e) {
                log.error("find class error", e);
            }
        }
        throw new ClassNotFoundException("Can't find " + name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> allResources = new LinkedList<>();
        List<Jar> jarList = getAllJars();
        for (Jar jar : jarList) {
            JarEntry jarEntry = jar.jarFile.getJarEntry(name);
            if (jarEntry != null) {
                allResources.add(new URL(JAR_URL_PREFIX + jar.sourceFile.getAbsolutePath() + "!/" + name));
            }
        }
        final Iterator<URL> iterator = allResources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    @Override
    public URL getResource(String name) {
        List<Jar> jarList = getAllJars();
        for (Jar jar : jarList) {
            JarEntry jarEntry = jar.jarFile.getJarEntry(name);
            if (jarEntry != null) {
                try {
                    return new URL(JAR_URL_PREFIX + jar.sourceFile.getAbsolutePath() + "!/" + name);
                } catch (MalformedURLException e) {
                    log.error("get resource error", e);
                }
            }
        }
        return null;
    }

    private static byte[] getClassBytes(Jar jar, String path) throws IOException {
        URL classFileUrl = new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + path);
        byte[] data;
        try (final BufferedInputStream is = new BufferedInputStream(classFileUrl.openStream());
             final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int ch;
            while ((ch = is.read()) != -1) {
                baos.write(ch);
            }
            data = baos.toByteArray();
        }
        return data;
    }

    private static byte[] getClassBytes2(Jar jar, String path, JarEntry jarEntry) throws IOException {
        byte[] data;
        try (BufferedInputStream in = new BufferedInputStream(jar.jarFile.getInputStream(jarEntry));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int ch;
            while ((ch = in.read()) != -1) {
                out.write(ch);
            }
            data = out.toByteArray();
        }
        return data;
    }

    private List<Jar> getAllJars() {
        if (Objects.isNull(allJars)) {
            jarScanLock.lock();
            try {
                if (Objects.isNull(allJars)) {
                    allJars = doGetJars();
                }
            } finally {
                jarScanLock.unlock();
            }
        }
        return allJars;
    }

    private List<Jar> doGetJars() {
        List<Jar> jars = new LinkedList<>();
        // d:/xx/plugins
        for (File path : classpath) {
            if (path.exists() && path.isDirectory()) {
                String[] jarFileNames = path.list((dir, name) -> name.endsWith(".jar"));
                if (ArrayUtil.isEmpty(jarFileNames)) {
                    continue;
                }
                for (String jarFileName : jarFileNames) {
                    try {
                        File file = new File(path, jarFileName);
                        Jar jar = new Jar(new JarFile(file), file);
                        jars.add(jar);
                        log.info("{} jar loaded", file);
                    } catch (IOException e) {
                        log.error("{} jar file can‘t resolved", jarFileName);
                    }

                }
            }
        }
        return jars;
    }

    @RequiredArgsConstructor
    private static class Jar {
        private final JarFile jarFile;
        private final File sourceFile;
    }
}

package com.ryf.apm.agent.core.boot;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2024/3/30
 */
@Slf4j
public class AgentPackagePath {
    /**
     * agent.jar所在目录
     */
    private static File AGENT_PACKAGE_PATH;

    public static File getPath() {
        if (AGENT_PACKAGE_PATH == null) {
            AGENT_PACKAGE_PATH = findPath();
        }
        return AGENT_PACKAGE_PATH;
    }

    public static boolean isPathFound() {
        return AGENT_PACKAGE_PATH != null;
    }

    private static File findPath() {
        // com/ryf/apm.agent/core/boot/AgentPackagePath.class
        String classResourcePath = AgentPackagePath.class.getName().replaceAll("\\.", "/") + ".class";
        // file:/D:/work/apm/agent/target/classes/com/ryf/apm.agent/core/boot/AgentPackagePath.class
        // jar:file:/D:/work/apm/agent/target/apm-agent-1.0.0-SNAPSHOT.jar!/com/ryf/apm.agent/core/boot/AgentPackagePath.class
        URL resource = AgentPackagePath.class.getClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String urlString = resource.toString();

            log.debug("The beacon class location is {}.", urlString);

            int insidePathIndex = urlString.indexOf('!');
            boolean isInJar = insidePathIndex > -1;

            if (isInJar) {
                // /D:/work/apm/agent/target/apm-agent-1.0.0-SNAPSHOT.jar
                urlString = urlString.substring(urlString.indexOf("file:"), insidePathIndex);
                File agentJarFile = null;
                try {
                    agentJarFile = new File(new URL(urlString).toURI());
                } catch (MalformedURLException | URISyntaxException e) {
                    log.error("Can not locate agent jar file by url:" + urlString, e);
                }
                if (agentJarFile != null && agentJarFile.exists()) {
                    return agentJarFile.getParentFile();
                }
            } else {
                int prefixLength = "file:".length();
                String classLocation = urlString.substring(
                        prefixLength, urlString.length() - classResourcePath.length());
                return new File(classLocation);
            }
        }

        log.error("Can not locate agent jar file.");
        throw new RuntimeException("Can not locate agent jar file.");
    }

}

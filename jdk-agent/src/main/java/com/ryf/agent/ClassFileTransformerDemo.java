package com.ryf.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description
 * @date 2023/10/29
 */
@Slf4j
public class ClassFileTransformerDemo implements ClassFileTransformer {

    private static final String CUSTOM_CLASS_NAME = "com/ryf/app/service/impl/HelloServiceImpl";

    /**
     * 当某个类将要被加载时，会进入该方法
     * @param loader 类加载器
     * @param className 将要被加载的类的全限定类目，如：com.ryf.app.controller.HelloController
     * @return 需求增强，就返回增强后的字节码，不需要增强，就返回null
     *
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (CUSTOM_CLASS_NAME.equals(className)) {
            ClassPool classPool = ClassPool.getDefault();
            log.info("transform start");
            byte[] byteCode;
            try {
                CtClass ctClass = classPool.get("com.ryf.app.service.impl.HelloServiceImpl");
                CtMethod ctMethod = ctClass.getDeclaredMethod("hello", new CtClass[]{classPool.getCtClass("java.lang.String")});
                ctMethod.insertBefore("System.out.println(\"say hello before\");");
                // 获取修改后的字节码
                byteCode = ctClass.toBytecode();
                log.info("transform class：{} success", className);
                return byteCode;
            } catch (Exception e) {
                log.error("transform error", e);
            }
        }
        return new byte[0];
    }
}

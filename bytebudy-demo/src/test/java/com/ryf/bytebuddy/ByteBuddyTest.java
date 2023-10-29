package com.ryf.bytebuddy;

import cn.hutool.core.io.FileUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.*;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description bytebuddy测试类
 * @date 2023/10/26
 */
public class ByteBuddyTest {

    private String path;

    @BeforeEach
    public void init() {
        path = Objects.requireNonNull(ByteBuddyTest.class.getClassLoader().getResource("")).getPath();
    }


    @Test
    public void testGenerateClass() throws IOException {
        NamingStrategy.Suffixing suffixing = new NamingStrategy.Suffixing("ryf");
        try (DynamicType.Unloaded<UserManager> unloaded = new ByteBuddy()
                // 指定命名规则
                .with(suffixing)
                // 指定父类
                .subclass(UserManager.class)
                // 指定具体类名
                .name("com.ryf.UserManagerTest")
                .make()) {
            // unload标识生成的字节码还未加载到jvm
            byte[] bytes = unloaded.getBytes();
            unloaded.saveIn(new File(path));
            // 把生成的字节码注入到某个jar
            unloaded.inject(new File(""));
        }
    }


    /**
     * 对实例方法插桩
     */
    @Test
    public void testInsertion() throws InstantiationException, IllegalAccessException {
        try (DynamicType.Unloaded<UserManager> unloaded = new ByteBuddy()
                .subclass(UserManager.class)
                .name("a.b.UserManagerByteBuddy")
                // 通过方法名进行拦截
                .method(named("toString"))
                // 指定拦截到方法后该如何处理
                .intercept(FixedValue.value("Hello ByteBuddy"))
                .make()) {
            // 加载字节码到jvm
            DynamicType.Loaded<UserManager> load = unloaded.load(getClass().getClassLoader());
            Class<? extends UserManager> loaded = load.getLoaded();
            System.out.println(loaded.getClassLoader());
            UserManager userManager = loaded.newInstance();
            System.out.println(userManager);
        }
    }

    @Test
    public void test() throws InstantiationException, IllegalAccessException, IOException {
        try (DynamicType.Unloaded<UserManager> unloaded = new ByteBuddy()
                .rebase(UserManager.class)
                .name("a.b.UserManagerByteBuddy")
                .method(named("selectUserName").and(ElementMatchers.returns(TypeDescription.STRING)))
                .intercept(FixedValue.nullValue())
                .make()) {
            // DynamicType.Loaded<UserManager> load = unloaded.load(getClass().getClassLoader());
            // UserManager userManager = load.getLoaded().newInstance();
            // System.out.println(userManager.selectUserName(1));
            unloaded.saveIn(new File(path));
        }
    }


    /**
     * 插入新方法
     *
     * @throws Exception ex
     */
    @Test
    public void insertMethodTest() throws Exception {
        try (DynamicType.Unloaded<UserManager> unloaded = new ByteBuddy()
                .redefine(UserManager.class)
                .name("a.b.UserManagerByteBuddy")
                // 定义新方法,方法名，返回值类型，修饰符
                .defineMethod("selectUserName2", String.class, Modifier.PUBLIC + Modifier.STATIC)
                .withParameters(Long.class, String.class)
                .intercept(FixedValue.value("byte buddy generate new method "))
                .make()) {
            unloaded.saveIn(new File(path));
        }
    }

    /**
     * 插入新属性
     *
     * @throws Exception ex
     */
    @Test
    public void insertParameterTest() throws Exception {
        try (DynamicType.Unloaded<UserManager> unloaded = new ByteBuddy()
                .redefine(UserManager.class)
                .name("a.b.UserManagerByteBuddy")
                // 定义新属性
                .defineField("age", int.class, Modifier.PRIVATE)
                // 指定age所在的操作方法
                .implement(UserDao.class)
                // 生成接口实现
                .intercept(FieldAccessor.ofField("age"))
                .make()) {
            unloaded.saveIn(new File(path));
        }
    }

    /**
     * 方法委托
     *
     * @throws Exception ex
     */
    @Test
    public void methodDelegateTest() throws Exception {
        try (DynamicType.Unloaded<UserManager> unloaded = new ByteBuddy()
                .subclass(UserManager.class)
                .name("a.b.UserManagerByteBuddy")
                // 拦截制定方法
                .method(named("selectUserName"))
                // 委托给同签名的静态方法执行，传入参数为class时
                // .intercept(MethodDelegation.to(UserManager1.class))
                // 传入对象，即UserManager.selectUserName，委托给UserManager2执行对应逻辑
                // .intercept(MethodDelegation.to(new UserManager2()))
                // 通过注解增强调用
                .intercept(MethodDelegation.to(new UserManager3()))
                .make()) {
            DynamicType.Loaded<UserManager> load = unloaded.load(getClass().getClassLoader());
            Class<? extends UserManager> loaded = load.getLoaded();
            UserManager userManager = loaded.newInstance();
            System.out.println("result= " + userManager.selectUserName(100));
        }
    }

    /**
     * 动态修改参数
     * 1.自定义MyCallable接口
     * 2.使用@Morph替代@SuperCall
     * 3.指定拦截器需先调用withBinders
     *
     * @throws Exception ex
     */
    @Test
    public void dynamicModifyParam() throws Exception {
        try (DynamicType.Unloaded<UserManager> unloaded = new ByteBuddy()
                .subclass(UserManager.class)
                .name("a.b.UserManagerByteBuddy")
                // 拦截制定方法
                .method(named("selectUserName"))
                .intercept(MethodDelegation
                        .withDefaultConfiguration()
                        .withBinders(
                                // 在UserManager4中使用MyCallable之前需要bytebuddy
                                // 参数的类型是什么 -> MyCallable
                                Morph.Binder.install(MyCallable.class)
                        )
                        .to(new UserManager4()))
                .make()) {
            DynamicType.Loaded<UserManager> load = unloaded.load(getClass().getClassLoader());
            Class<? extends UserManager> loaded = load.getLoaded();
            UserManager userManager = loaded.newInstance();
            System.out.println("result= " + userManager.selectUserName(100));
        }
    }


    /**
     * 构造方法插桩
     *
     * @throws Exception ex
     */
    @Test
    public void constructorTest() throws Exception {
        try (DynamicType.Unloaded<UserManager> unloaded = new ByteBuddy()
                .subclass(UserManager.class)
                .name("a.b.UserManagerByteBuddy")
                .constructor(any())
                .intercept(
                        // 指定在构造方法执行完成后委托给UserManager4
                        SuperMethodCall.INSTANCE.andThen(
                                MethodDelegation.to(new UserManager4())
                        )
                )
                .make()) {
            DynamicType.Loaded<UserManager> load = unloaded.load(getClass().getClassLoader());
            Class<? extends UserManager> loaded = load.getLoaded();
            UserManager userManager = loaded.newInstance();
            System.out.println("result= " + userManager.selectUserName(100));
        }
    }

    @Test
    public void staticMethodTest() throws Exception {
        try (DynamicType.Unloaded<FileUtil> unloaded = new ByteBuddy()
                .rebase(FileUtil.class)
                .name("a.b.UserManagerByteBuddy")
                .method(ElementMatchers.named("size").and(isStatic()))
                .intercept(MethodDelegation.to(new UserManager5()))
                .make()) {
            DynamicType.Loaded<FileUtil> load = unloaded.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.CHILD_FIRST);
            Class<? extends FileUtil> loaded = load.getLoaded();
            Method method = loaded.getMethod("size", File.class);
            // 静态方法
            System.out.println(method.invoke(null, new File("E:\\test\\pdf\\test.pdf")));
            unloaded.saveIn(new File(path));
        }
    }


    /**
     * 动态加载类,from jar
     *
     * @throws Exception ex
     */
    @Test
    public void dynamicLoadClass1() throws Exception {

        try (
                // 从指定jar包加载
                ClassFileLocator jarFileLocator1 = ClassFileLocator.ForJarFile.of(new File("E:\\environemnt\\maven\\repository\\org\\springframework\\spring-beans\\6.0.13\\spring-beans-6.0.13.jar"));
                ClassFileLocator jarFileLocator2 = ClassFileLocator.ForJarFile.of(new File("E:\\environemnt\\maven\\repository\\org\\springframework\\spring-core\\6.0.13\\spring-core-6.0.13.jar"));
                // 系统类加载器
                ClassFileLocator systemLoader = ClassFileLocator.ForClassLoader.ofSystemLoader()) {
            ClassFileLocator.Compound compound = new ClassFileLocator.Compound(jarFileLocator1, jarFileLocator2, systemLoader);
            TypePool typePool = TypePool.Default.of(compound);
            // 写入全类名获取 TypeDescription，不会触发类加载
            TypeDescription typeDescription = typePool.describe("org.springframework.beans.factory.support.RootBeanDefinition").resolve();
            //
            try (DynamicType.Unloaded<Object> unloaded = new ByteBuddy()
                    .redefine(typeDescription, compound)
                    .method(named("getTargetType"))
                    .intercept(FixedValue.nullValue())
                    .make()) {
                unloaded.saveIn(new File(path));
            }
        }
    }

    @Test
    public void dynamicLoadClass2() throws Exception {
        try (
                // 从指定目录下加载
                ClassFileLocator fileLocator = new ClassFileLocator.ForFolder(new File("E:\\code\\study\\spring-study\\target\\classes"));
                // 系统类加载器
                ClassFileLocator systemLoader = ClassFileLocator.ForClassLoader.ofSystemLoader()) {
            ClassFileLocator.Compound compound = new ClassFileLocator.Compound(fileLocator, systemLoader);
            TypePool typePool = TypePool.Default.of(compound);
            // 写入全类名获取 TypeDescription，不会触发类加载
            TypeDescription typeDescription = typePool.describe("com.ryf.Main").resolve();
            //
            try (DynamicType.Unloaded<Object> unloaded = new ByteBuddy()
                    .redefine(typeDescription, compound)
                    .method(named("uuid"))
                    .intercept(FixedValue.value("hello you have been intercepted"))
                    .make()) {
                unloaded.saveIn(new File(path));
            }
        }
    }


    /**
     * 清空方法体
     *
     * @throws Exception ex
     */
    @Test
    public void cleanMethodBody() throws Exception {
        try (DynamicType.Unloaded<UserManager> unloaded = new ByteBuddy()
                .redefine(UserManager.class)
                .method(ElementMatchers.any().and(isDeclaredBy(UserManager.class)))
                .intercept(StubMethod.INSTANCE)
                .make()) {
            unloaded.saveIn(new File(path));
        }
    }

    @Test
    public void fooTest() throws Exception {
        Foo dynamicFoo = new ByteBuddy()
                .subclass(Foo.class)
                .method(ElementMatchers.isDeclaredBy(Foo.class)).intercept(FixedValue.value("One!"))
                .method(named("foo")).intercept(FixedValue.value("Two!"))
                .method(named("foo").and(takesArguments(1))).intercept(FixedValue.value("Three!"))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .newInstance();
        System.out.println(dynamicFoo);
    }

    class Foo {
        public String bar() {
            return null;
        }

        public String foo() {
            return null;
        }

        public String foo(Object o) {
            return null;
        }
    }
}

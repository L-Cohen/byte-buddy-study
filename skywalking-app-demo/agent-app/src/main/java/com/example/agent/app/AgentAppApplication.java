package com.example.agent.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * -javaagent:E:\code\study\bytebuddy-study\skywalking-app-demo\spring-mvc-standalone-plugin\target\spring-mvc-standalone-plugin-1.0-SNAPSHOT-jar-with-dependencies.jar
 * -javaagent:E:\code\study\bytebuddy-study\skywalking-app-demo\mysql-standalone-plugin\target\mysql-standalone-plugin-1.0-SNAPSHOT-jar-with-dependencies.jar
 * -javaagent:E:\code\study\bytebuddy-study\skywalking-app-demo\apm-sniffer\skywalking-app-demo-dist\apm-agent-1.0-SNAPSHOT-jar-with-dependencies.jar
 * @author ryf
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(value = "com.example.agent.app.mapper")
public class AgentAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentAppApplication.class, args);
    }

}

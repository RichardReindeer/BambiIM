package com.bambi.imclient;

import com.bambi.imclient.controller.CommandController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

/**
 * 启动类
 */
@Configuration
/*@EnableAutoConfiguration
@ComponentScan("com.bambi.imclient")*/
@SpringBootApplication
public class ImClientApplication {

    /**
     * 1. 启动并初始化 Spring 环境以及其Spring组件
     *
     * @param args
     */
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ImClientApplication.class, args);

        // todo 根据传入参数判断启动另外的几个测试项目
        startSingleClient(context);
    }

    /**
     * 启动单体客户端
     */
    private static void startSingleClient(ApplicationContext context) {
        CommandController commandController = context.getBean(CommandController.class);
        commandController.initCommandMap();

        try {
            commandController.commandTreadRunning();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

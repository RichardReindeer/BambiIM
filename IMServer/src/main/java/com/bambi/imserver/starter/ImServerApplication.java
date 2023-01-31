package com.bambi.imserver.starter;

import com.bambi.imserver.server.ChatNettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.bambi.imserver")
@SpringBootApplication
public class ImServerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ImServerApplication.class, args);

        startChatServer(context);
    }

    /**
     * 启动聊天服务器
     * 根据正文类获取注册到Spring中的Bean对象
     * @param context
     */
    private static void startChatServer(ApplicationContext context) {
        ChatNettyServer bean = context.getBean(ChatNettyServer.class);
        bean.runServer();
    }

}

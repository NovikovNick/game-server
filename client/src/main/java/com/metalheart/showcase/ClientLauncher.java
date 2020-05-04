package com.metalheart.showcase;

import com.metalheart.client.GameClient;
import com.metalheart.configuration.GameConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ClientLauncher {

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(GameConfiguration.class);
        GameClient gameClient = (GameClient) context.getBean("gameClient");
        gameClient.startReceiving("192.168.0.102", 7777);
    }
}

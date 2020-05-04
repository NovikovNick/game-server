package com.metalheart;

import com.metalheart.configuration.GameConfiguration;
import com.metalheart.server.GameServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ServerLauncher {

    public static void main(String[] args) throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(GameConfiguration.class);
        GameServer gameServer = (GameServer) context.getBean("gameServer");
        gameServer.run();
    }
}

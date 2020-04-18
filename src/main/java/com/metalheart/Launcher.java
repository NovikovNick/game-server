package com.metalheart;

import com.metalheart.configuration.GameConfiguration;
import com.metalheart.server.NettyAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Launcher {

    public static void main(String[] args) throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(GameConfiguration.class);
        NettyAdapter nettyAdapter = (NettyAdapter) context.getBean("nettyAdapter");

        nettyAdapter.run();
    }
}

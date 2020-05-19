package com.metalheart;

import com.metalheart.configuration.GameConfiguration;
import com.metalheart.server.GameServer;
import com.metalheart.service.ServerVisualizer;
import com.metalheart.service.imp.CanvasServiceImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerLauncher extends Application {

    public static void main(String[] args) {
        launch(ServerLauncher.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(GameConfiguration.class);
        ServerVisualizer mazeShowcase = (ServerVisualizer) context.getBean("serverVisualizer");
        CanvasServiceImpl canvasService = (CanvasServiceImpl) context.getBean("canvasServiceImpl");
        GameServer gameServer = (GameServer) context.getBean("gameServer");

        Scene scene = canvasService.createScene();

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);
        primaryStage.show();


        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.execute(() -> {
            try {
                gameServer.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        pool.execute(() -> {
            try {
                mazeShowcase.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

package com.metalheart.showcase.launcher;

import com.metalheart.configuration.GameConfiguration;
import com.metalheart.service.CanvasService;
import com.metalheart.service.showcase.CollisionShowcase;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CollistionShowcaseLauncher extends Application {

    public static void main(String[] args) {
        launch(CollistionShowcaseLauncher.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(GameConfiguration.class);
        CollisionShowcase collistionShowcase = (CollisionShowcase) context.getBean("collisionShowcase");
        CanvasService canvasService = (CanvasService) context.getBean("canvasService");

        Scene scene = canvasService.createScene();

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);
        primaryStage.show();

        collistionShowcase.start();
    }
}
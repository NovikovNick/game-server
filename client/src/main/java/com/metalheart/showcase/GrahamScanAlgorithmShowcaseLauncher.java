package com.metalheart.showcase;

import com.metalheart.configuration.GameConfiguration;
import com.metalheart.service.CanvasService;
import com.metalheart.service.GrahamScanAlgorithmShowcase;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GrahamScanAlgorithmShowcaseLauncher extends Application {

    public static void main(String[] args) {
        launch(GrahamScanAlgorithmShowcaseLauncher.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(GameConfiguration.class);
        GrahamScanAlgorithmShowcase showcase = (GrahamScanAlgorithmShowcase) context.getBean("grahamScanAlgorithmShowcase");
        CanvasService canvasService = (CanvasService) context.getBean("canvasService");

        Scene scene = canvasService.createScene();

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);
        primaryStage.show();

        showcase.start();
    }
}
package com.metalheart.showcase.launcher;

import com.metalheart.configuration.GameConfiguration;
import com.metalheart.service.Scene3DService;
import com.metalheart.service.showcase.CameraShowcase;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Camera3DShowcaseLauncher extends Application {

    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        launch(Camera3DShowcaseLauncher.class, args);
    }

    @Override
    public void start(Stage stage) {

        ApplicationContext context = new AnnotationConfigApplicationContext(GameConfiguration.class);
        CameraShowcase showcase = (CameraShowcase) context.getBean("cameraShowcase");
        Scene3DService scene3DService = (Scene3DService) context.getBean("scene3DServiceImpl");

        Scene scene = scene3DService.createScene(showcase.initControls());

        stage.setScene(scene);
        stage.setFullScreen(true);

        stage.show();
        showcase.start();
    }
}

package com.metalheart.showcase.launcher;

import com.metalheart.configuration.GameConfiguration;
import com.metalheart.service.Scene3DService;
import com.metalheart.service.showcase.Terrain3dShowcase;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Terrain3DShowcaseLauncher extends Application {

    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        launch(Terrain3DShowcaseLauncher.class, args);
    }

    @Override
    public void start(Stage stage) {

        ApplicationContext context = new AnnotationConfigApplicationContext(GameConfiguration.class);
        Terrain3dShowcase showcase = (Terrain3dShowcase) context.getBean("terrain3dShowcase");
        Scene3DService scene3DService = (Scene3DService) context.getBean("scene3DServiceImpl");

        Scene scene = scene3DService.createScene(showcase.initControls());

        stage.setScene(scene);
        stage.setFullScreen(true);

        stage.show();
        showcase.start();
    }
}
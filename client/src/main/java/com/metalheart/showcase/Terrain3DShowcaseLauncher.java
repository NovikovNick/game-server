package com.metalheart.showcase;

import com.metalheart.configuration.GameConfiguration;
import com.metalheart.model.physic.Vector3d;
import com.metalheart.service.Scene3DService;
import com.metalheart.service.TerrainService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

public class Terrain3DShowcaseLauncher extends Application {

    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        launch(Terrain3DShowcaseLauncher.class, args);
    }

    @Override
    public void start(Stage stage) {
        ApplicationContext context = new AnnotationConfigApplicationContext(GameConfiguration.class);
        //MazeShowcase mazeShowcase = (MazeShowcase) context.getBean("mazeShowcase");
        TerrainService terrainService = (TerrainService) context.getBean("terrainServiceImpl");
        Scene3DService scene3DService = (Scene3DService) context.getBean("scene3DServiceImpl");

        List<Vector3d> data = terrainService.generateMaze().stream()
                .flatMap(terrainChunk -> terrainChunk.getWallsCoords().stream())
                .collect(Collectors.toList());

        Scene scene = scene3DService.createScene(data);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
package com.metalheart.service;

import com.metalheart.model.logic.Player;
import com.metalheart.model.logic.State;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.model.physic.Vector3d;
import com.metalheart.service.visial.Scene2dService;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServerVisualizer extends AnimationTimer {

    @Autowired
    private Scene2dService canvasService;

    @Autowired
    private GameLogicService gameStateService;

    @Override
    public void handle(long now) {

        canvasService.clear(Color.BLACK);

        State state = gameStateService.getState();

        List<Polygon2d> polygons = state.getTerrainChunks().stream()
                .flatMap(chunk -> chunk.getWalls().stream())
                .map(wall -> wall.getRigidBody().getShape())
                .collect(Collectors.toList());

        for (Polygon2d polygon2d : polygons) {
            canvasService.draw(polygon2d, Color.WHITE);
        }

        for (Player player : state.getPlayers().values()) {

            Polygon2d shape = player.getRigidBody().getShape();
            Vector3d position = player.getTransform().getPosition();

            canvasService.draw(shape.withOffset(position), Color.RED);
        }
    }
}

package com.metalheart.service;

import com.metalheart.model.Force;
import com.metalheart.model.ShowcaseObject;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.repository.MazeRepository;
import com.metalheart.repository.PlayerRepository;
import com.metalheart.service.maze.Maze;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MazeShowcase extends AnimationTimer {

    @Autowired
    PlayerInputService playerInputService;

    @Autowired
    ShowcaseService showcaseService;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    MazeRepository mazeRepository;

    @Autowired
    MazeAlgorithmVisualiser mazeAlgorithmVisualiser;

    @Autowired
    TerrainService terrainService;

    @Autowired
    CanvasService canvasService;
    private AtomicLong sequenceNumber = new AtomicLong(0);
    private Long previousAnimationAt;

    @Override
    public void handle(long now) {

        // maze visualization

        List<Polygon2d> walls;
        if (sequenceNumber.incrementAndGet() % 5 == 0) {
            Maze maze = mazeAlgorithmVisualiser.step();
            walls = canvasService.toShowcasePolygons(terrainService.build(maze));
            mazeRepository.save(walls);
        }


        // player input and physics

        Point2d mousePosition = playerInputService.getMousePosition();
        Force inputForce = playerInputService.getInputForce();
        float dt = getDeltaTime(now);

        ShowcaseObject player = playerRepository.get();
        player = showcaseService.rotateTo(player, mousePosition);

        ShowcaseObject newPosition = showcaseService.translate(player, inputForce, dt);

        boolean isIntersected = false;
        for (int i = 0; i < mazeRepository.get().size(); i++) {
            if (isIntersected = PhysicUtil.isIntersect(mazeRepository.get().get(i), newPosition.getData())) {
                break;
            }
        }

        if (!isIntersected) {
            player = newPosition;
            playerRepository.save(newPosition);
        }


        // render

        GraphicsContext gc = canvasService.getGraphicsContext();
        canvasService.clear();

        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);

        for (int i = 0; i < mazeRepository.get().size(); i++) {

            Polygon2d polygon2d = mazeRepository.get().get(i);
            Point2d p = polygon2d.getPoints().get(0);

            final float x = p.getX() + 16;
            final float y = p.getY() + 17;

            boolean isActive = false;
            if (!mazeAlgorithmVisualiser.getMaze().getBuildPath().isEmpty()) {
                Point2d active = mazeAlgorithmVisualiser.getMaze().getBuildPath().peek();
                isActive =
                        (x > (active.getX() * 5 - 1)) && (x < (active.getX() * 5 + 4))
                                &&
                                (y > (active.getY() * 5 - 1)) && (y < (active.getY() * 5 + 4));
            }

            canvasService.draw(polygon2d, gc, isActive);
        }
        canvasService.draw(player.getData(), gc, isIntersected);


        {// debug
            Point2d center = PhysicUtil.getCenter(player.getData());
            Point2d c = CanvasService.toLocalCoord(center);
            Point2d m = mousePosition;

            gc.setStroke(Color.RED);
            gc.strokeLine(c.getX(), c.getY(), m.getX(), m.getY());
        }
    }

    private float getDeltaTime(long now) {
        float timeDelta;
        if (previousAnimationAt == null) {
            timeDelta = 0.0015f;
        } else {
            timeDelta = (now - previousAnimationAt) / 1000000000f;
        }
        previousAnimationAt = now;
        return timeDelta;
    }
}

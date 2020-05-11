package com.metalheart.service;

import com.metalheart.model.physic.Force;
import com.metalheart.model.ShowcaseObject;
import com.metalheart.model.TerrainChunk;
import com.metalheart.model.Vector3;
import com.metalheart.model.physic.CollisionResult;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.repository.MazeRepository;
import com.metalheart.repository.PlayerRepository;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.metalheart.service.CanvasService.toLocalCoord;

@Component
public class MazeOptimizerShowcase extends AnimationTimer {

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


    List<Polygon2d>walls;

    @Override
    public void handle(long now) {

        // maze visualization

        if (walls == null) {
            Set<TerrainChunk> chunks = terrainService.generateMaze();
            //TerrainChunk chunk = chunks.stream().findFirst().get();
            walls = canvasService.toShowcaseOptimizedPolygons(chunks);
        }


        // player input and physics

        Point2d mousePosition = playerInputService.getMousePosition();
        Force inputForce = playerInputService.getInputForce();
        float dt = getDeltaTime(now);

        ShowcaseObject player = playerRepository.get();
        player = showcaseService.rotateTo(player, mousePosition);

        ShowcaseObject newPosition = showcaseService.translate(player, inputForce, dt);

        CollisionResult result = CollisionResult.builder().collide(false).build();
        for (Polygon2d polygon2d : walls) {
            CollisionResult r = PhysicUtil.detectCollision(polygon2d, newPosition.getData());
            if (r.isCollide()) {
                newPosition = showcaseService.translate(newPosition, new Force(r.getNormal(), r.getDepth()), 1);
                result = r;
            }
        }

        player = newPosition;
        playerRepository.save(newPosition);


        // render

        GraphicsContext gc = canvasService.getGraphicsContext();
        canvasService.clear();

        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);

        for (Polygon2d polygon : walls) {
            canvasService.draw(polygon, gc, false);
        }
        canvasService.draw(player.getData(), gc, result.isCollide());


        {// debug
            Point2d center = PhysicUtil.getCenter(player.getData());
            Point2d c = toLocalCoord(center);
            Point2d m = mousePosition;

            gc.setStroke(Color.RED);
            gc.strokeLine(c.getD0(), c.getD1(), m.getD0(), m.getD1());

            if (result.isCollide()) {
                Vector3 n = result.getNormal();
                Point2d p1 = toLocalCoord(result.getP1());
                Point2d p2 = toLocalCoord(result.getP2());

                gc.setStroke(Color.BLUE);
                gc.strokeLine(p1.getD0(), p1.getD1(), p2.getD0(), p2.getD1());

                gc.setFill(Color.BLUE);
                gc.fillOval(
                        p1.getD0() - 3,
                        p1.getD1() - 3,
                        6,
                        6
                );

                gc.setFill(Color.RED);
                gc.fillOval(
                        p2.getD0() - 3,
                        p2.getD1() - 3,
                        6,
                        6
                );
            }

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

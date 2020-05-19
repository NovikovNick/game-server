package com.metalheart.service;

import com.metalheart.math.PhysicUtil;
import com.metalheart.model.ShowcaseObject;
import com.metalheart.model.physic.*;
import com.metalheart.repository.MazeRepository;
import com.metalheart.repository.PlayerRepository;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.metalheart.service.CanvasService.toLocalCoord;
import static java.util.Arrays.asList;

@Component
public class CollisionShowcase extends AnimationTimer {

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


    List<Polygon2d> polygons;

    @Override
    public void handle(long now) {

        // maze visualization

        if (polygons == null) {
            List<Point2d> points = asList(
                    new Point2d(-0.5f, 0.5f),
                    new Point2d(-1.5f, 0.5f),
                    new Point2d(-1.5f, -0.5f),
                    new Point2d(-0.5f, -0.5f),

                    new Point2d(-1.5f, 0.5f),
                    new Point2d(-2.5f, 0.5f),
                    new Point2d(-2.5f, -0.5f),
                    new Point2d(-1.5f, -0.5f),

                    new Point2d(-2.5f, 0.5f),
                    new Point2d(-3.5f, 0.5f),
                    new Point2d(-3.5f, -0.5f),
                    new Point2d(-2.5f, -0.5f)
            );

            polygons = asList(PhysicUtil.grahamScan(points));
        }




        /*{// rotation
            sequenceNumber.incrementAndGet();
            List<Point2d> points = new ArrayList<>(polygons.get(0).getPoints());

            for (int i = 0; i < points.size() - 1; i++) {
                Point2d p0 = points.get(i);
                Point2d p1 = points.get(i + 1);
                points.set(i + 1, PhysicUtil.rotate(p1, (float) Math.toRadians(1)));
            }
            polygons = asList(PhysicUtil.grahamScan(points));
        }*/

        // player input and physics

        Point2d mousePosition = playerInputService.getMousePosition();
        Force inputForce = playerInputService.getInputForce();
        float dt = getDeltaTime(now);

        ShowcaseObject player = playerRepository.get();
        player = showcaseService.rotateTo(player, mousePosition);

        ShowcaseObject newPosition = showcaseService.translate(player, inputForce, dt);

        CollisionResult result = CollisionResult.builder().collide(false).build();
        for (Polygon2d polygon2d : polygons) {
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

        for (Polygon2d polygon : polygons) {
            canvasService.draw(polygon, gc, false);
        }
        canvasService.draw(player.getData(), gc, result.isCollide());


        { //debug
            Point2d center = PhysicUtil.getCenter(player.getData());
            Point2d c = toLocalCoord(center);
            Point2d m = mousePosition;

            gc.setStroke(Color.RED);
            gc.strokeLine(c.getD0(), c.getD1(), m.getD0(), m.getD1());

            if (result.isCollide()) {
                Vector3d n = result.getNormal();
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

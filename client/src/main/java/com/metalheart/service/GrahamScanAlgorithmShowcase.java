package com.metalheart.service;

import com.metalheart.algorithm.GrahamScanAlgorithm;
import com.metalheart.model.physic.Point2d;
import com.metalheart.repository.MazeRepository;
import com.metalheart.repository.PlayerRepository;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Component
public class GrahamScanAlgorithmShowcase extends AnimationTimer {

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


    private GrahamScanAlgorithm algorithm;

    @Override
    public void handle(long now) {

        Random random = new Random();

        if (algorithm == null) {
            List<Point2d> points = IntStream.range(0, 20).boxed()
                    .map(i -> new Point2d(random.nextInt(50) - 25, random.nextInt(30) - 15))
                    .collect(toList());
            algorithm = new GrahamScanAlgorithm(points);
        }

        if (sequenceNumber.incrementAndGet() % 1 == 0) {
            algorithm.step();
        }

        GraphicsContext gc = canvasService.getGraphicsContext();
        canvasService.clear();

        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);

        for (Point2d p : algorithm.getPoints()) {
            canvasService.draw(p, Color.WHITE);
        }


        List<Point2d> points = algorithm.getStack().stream().collect(toList());


        for (int i = 0; i < points.size() - 1; i++) {

            final Point2d p0 = points.get(i);
            final Point2d p1 = points.get(i + 1);

            canvasService.draw(p0, p1, Color.YELLOW);

            canvasService.draw(p0, Color.GREEN);
            canvasService.draw(p1, Color.GREEN);
        }


        if (algorithm.getStack().peek() != null) {
            canvasService.draw(algorithm.getStack().peek(), Color.RED);

            if (algorithm.getI() < algorithm.getPoints().size()) {

                canvasService.draw(algorithm.getPoints().get(algorithm.getI()), Color.BLUE);
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

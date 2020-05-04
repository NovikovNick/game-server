package com.metalheart.showcase;

import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.service.PhysicUtil;
import com.metalheart.service.TerrainService;
import com.metalheart.service.imp.TerrainServiceImpl;
import com.metalheart.service.maze.Maze;
import com.metalheart.showcase.model.Force;
import com.metalheart.showcase.model.ShowcaseObject;
import com.metalheart.showcase.repository.MazeRepository;
import com.metalheart.showcase.repository.PlayerRepository;
import com.metalheart.showcase.service.CanvasService;
import com.metalheart.showcase.service.MazeAlgorithmVisualiser;
import com.metalheart.showcase.service.PlayerInputService;
import com.metalheart.showcase.service.ShowcaseService;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


public class Physic2DShowcase extends Application {

    PlayerInputService playerInputService = new PlayerInputService();


    ShowcaseService showcaseService = new ShowcaseService();


    PlayerRepository playerRepository = new PlayerRepository();


    MazeRepository mazeRepository = new MazeRepository();


    MazeAlgorithmVisualiser mazeAlgorithmVisualiser = new MazeAlgorithmVisualiser();


    TerrainService terrainService = new TerrainServiceImpl();


    CanvasService canvasService = new CanvasService();


    private AnimationTimer timer = new AnimationTimer() {

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
    };

    public static void main(String[] args) {
        launch(Physic2DShowcase.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //generateMase();

        Scene scene = canvasService.createScene();

        scene.setOnKeyPressed(playerInputService.getKeyPressHandler());
        scene.setOnKeyReleased(playerInputService.getKeyReleaseHandler());

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);
        primaryStage.show();
        timer.start();
    }
}
package com.metalheart.showcase;

import com.metalheart.model.TerrainChunk;
import com.metalheart.model.Vector3;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.service.PhysicUtil;
import com.metalheart.service.TerrainService;
import com.metalheart.service.imp.TerrainServiceImpl;
import com.metalheart.service.maze.Maze;
import com.metalheart.service.maze.RecursiveBacktrackerMazeBuilder;
import com.metalheart.service.maze.MazeDoorDirection;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Physic2DShowcase extends Application {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    private static Canvas game = new Canvas(WIDTH, HEIGHT);

    private static final int UNIT = 30;
    private static final int SPEED = 15;

    private boolean wPressed;
    private boolean aPressed;
    private boolean sPressed;
    private boolean dPressed;


    private RecursiveBacktrackerMazeBuilder mazeBuilder = new RecursiveBacktrackerMazeBuilder()
            .setWidth(5)
            .setHeight(5)
            .setEnter(new Point2d(0, 0))
            .setEnterDirection(MazeDoorDirection.LEFT)
            .setExit(new Point2d(5, 4))
            .setExitDirection(MazeDoorDirection.RIGHT);
    private Maze maze = new Maze();


    private List<Polygon2d> walls = new ArrayList<>();
    private PolygonShape player;


    private AnimationTimer timer = new AnimationTimer() {

        private AtomicLong sequenceNumber = new AtomicLong(0);
        private Long previousAnimationAt;

        @Override
        public void handle(long now) {


            if (sequenceNumber.incrementAndGet() % 1 == 0) {
                generateMase();
            }

            Point2d mousePosition = getMousePosition();
            if (player == null) {

                player = new PolygonShape(new Polygon2d(
                        new Point2d(-0.5f, 0.5f),
                        new Point2d(0.5f, 0.5f),
                        new Point2d(0.5f, -0.5f),
                        new Point2d(-0.5f, -0.5f)
                ), mousePosition);
            }

            float dt = getDeltaTime(now);

            Polygon2d polygon = player.translate(getInputForce(), dt);

            int intersectedIndex = -1;
            boolean isIntersected = false;
            for (int i = 0; i < walls.size(); i++) {
                if (isIntersected = PhysicUtil.isIntersect(walls.get(i), polygon)) {
                    intersectedIndex = i;
                    break;
                }
            }

            if (!isIntersected) {
                player.setData(polygon);
            }

            player.lookAt(mousePosition);


            GraphicsContext gc = game.getGraphicsContext2D();

            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, WIDTH, HEIGHT);

            gc.setFill(Color.WHITE);
            gc.setStroke(Color.WHITE);

            for (int i = 0; i < walls.size(); i++) {

                Polygon2d polygon2d = walls.get(i);
                Point2d p = polygon2d.getPoints().get(0);

                final float x = p.getX() + 16;
                final float y = p.getY() + 17;

                boolean isActive = false;
                if(!maze.getBuildPath().isEmpty()) {
                    Point2d active = maze.getBuildPath().peek();
                    isActive =
                            (x > (active.getX() * 5 - 1)) && (x < (active.getX() * 5 + 4))
                                    &&
                                    (y > (active.getY() * 5 - 1)) && (y < (active.getY() * 5 + 4));
                }

                draw(polygon2d, gc, isActive);
            }
            draw(player.getData(), gc, isIntersected);


            {// debug
                Point2d center = PhysicUtil.getCenter(player.getData());
                Point2d c = toLocalCoord(center);
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

    private Point2d getMousePosition() {
        return new Point2d(
                MouseInfo.getPointerInfo().getLocation().x,
                MouseInfo.getPointerInfo().getLocation().y);
    }

    public static void main(String[] args) {
        launch(Physic2DShowcase.class, args);
    }

    private Force getInputForce() {
        Vector3 direction = new Vector3(0, 0, 0);

        if (!(wPressed || sPressed || aPressed || dPressed)) {
            return new Force(direction, 0);
        }

        if (wPressed) direction.setY(direction.getY() + 1);
        if (sPressed) direction.setY(direction.getY() - 1);

        if (aPressed) direction.setX(direction.getX() - 1);
        if (dPressed) direction.setX(direction.getX() + 1);

        return new Force(direction, SPEED);
    }

    private void draw(Polygon2d polygon, GraphicsContext gc, boolean active) {

        double[] xPoints = polygon.getPoints().stream().mapToDouble(Point2d::getX).map(Physic2DShowcase::toXCoord).toArray();
        double[] yPoints = polygon.getPoints().stream().mapToDouble(Point2d::getY).map(Physic2DShowcase::toYCoord).toArray();

        if (active) {
            gc.setFill(Color.RED);
            gc.fillPolygon(xPoints, yPoints, 4);
        }

        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);

        for (Point2d p : polygon.getPoints()) {
            gc.fillOval(
                    toXCoord(p.getX()) - 3,
                    toYCoord(p.getY()) - 3,
                    6,
                    6
            );
        }

        gc.strokePolygon(xPoints, yPoints, 4);
    }

    public static Double toYCoord(double y) {
        return HEIGHT / 2 - y * UNIT;
    }

    public static Double toXCoord(double x) {
        return WIDTH / 2 + x * UNIT;
    }

    public static Point2d toLocalCoord(Point2d p) {
        return new Point2d(
                WIDTH / 2 + p.getX() * UNIT,
                HEIGHT / 2 - p.getY() * UNIT
        );
    }

    public static Point2d toGlobalCoord(Point2d p) {
        return new Point2d(
                p.getX() / UNIT - WIDTH / 2,
                HEIGHT / 2 - p.getY() / UNIT
        );
    }


    private EventHandler<KeyEvent> keyPressHandler = e -> {
        switch (e.getCode()) {
            case W:
                wPressed = true;
                break;
            case A:
                aPressed = true;
                break;
            case S:
                sPressed = true;
                break;
            case D:
                dPressed = true;
                break;
        }
    };

    private EventHandler<KeyEvent> keyReleaseHandler = e -> {
        switch (e.getCode()) {
            case W:
                wPressed = false;
                break;
            case A:
                aPressed = false;
                break;
            case S:
                sPressed = false;
                break;
            case D:
                dPressed = false;
                break;
        }
    };

    @Override
    public void start(Stage primaryStage) throws Exception {

        generateMase();

        StackPane root = new StackPane();

        game.setOnKeyPressed(keyPressHandler);
        game.setOnKeyReleased(keyReleaseHandler);

        root.getChildren().addAll(game);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        scene.setOnKeyPressed(keyPressHandler);
        scene.setOnKeyReleased(keyReleaseHandler);

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);
        primaryStage.show();
        timer.start();
    }

    private void generateMase() {

        if (!mazeBuilder.isFinished(maze)) {
            maze = mazeBuilder.buildNextStep(maze);
        }

        TerrainService terrainService = new TerrainServiceImpl();

        List<Polygon2d> walls = new ArrayList<>();
        for (TerrainChunk chunk : terrainService.build(maze)) {
            Vector3 position = chunk.getPosition();
            for (Vector3 voxel : chunk.getChildren()) {
                if (voxel.getY() == 2) {
                    final float pX = position.getX();
                    final float pZ = position.getZ();
                    final float vX = voxel.getX();
                    final float vZ = voxel.getZ();


                    walls.add(new Polygon2d(
                            new Point2d(pX * 10 + vX - 0.5f - 16, pZ * 10 + vZ - 0.5f - 17),
                            new Point2d(pX * 10 + vX - 0.5f - 16, pZ * 10 + vZ + 0.5f - 17),
                            new Point2d(pX * 10 + vX + 0.5f - 16, pZ * 10 + vZ + 0.5f - 17),
                            new Point2d(pX * 10 + vX + 0.5f - 16, pZ * 10 + vZ - 0.5f - 17)
                    ));
                }
            }
        }
        this.walls = walls;
    }

    @Data
    public class PolygonShape {

        private Polygon2d data;
        private Point2d direction = new Point2d(0, 1);

        public PolygonShape(Polygon2d data, Point2d direction) {
            this.direction = direction;
            this.data = data;
        }

        public Polygon2d translate(Force f, float dt) {
            List<Point2d> newPoints = new ArrayList<>();
            for (Point2d p : data.getPoints()) {
                newPoints.add(new Point2d(
                        f.getDirection().getX() * f.getMagnitude() * dt + p.getX(),
                        f.getDirection().getY() * f.getMagnitude() * dt + p.getY()));
            }
            return new Polygon2d(newPoints);
        }

        public void lookAt(Point2d lookAt) {
            Point2d center = PhysicUtil.getCenter(data);
            Point2d c = toLocalCoord(center);
            float angle = PhysicUtil.getAngle(c, direction) - PhysicUtil.getAngle(c, lookAt);
            player.data = PhysicUtil.rotate(player.data, angle, center);
            player.direction = lookAt;
        }
    }

}
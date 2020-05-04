package com.metalheart.showcase.service;

import com.metalheart.model.TerrainChunk;
import com.metalheart.model.Vector3;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CanvasService {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int UNIT = 30;

    private Canvas game = new Canvas(WIDTH, HEIGHT);

    public GraphicsContext getGraphicsContext() {
        return game.getGraphicsContext2D();
    }

    public Scene createScene() {
        StackPane root = new StackPane();
        root.getChildren().addAll(game);
        return new Scene(root, CanvasService.WIDTH, CanvasService.HEIGHT);
    }

    public void clear() {
        GraphicsContext gc = getGraphicsContext();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
    }

    public void draw(Polygon2d polygon, GraphicsContext gc, boolean active) {

        double[] xPoints = polygon.getPoints().stream().mapToDouble(Point2d::getX).map(CanvasService::toXCoord).toArray();
        double[] yPoints = polygon.getPoints().stream().mapToDouble(Point2d::getY).map(CanvasService::toYCoord).toArray();

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



    public List<Polygon2d> toShowcasePolygons(Set<TerrainChunk> chunks) {
        List<Polygon2d> walls = new ArrayList<>();
        for (TerrainChunk chunk : chunks) {
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
        return walls;
    }
}

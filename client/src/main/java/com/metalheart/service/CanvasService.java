package com.metalheart.service;

import com.metalheart.model.logic.TerrainChunk;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.model.transport.TerrainChunkDTO;
import com.metalheart.model.transport.Vector3;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CanvasService {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int UNIT = 10;

    @Autowired
    private PlayerInputService playerInputService;

    @Autowired
    private ConversionService conversionService;

    private Canvas game;

    public GraphicsContext getGraphicsContext() {
        return game.getGraphicsContext2D();
    }

    public Scene createScene() {

        game = new Canvas(WIDTH, HEIGHT);

        StackPane root = new StackPane();
        root.getChildren().addAll(game);

        Scene scene = new Scene(root, CanvasService.WIDTH, CanvasService.HEIGHT);
        scene.setOnKeyPressed(playerInputService.getKeyPressHandler());
        scene.setOnKeyReleased(playerInputService.getKeyReleaseHandler());

        return scene;
    }

    public void clear() {
        GraphicsContext gc = getGraphicsContext();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
    }


    public void draw(Point2d p, Color color) {

        Point2d point = toLocalCoord(p);

        GraphicsContext gc = getGraphicsContext();
        gc.setFill(color);
        gc.fillOval(
                point.getD0() - 3,
                point.getD1() - 3,
                6,
                6
        );
    }

    public void draw(Point2d point0, Point2d point1, Color color) {

        Point2d p0 = toLocalCoord(point0);
        Point2d p1 = toLocalCoord(point1);

        GraphicsContext gc = getGraphicsContext();
        gc.setStroke(color);
        gc.strokeLine(p0.getD0(), p0.getD1(), p1.getD0(), p1.getD1());
    }

    public void draw(Polygon2d polygon, GraphicsContext gc, boolean active) {

        double[] xPoints = polygon.getPoints().stream().mapToDouble(Point2d::getD0).map(CanvasService::toXCoord).toArray();
        double[] yPoints = polygon.getPoints().stream().mapToDouble(Point2d::getD1).map(CanvasService::toYCoord).toArray();

        if (active) {
            gc.setFill(Color.RED);
            gc.fillPolygon(xPoints, yPoints, 4);
        }

        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);

        for (Point2d p : polygon.getPoints()) {
            gc.fillOval(
                    toXCoord(p.getD0()) - 3,
                    toYCoord(p.getD1()) - 3,
                    6,
                    6
            );
        }

        gc.strokePolygon(xPoints, yPoints, polygon.getPoints().size());
    }

    public static Double toYCoord(double y) {
        return HEIGHT / 2 - y * UNIT;
    }

    public static Double toXCoord(double x) {
        return WIDTH / 2 + x * UNIT;
    }

    public static Point2d toLocalCoord(Point2d p) {
        return new Point2d(
                WIDTH / 2 + p.getD0() * UNIT,
                HEIGHT / 2 - p.getD1() * UNIT
        );
    }

    public List<Polygon2d> toShowcasePolygons2(Collection<TerrainChunk> chunks) {

       return chunks.stream()
                .flatMap(chunk -> chunk.getWalls().stream())
                .map(wall -> wall.getRigidBody().getShape())
                .collect(Collectors.toList());
    }

    public List<Polygon2d> toShowcasePolygons(Collection<TerrainChunkDTO> chunks) {

        List<Polygon2d> walls = new ArrayList<>();
        for (TerrainChunkDTO chunk : chunks) {
            for (Vector3 voxel : chunk.getChildren()) {
                if (voxel.getY() == 2) {
                    final float pX = 0;
                    final float pZ = 0;
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

    public List<Polygon2d> toShowcaseOptimizedPolygons(Collection<TerrainChunk> chunks) {

        TypeDescriptor sourceType = TypeDescriptor.valueOf(TerrainChunkDTO.class);
        TypeDescriptor targetType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Polygon2d.class));

        List<Polygon2d> walls = new ArrayList<>();
        for (TerrainChunk chunk : chunks) {
            List<Polygon2d> wallChunk = (List<Polygon2d>) conversionService.convert(chunk, sourceType, targetType);

            wallChunk.stream()
                    .map(this::addOffset)
                    .forEach(walls::add);
        }
        return walls;
    }

    public Polygon2d addOffset(Polygon2d polygon) {

        final int xOffset = -16;
        final int yOffset = -15;

        return new Polygon2d(
                polygon.getPoints().stream()
                        .map(p -> new Point2d(p.getD0() + xOffset, p.getD1() + yOffset))
                        .collect(Collectors.toList()));
    }
}

package com.metalheart.service;

import com.metalheart.model.TerrainChunk;
import com.metalheart.model.Vector3;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Component
public class CanvasService {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int UNIT = 30;

    @Autowired
    private PlayerInputService playerInputService;

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
                point.getX() - 3,
                point.getY() - 3,
                6,
                6
        );
    }

    public void draw(Point2d point0, Point2d point1, Color color) {

        Point2d p0 = toLocalCoord(point0);
        Point2d p1 = toLocalCoord(point1);

        GraphicsContext gc = getGraphicsContext();
        gc.setStroke(color);
        gc.strokeLine(p0.getX(), p0.getY(), p1.getX(), p1.getY());
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
                WIDTH / 2 + p.getX() * UNIT,
                HEIGHT / 2 - p.getY() * UNIT
        );
    }


    public List<Polygon2d> toShowcasePolygons(Collection<TerrainChunk> chunks) {

        List<Polygon2d> walls = new ArrayList<>();
        for (TerrainChunk chunk : chunks) {
            Vector3 position = chunk.getPosition();
            for (Vector3 voxel : chunk.getChildren()) {
                if (voxel.getY() == 2) {
                    final float pX = 0;//position.getX();
                    final float pZ = 0;//position.getZ();
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

        List<Polygon2d> walls = new ArrayList<>();
        for (TerrainChunk chunk : chunks) {
            List<Polygon2d> wallChunk = toPolygons(chunk);
            walls.addAll(wallChunk);
        }
        return walls;
    }

    public List<Polygon2d> toPolygons(TerrainChunk chunk) {

        List<List<Point2d>> res = new ArrayList<>();
        Map<Point2d, List<Point2d>> map = new HashMap<>();

        for (Vector3 pos : chunk.getChildren()) {
            map.put(new Point2d(pos.getX(), pos.getZ()), new ArrayList<>(asList(
                    new Point2d(pos.getX() - 0.5f - 16, pos.getZ() - 0.5f - 17),
                    new Point2d(pos.getX() - 0.5f - 16, pos.getZ() + 0.5f - 17),
                    new Point2d(pos.getX() + 0.5f - 16, pos.getZ() + 0.5f - 17),
                    new Point2d(pos.getX() + 0.5f - 16, pos.getZ() - 0.5f - 17)
            )));
        }


        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {

                if (map.containsKey(new Point2d(x, y))) {

                    List<Point2d> points = map.get(new Point2d(x, y));

                    while (map.containsKey(new Point2d(x + 1, y))) {
                        points.addAll(map.get(new Point2d(++x, y)));
                    }
                    res.add(points);
                }
            }
        }

        while (isMergeble(res)) ;

        return res.stream()
                .map(PhysicUtil::grahamScan)
                .map(this::removeUnnecessaryPoints)
                .collect(Collectors.toList());
    }

    public Polygon2d removeUnnecessaryPoints(Polygon2d polygon) {
        List<Point2d> points = new ArrayList<>(polygon.getPoints());
        boolean removed;
        do{
            removed = false;
            for (int i = 0; i < points.size() - 2; i++) {
                float crossProduct = PhysicUtil.crossProduct(points.get(i), points.get(i+2), points.get(i+1));
                if(crossProduct > -0.0001 && crossProduct < 0.0001) {
                    points.remove(i+1);
                    removed = true;
                    break;
                }
            }
        } while (removed);
        return new Polygon2d(points);
    }

    private boolean isMergeble(List<List<Point2d>> res) {
        for (int i = 0; i < res.size(); i++) {

            List<Point2d> p0 = res.get(i);
            for (int j = 0; j < res.size(); j++) {

                if (i != j) {

                    List<Point2d> p1 = res.get(j);

                    if (canBeMerged(p0, p1)) {

                        p0.addAll(p1);
                        res.remove(p0);
                        res.remove(p1);
                        res.add(p0);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canBeMerged(List<Point2d> p0, List<Point2d> p1) {

        List<Float> p0X = p0.stream().map(Point2d::getX).distinct().collect(Collectors.toList());
        List<Float> p1X = p1.stream().map(Point2d::getX).distinct().collect(Collectors.toList());
        List<Float> p1Y = p1.stream().map(Point2d::getY).distinct().collect(Collectors.toList());

        if (!p0X.equals(p1X)) {
            return false;
        }

        OptionalDouble p0MaxY = p0.stream()
                .map(Point2d::getY)
                .mapToDouble(Double::valueOf)
                .max();

        OptionalDouble p0MinY = p0.stream()
                .map(Point2d::getY)
                .mapToDouble(Double::valueOf)
                .min();


        return p1Y.contains((float)p0MaxY.getAsDouble()) || p1Y.contains((float)p0MinY.getAsDouble());
    }
}

package com.metalheart.service.imp;

import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.model.physic.Vector3d;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;

@Component
public class CanvasServiceImpl {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int UNIT = 30;

    private Canvas game;

    public GraphicsContext getGraphicsContext() {
        return game.getGraphicsContext2D();
    }

    public Scene createScene() {

        game = new Canvas(WIDTH, HEIGHT);

        StackPane root = new StackPane();
        root.getChildren().addAll(game);

        return new Scene(root, WIDTH, HEIGHT);
    }

    public void clear(Color color) {
        GraphicsContext gc = getGraphicsContext();
        gc.setFill(color);
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

    public void draw(Polygon2d polygon, Color color) {

        polygon = polygon.withOffset(new Vector3d(-16, 0, -15));

        double[] xPoints = polygon.getPoints().stream().mapToDouble(Point2d::getD0).map(CanvasServiceImpl::toXCoord).toArray();
        double[] yPoints = polygon.getPoints().stream().mapToDouble(Point2d::getD1).map(CanvasServiceImpl::toYCoord).toArray();

        GraphicsContext gc = getGraphicsContext();

        gc.setFill(color);
        gc.setStroke(color);

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
}

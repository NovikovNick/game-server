package com.metalheart.service;

import com.metalheart.model.physic.Line;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.util.Arrays.asList;

public final class PhysicUtil {

    private PhysicUtil() {
        throw new UnsupportedOperationException();
    }

    public static boolean isIntersect(Line a, Line b) {

        if (a.getStart() > a.getEnd()) {
            a = new Line(a.getEnd(), a.getStart());
        }

        if (b.getStart() > b.getEnd()) {
            b = new Line(b.getEnd(), b.getStart());
        }

        if ((b.getEnd() == a.getStart()) || (a.getEnd() == b.getStart()) || (a.getEnd() == b.getEnd())) {
            return true;
        }

        if (a.getEnd() > b.getEnd()) {
            Line tmp = b;
            b = a;
            a = tmp;
        }

        if (a.getEnd() > b.getStart()) {
            return true;
        }

        return false;
    }

    public static boolean isIntersect(Polygon2d a, Polygon2d b) {

        final List<Polygon2d> polygons = asList(a, b);
        for (int j = 0; j < 2; j++) {
            List<Point2d> points = polygons.get(j).getPoints();
            for (int i = 0; i < points.size(); i++) {

                Point2d p1 = points.get(i);
                Point2d p2 = i + 1 == points.size() ? points.get(0) : points.get(i + 1);

                float angle = -getAngle(p1, p2);
                if (!isIntersect(getProjection(rotate(a, angle), true), getProjection(rotate(b, angle), true))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static float getAngle(Point2d p1, Point2d p2) {
        float deltaX = p2.getX() - p1.getX();
        float deltaY = p2.getY() - p1.getY();
        return (float) Math.atan2(deltaY, deltaX);
    }

    public static Polygon2d rotate(Polygon2d polygon, float radian) {

        return new Polygon2d(polygon.getPoints().stream()
                .map(p -> rotate(p, radian))
                .collect(Collectors.toList()));
    }

    public static Polygon2d rotate(Polygon2d polygon, float angle, Point2d center) {

        return new Polygon2d(polygon.getPoints().stream()
                .map(p -> rotate(p, angle, center))
                .collect(Collectors.toList()));
    }

    public static Point2d rotate(Point2d p, float angle) {
        float cos = (float) cos(angle);
        float sin = (float) sin(angle);
        float x = p.getX();
        float y = p.getY();

        return new Point2d(
                x * cos - y * sin,
                x * sin + y * cos);
    }

    public static Point2d rotate(Point2d p, float angle, Point2d center) {
        float cos = (float) cos(angle);
        float sin = (float) sin(angle);

        float x = p.getX();
        float y = p.getY();

        float x0 = center.getX();
        float y0 = center.getY();

        return new Point2d(
                x0 + (x - x0) * cos - (y - y0) * sin,
                y0 + (y - y0) * cos + (x - x0) * sin);
    }

    public static Line getProjection(Polygon2d polygon, boolean toX) {

        float initialValue = toX ? polygon.getPoints().get(0).getX() : polygon.getPoints().get(0).getY();
        float min = initialValue;
        float max = initialValue;

        for (Point2d point : polygon.getPoints()) {
            float dim = toX ? point.getX() : point.getY();
            max = dim > max ? dim : max;
            min = dim < min ? dim : min;
        }
        return new Line(min, max);
    }

    /**
     * temporary realisation
     * @param polygon
     * @return
     */
    public static Point2d getCenter(Polygon2d polygon) {
        List<Point2d> points = polygon.getPoints();
        Point2d p0 = points.get(0);
        Point2d p2 = points.get(2);

        return new Point2d(
                p0.getX() - (p0.getX() - p2.getX()) / 2,
                p0.getY() - (p0.getY() - p2.getY()) / 2
        );
    }
}

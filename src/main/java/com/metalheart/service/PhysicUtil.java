package com.metalheart.service;

import com.metalheart.model.physic.Line;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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

        if ((b.getEnd() == a.getStart()) || (a.getEnd() == b.getStart())) {
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

        List<Point2d> aPoints = a.getPoints();
        for (int i = 0; i < aPoints.size(); i++) {

            Point2d p1 = aPoints.get(i);
            Point2d p2 = i + 1 == aPoints.size() ? aPoints.get(0) : aPoints.get(i);

            float angle = getAngle(p1, p2);
            if (!isIntersect(getProjection(rotate(a, angle), true), getProjection(rotate(b, angle), true))) {
                return false;
            }
        }

        List<Point2d> bPoints = b.getPoints();
        for (int i = 0; i < bPoints.size(); i++) {

            Point2d p1 = bPoints.get(i);
            Point2d p2 = i + 1 == bPoints.size() ? bPoints.get(0) : bPoints.get(i);

            float angle = getAngle(p1, p2);
            if (!isIntersect(getProjection(rotate(a, angle), true), getProjection(rotate(b, angle), true))) {
                return false;
            }
        }

        return true;
    }

    public static float getAngle(Point2d p1, Point2d p2) {
        float deltaX = p2.getX() - p1.getX();
        float deltaY = p2.getY() - p1.getY();
        return (float) Math.atan2(deltaY, deltaX);
    }

    public static Polygon2d rotate(Polygon2d polygon, float angle) {

        return new Polygon2d(polygon.getPoints().stream()
                .map(p -> rotate(p, angle))
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
}

package com.metalheart.service;

import com.metalheart.algorithm.GrahamScanAlgorithm;
import com.metalheart.model.Vector3;
import com.metalheart.model.physic.CollisionResult;
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

    public static CollisionResult detectCollision(Line a, Line b) {

        boolean sign = false;

        if (a.getStart() > a.getEnd()) {
            a = new Line(a.getEnd(), a.getStart());
        }

        if (b.getStart() > b.getEnd()) {
            b = new Line(b.getEnd(), b.getStart());
        }

        if ((b.getEnd() == a.getStart())
                || (a.getEnd() == b.getStart())
                || (a.getEnd() == b.getEnd())
                || (a.getStart() == b.getStart())) {
            return CollisionResult.builder().collide(true).depth(0).build();
        }

        if (a.getEnd() > b.getEnd()) {
            Line tmp = b;
            b = a;
            a = tmp;
            sign = true;
        }

        if (a.getEnd() > b.getStart()) {
            return CollisionResult.builder().collide(true).depth(a.getEnd() - b.getStart()).sign(sign).build();
        }

        return CollisionResult.builder().collide(false).build();
    }

    public static CollisionResult detectCollision(Polygon2d a, Polygon2d b) {

        Float depth = null;
        Vector3 normal = null;
        Point2d c1 = null;
        Point2d c2 = null;

        final List<Polygon2d> polygons = asList(a, b);
        for (int j = 0; j < 2; j++) {
            List<Point2d> points = polygons.get(j).getPoints();
            for (int i = 0; i < points.size(); i++) {

                Point2d p1 = points.get(i);
                Point2d p2 = i + 1 == points.size() ? points.get(0) : points.get(i + 1);

                float angle = -getAngle(p1, p2);
                Line aProjection = getProjection(rotate(a, angle), true);
                Line bProjection = getProjection(rotate(b, angle), true);
                CollisionResult collisionResult = detectCollision(aProjection, bProjection);
                if (!collisionResult.isCollide()) {

                    return CollisionResult.builder().collide(false).build();

                } else if (depth == null || depth > collisionResult.getDepth()) {

                    depth = collisionResult.getDepth();
                    c1 = p1;
                    c2 = p2;
                    float deltaX = collisionResult.isSign() ? p1.getD0() - p2.getD0() : p2.getD0() - p1.getD0();
                    float deltaY = collisionResult.isSign() ? p1.getD1() - p2.getD1() : p2.getD1() - p1.getD1();
                    normal = new Vector3(deltaX, deltaY, 0).normalize();
                }
            }
        }
        return CollisionResult.builder()
                .p1(c1)
                .p2(c2)
                .collide(true)
                .depth(depth)
                .normal(normal)
                .build();
    }

    public static float getAngle(Point2d p1, Point2d p2) {
        float deltaX = p2.getD0() - p1.getD0();
        float deltaY = p2.getD1() - p1.getD1();
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
        float x = p.getD0();
        float y = p.getD1();

        return new Point2d(
                x * cos - y * sin,
                x * sin + y * cos);
    }

    public static Point2d rotate(Point2d p, float angle, Point2d center) {
        float cos = (float) cos(angle);
        float sin = (float) sin(angle);

        float x = p.getD0();
        float y = p.getD1();

        float x0 = center.getD0();
        float y0 = center.getD1();

        return new Point2d(
                x0 + (x - x0) * cos - (y - y0) * sin,
                y0 + (y - y0) * cos + (x - x0) * sin);
    }

    public static Line getProjection(Polygon2d polygon, boolean toX) {

        float initialValue = toX ? polygon.getPoints().get(0).getD0() : polygon.getPoints().get(0).getD1();
        float min = initialValue;
        float max = initialValue;

        for (Point2d point : polygon.getPoints()) {
            float dim = toX ? point.getD0() : point.getD1();
            max = dim > max ? dim : max;
            min = dim < min ? dim : min;
        }
        return new Line(min, max);
    }

    /**
     * temporary realisation
     *
     * @param polygon
     * @return
     */
    public static Point2d getCenter(Polygon2d polygon) {
        List<Point2d> points = polygon.getPoints();
        Point2d p0 = points.get(0);
        Point2d p2 = points.get(2);

        return new Point2d(
                p0.getD0() - (p0.getD0() - p2.getD0()) / 2,
                p0.getD1() - (p0.getD1() - p2.getD1()) / 2
        );
    }

    public static Polygon2d grahamScan(List<Point2d> points) {

        GrahamScanAlgorithm algorithm = new GrahamScanAlgorithm(points);
        while (!algorithm.isFinished()) {
            algorithm.step();
        }
        return algorithm.getResult();
    }

    public static  boolean isLeftRotation(Point2d a, Point2d b, Point2d c) {
        return crossProduct(a, b, c) >= 0;
    }

    public static  float crossProduct(Point2d a, Point2d b, Point2d c) {
        Point2d u = new Point2d(b.getD0() - a.getD0(), b.getD1() - a.getD1());
        Point2d v = new Point2d(c.getD0() - b.getD0(), c.getD1() - b.getD1());

        return u.getD0() * v.getD1() - u.getD1() * v.getD0();
    }
}

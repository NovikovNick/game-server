package com.metalheart.service.terrain;

import com.metalheart.math.PhysicUtil;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.model.physic.Vector3d;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class TerrainConverUtil {

    public static List<Polygon2d> convert(Set<Vector3d> src) {

        List<List<Point2d>> res = new ArrayList<>();
        Map<Point2d, List<Point2d>> map = new HashMap<>();

        for (Vector3d pos : src) {
            if (pos.d1 == 2) {
                map.put(new Point2d(pos.d0, pos.d2), new ArrayList<>(asList(
                        new Point2d(pos.d0 - 0.5f, pos.d2 - 0.5f),
                        new Point2d(pos.d0 - 0.5f, pos.d2 + 0.5f),
                        new Point2d(pos.d0 + 0.5f, pos.d2 + 0.5f),
                        new Point2d(pos.d0 + 0.5f, pos.d2 - 0.5f)
                )));
            }
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

        while (merge(res)) ;

        return res.stream()
                .map(PhysicUtil::grahamScan)
                .map(TerrainConverUtil::removeUnnecessaryPoints)
                .collect(Collectors.toList());
    }

    private static boolean merge(List<List<Point2d>> res) {
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

    private static Polygon2d removeUnnecessaryPoints(Polygon2d polygon) {
        List<Point2d> points = new ArrayList<>(polygon.getPoints());
        boolean removed;
        do {
            removed = false;
            for (int i = 0; i < points.size() - 2; i++) {
                float crossProduct = PhysicUtil.crossProduct(points.get(i), points.get(i + 2), points.get(i + 1));
                if (crossProduct > -0.0001 && crossProduct < 0.0001) {
                    points.remove(i + 1);
                    removed = true;
                    break;
                }
            }
        } while (removed);
        return new Polygon2d(points);
    }

    private static boolean canBeMerged(List<Point2d> p0, List<Point2d> p1) {

        List<Float> p0X = p0.stream().map(Point2d::getD0).distinct().collect(Collectors.toList());
        List<Float> p1X = p1.stream().map(Point2d::getD0).distinct().collect(Collectors.toList());
        List<Float> p1Y = p1.stream().map(Point2d::getD1).distinct().collect(Collectors.toList());

        if (!p0X.equals(p1X)) {
            return false;
        }

        OptionalDouble p0MaxY = p0.stream()
                .map(Point2d::getD1)
                .mapToDouble(Double::valueOf)
                .max();

        OptionalDouble p0MinY = p0.stream()
                .map(Point2d::getD1)
                .mapToDouble(Double::valueOf)
                .min();


        return p1Y.contains((float) p0MaxY.getAsDouble()) || p1Y.contains((float) p0MinY.getAsDouble());
    }
}

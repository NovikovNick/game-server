package com.metalheart.model.physic;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Data
public class Polygon2d {

    private final List<Point2d> points;

    public Polygon2d(List<Point2d> points) {
        this.points = Collections.unmodifiableList(points);
    }

    public Polygon2d(Point2d...points) {
        this.points = Collections.unmodifiableList(Arrays.stream(points).collect(Collectors.toList()));
    }

    public Polygon2d withOffset(Vector3d offset) {
        return new Polygon2d(getPoints()
                .stream()
                .map(p -> new Point2d(p.getD0() + offset.d0, p.getD1() + offset.d2))
                .collect(toList()));
    }
}

package com.metalheart.model.physic;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Polygon2d {

    private final List<Point2d> points;

    public Polygon2d(List<Point2d> points) {
        this.points = Collections.unmodifiableList(points);
    }

    public Polygon2d(Point2d...points) {
        this.points = Collections.unmodifiableList(Arrays.stream(points).collect(Collectors.toList()));
    }
}

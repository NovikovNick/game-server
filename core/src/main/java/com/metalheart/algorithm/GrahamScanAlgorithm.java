package com.metalheart.algorithm;

import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.math.PhysicUtil;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

@Data
public class GrahamScanAlgorithm {

    private List<Point2d> points;
    private Stack<Point2d> stack;
    private Polygon2d result;

    private Integer i;


    public GrahamScanAlgorithm(List<Point2d> points) {

        i = 0;
        stack = new Stack<>();

        Point2d cursor = points.get(0);
        for (Point2d point : points) {

            if (point.getD1() <= cursor.getD1()) {

                if (point.getD1() == cursor.getD1()) {
                    cursor = point.getD0() < cursor.getD0() ? point : cursor;
                } else {
                    cursor = point;
                }
            }
        }

        stack.push(cursor);

        final Point2d fst = stack.peek();

        final Comparator<Point2d> pointPolarAngleComparator = (p1, p2) -> {

            final float p1DeltaX = p1.getD0() - fst.getD0();
            final float p1DeltaY = p1.getD1() - fst.getD1();
            final float p2DeltaX = p2.getD0() - fst.getD0();
            final float p2DeltaY = p2.getD1() - fst.getD1();

            double a1 = Math.atan2(p1DeltaX, p1DeltaY);
            double a2 = Math.atan2(p2DeltaX, p2DeltaY);

            int resByAngle = Double.compare(a1, a2);

            if (resByAngle == 0) {
                double p1Length = Math.sqrt(p1DeltaX * p1DeltaX + p1DeltaY * p1DeltaY);
                double p2Length = Math.sqrt(p2DeltaX * p2DeltaX + p2DeltaY * p2DeltaY);
                return Double.compare(p1Length, p2Length) * -1;
            }

            return resByAngle;
        };

        this.points = points.stream()
                .filter(point -> !fst.equals(point))
                .distinct()
                .sorted(pointPolarAngleComparator.reversed())
                .collect(Collectors.toList());

        stack.push(this.points.get(i++));
    }

    public void step() {

        if (i < points.size()) {

            Point2d next = points.get(i);
            Point2d previous = stack.pop();
            Point2d current;

            boolean toRight;
            do {
                current = previous;
                previous = stack.pop();

                toRight = !PhysicUtil.isLeftRotation(next, previous, current);
            } while (toRight);

            stack.push(previous);
            stack.push(current);
            stack.push(next);
            i++;

        } else {
            result = new Polygon2d(stack.stream().collect(Collectors.toList()));
        }
    }


    public boolean isFinished() {
        return result != null;
    }
}

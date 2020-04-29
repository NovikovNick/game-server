package com.metalheart;

import com.metalheart.model.physic.Line;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.service.PhysicUtil;
import org.junit.Assert;
import org.junit.Test;

public class PhysicTest {


    @Test
    public void lineIntersectionTest() {

        Assert.assertFalse(PhysicUtil.isIntersect(new Line(0f, 1f), new Line(2f, 3f)));
        Assert.assertFalse(PhysicUtil.isIntersect(new Line(0f, 1f), new Line(3f, 2f)));

        Assert.assertFalse(PhysicUtil.isIntersect(new Line(3f, 2f), new Line(0f, 1f)));
        Assert.assertFalse(PhysicUtil.isIntersect(new Line(3f, 2f), new Line(0f, 1f)));


        Assert.assertTrue(PhysicUtil.isIntersect(new Line(0f, 1f), new Line(0.5f, 1.5f)));
        Assert.assertTrue(PhysicUtil.isIntersect(new Line(0f, 1f), new Line(1f, 1.5f)));
        Assert.assertTrue(PhysicUtil.isIntersect(new Line(0.5f, 1f), new Line(0f, 1.5f)));

        Assert.assertTrue(PhysicUtil.isIntersect(new Line(0.5f, 1.5f), new Line(0f, 1f)));
        Assert.assertTrue(PhysicUtil.isIntersect(new Line(1f, 1.5f), new Line(0f, 1f)));
        Assert.assertTrue(PhysicUtil.isIntersect(new Line(0f, 1.5f), new Line(0.5f, 1f)));
    }

    @Test
    public void poligonIntersectionTest() {

        Polygon2d p1 = new Polygon2d(new Point2d(1, 1), new Point2d(2, 2), new Point2d(1, 3), new Point2d(0, 2));
        Polygon2d p2 = new Polygon2d(new Point2d(-1, -1), new Point2d(-3, 0), new Point2d(-4, 2), new Point2d(-2, 3));

        Assert.assertFalse(PhysicUtil.isIntersect(p1, p2));
    }


}

package com.metalheart;

import com.metalheart.model.physic.Line;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.math.PhysicUtil;
import org.junit.Assert;
import org.junit.Test;

public class PhysicTest {


    @Test
    public void lineIntersectionTest() {

        Assert.assertFalse(PhysicUtil.detectCollision(new Line(0f, 1f), new Line(2f, 3f)).isCollide());
        Assert.assertFalse(PhysicUtil.detectCollision(new Line(0f, 1f), new Line(3f, 2f)).isCollide());

        Assert.assertFalse(PhysicUtil.detectCollision(new Line(3f, 2f), new Line(0f, 1f)).isCollide());
        Assert.assertFalse(PhysicUtil.detectCollision(new Line(3f, 2f), new Line(0f, 1f)).isCollide());


        Assert.assertTrue(PhysicUtil.detectCollision(new Line(0f, 1f), new Line(0.5f, 1.5f)).isCollide());
        Assert.assertTrue(PhysicUtil.detectCollision(new Line(0.5f, 1f), new Line(0f, 1.5f)).isCollide());

        Assert.assertTrue(PhysicUtil.detectCollision(new Line(0.5f, 1.5f), new Line(0f, 1f)).isCollide());
        Assert.assertTrue(PhysicUtil.detectCollision(new Line(0f, 1.5f), new Line(0.5f, 1f)).isCollide());
    }

    @Test
    public void polygonIntersectionTest() {

        Polygon2d p1 = new Polygon2d(
                new Point2d(1.0494196f, 0.16851634f),
                new Point2d(-0.21843511f, 2.8874397f),
                new Point2d(3.2255344f, 4.493389f),
                new Point2d(4.493389f, 1.7744658f));

        Polygon2d p2 = new Polygon2d(
                new Point2d(-1.3289261f, 0.4836895f),
                new Point2d(-2.5967808f, 3.2026129f),
                new Point2d(-5.3157043f, 1.934758f),
                new Point2d(-4.047849f, -0.7841653f));

        Assert.assertFalse(PhysicUtil.detectCollision(p1, p2).isCollide());
    }


}

package com.metalheart.service;

import com.metalheart.math.PhysicUtil;
import com.metalheart.model.physic.Force;
import com.metalheart.model.ShowcaseObject;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShowcaseService {

    public ShowcaseObject translate(ShowcaseObject obj, Force f, float dt) {
        List<Point2d> newPoints = new ArrayList<>();
        for (Point2d p : obj.getData().getPoints()) {
            newPoints.add(new Point2d(
                    f.getDirection().d0 * f.getMagnitude() * dt + p.getD0(),
                    f.getDirection().d1 * f.getMagnitude() * dt + p.getD1()));
        }

        ShowcaseObject res = new ShowcaseObject();
        res.setData(new Polygon2d(newPoints));
        res.setDirection(obj.getDirection());
        return res;
    }

    public ShowcaseObject rotateTo(ShowcaseObject obj, Point2d target) {

        Point2d center = PhysicUtil.getCenter(obj.getData());
        Point2d c = CanvasService.toLocalCoord(center);
        float angle = PhysicUtil.getAngle(c, obj.getDirection()) - PhysicUtil.getAngle(c, target);

        ShowcaseObject res = new ShowcaseObject();
        res.setData(PhysicUtil.rotate(obj.getData(), angle, center));
        res.setDirection(target);
        return res;
    }
}

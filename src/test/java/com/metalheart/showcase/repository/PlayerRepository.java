package com.metalheart.showcase.repository;

import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.showcase.model.ShowcaseObject;

public class PlayerRepository {

    private ShowcaseObject player;

    public ShowcaseObject get() {
        if (player == null) {

            player = new ShowcaseObject(new Polygon2d(
                    new Point2d(-0.5f, 0.5f),
                    new Point2d(0.5f, 0.5f),
                    new Point2d(0.5f, -0.5f),
                    new Point2d(-0.5f, -0.5f)
            ), new Point2d(0, 1));
        }
        return player;
    }

    public ShowcaseObject save(ShowcaseObject player) {
        return this.player = player;
    }
}

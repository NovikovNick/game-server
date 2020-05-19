package com.metalheart.model.physic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollisionResult {

    private final boolean collide;

    private final float depth;
    private final Vector3d normal;

    private Point2d p1;
    private Point2d p2;
    private boolean sign;
}

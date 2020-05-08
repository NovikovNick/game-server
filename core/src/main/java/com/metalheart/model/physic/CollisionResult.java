package com.metalheart.model.physic;

import com.metalheart.model.Vector3;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollisionResult {

    private final boolean collide;

    private final float depth;
    private final Vector3 normal;

    private Point2d p1;
    private Point2d p2;
    private boolean sign;
}

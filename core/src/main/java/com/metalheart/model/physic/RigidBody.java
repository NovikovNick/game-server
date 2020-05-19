package com.metalheart.model.physic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RigidBody {
    private Polygon2d shape;
}

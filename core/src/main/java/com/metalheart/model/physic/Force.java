package com.metalheart.model.physic;

import com.metalheart.model.logic.GameObject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Force {

    private GameObject target;
    private Vector3d direction;
    private float magnitude;
    private float timeDelta;

    public Force(Vector3d direction, float magnitude) {
        this.direction = direction;
        this.magnitude = magnitude;
    }
}

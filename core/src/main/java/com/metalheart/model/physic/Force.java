package com.metalheart.model.physic;

import com.metalheart.model.Vector3;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Force {

    private Vector3 direction;
    private float magnitude;
    private float timeDelta;

    public Force(Vector3 direction, float magnitude) {
        this.direction = direction;
        this.magnitude = magnitude;
    }
}

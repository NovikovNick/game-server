package com.metalheart.showcase.model;

import com.metalheart.model.Vector3;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Force {

    private Vector3 direction;
    private float magnitude;
}

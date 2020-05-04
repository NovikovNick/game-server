package com.metalheart.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Force {

    private Vector3 direction;
    private float magnitude;
}

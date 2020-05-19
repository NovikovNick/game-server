package com.metalheart.model.physic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transform {
    private Vector3d position;
    private Vector3d rotation;
}

package com.metalheart.model.transport;

import lombok.Data;

@Data
public class PlayerDTO {

    private Vector3 position;
    private Vector3 rotation;
    private float speed;
}

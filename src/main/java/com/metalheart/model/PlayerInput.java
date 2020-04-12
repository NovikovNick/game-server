package com.metalheart.model;

import lombok.Data;

@Data
public class PlayerInput {
    public Long timestamp;
    public Integer datagramNumber;
    public float timeDelta;

    public Vector3 direction;
    public Float magnitude;
    public Boolean isRunning;
}

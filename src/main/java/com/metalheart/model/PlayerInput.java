package com.metalheart.model;

import lombok.Data;

@Data
public class PlayerInput {
    public int sequenceNumber;
    public Integer acknowledgmentNumber;

    public float timeDelta;

    public Vector3 direction;
    public Float magnitude;
    public Boolean isRunning;

    public Vector3 loadedChunck;
}

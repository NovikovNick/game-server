package com.metalheart.model;

import lombok.Data;

@Data
public class PlayerRequest {
    public Long timestamp;
    public Integer datagramNumber;
    public Byte playerId;

    public Vector3 direction;
    public Float magnitude;
    public Boolean isRunning;
}

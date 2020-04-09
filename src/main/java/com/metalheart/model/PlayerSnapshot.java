package com.metalheart.model;

import lombok.Data;

@Data
public class PlayerSnapshot {

    private Integer lastDatagramNumber;

    private byte playerId;
    private Vector3 position;
    private Vector3 direction;
}

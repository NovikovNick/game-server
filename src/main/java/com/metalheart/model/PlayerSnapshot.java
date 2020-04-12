package com.metalheart.model;

import lombok.Data;

import java.util.Collection;

@Data
public class PlayerSnapshot {
    private Long  timestamp;
    private Integer lastDatagramNumber;

    private GameObject player;
    private Collection<GameObject> otherPlayers;
}

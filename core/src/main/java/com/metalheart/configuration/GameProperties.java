package com.metalheart.configuration;

import lombok.Data;

@Data
public class GameProperties {

    // infrastructure
    private String host = "192.168.0.102";
    private int port = 7777;

    // performance
    private int tickRate = 10;
    private int playerSnapshotBufferCapacity = 32;

    // game
    private float walkSpeed = 2;
    private float runSpeed = 6;
}

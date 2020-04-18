package com.metalheart.configuration;

import lombok.Data;

@Data
public class GameProperties {

    private String host = "192.168.0.102";
    private int port = 7777;
    private int tickRate = 10;

    private float walkSpeed = 2;
    private float runSpeed = 6;
}

package com.metalheart.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class State {
    private Map<InetSocketAddress, Player> players;
    private Set<TerrainChunk> terrainChunks;

    public State() {
        this.players = new HashMap<>();
        this.terrainChunks = new HashSet<>();
    }
}

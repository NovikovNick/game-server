package com.metalheart.model;

import lombok.Data;

import java.util.Collection;

@Data
public class PlayerSnapshot {
    private long  timestamp;
    public int sequenceNumber;
    public int acknowledgmentNumber;

    private GameObject player;
    private Collection<Player> otherPlayers;
    private Collection<TerrainChunk> terrainChunks;
}

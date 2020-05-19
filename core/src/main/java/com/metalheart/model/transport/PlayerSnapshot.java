package com.metalheart.model.transport;

import lombok.Data;

import java.util.Collection;

@Data
public class PlayerSnapshot {
    private long  timestamp;
    public int sequenceNumber;
    public int acknowledgmentNumber;

    private PlayerDTO player;
    private Collection<PlayerDTO> otherPlayers;
    private Collection<TerrainChunkDTO> terrainChunks;
}

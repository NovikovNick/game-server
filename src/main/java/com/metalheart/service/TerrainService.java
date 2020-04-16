package com.metalheart.service;

import com.metalheart.model.TerrainChunk;

import java.util.Set;

public interface TerrainService {
    Set<TerrainChunk> generateSimpleRoom();
    TerrainChunk getTerrainChunk(int x, int y, int z);
}

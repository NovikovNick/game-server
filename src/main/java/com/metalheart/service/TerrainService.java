package com.metalheart.service;

import com.metalheart.model.TerrainChunk;

import java.util.Set;

public interface TerrainService {

    Set<TerrainChunk> generateSimpleRoom();

    Set<TerrainChunk> generateRandomRoom();

    TerrainChunk getFourPassingRoom(int x, int y, int z);

    TerrainChunk getClosedRoom(int x1, int y1, int z1);

    Set<TerrainChunk> getCubes(int x1, int y1, int z1);
}

package com.metalheart.service.imp;

import com.metalheart.model.TerrainChunk;
import com.metalheart.model.Vector3;
import com.metalheart.service.TerrainService;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class TerrainServiceImp implements TerrainService {

    @Override
    public Set<TerrainChunk> generateSimpleRoom() {
        Set<TerrainChunk> terrainChunks = new HashSet<>();
        terrainChunks.add(getTerrainChunk(0, 0, 0));
        terrainChunks.add(getTerrainChunk(1, 0, 0));
        terrainChunks.add(getTerrainChunk(2, 0, 0));

        terrainChunks.add(getTerrainChunk(0, 0, 1));
        terrainChunks.add(getTerrainChunk(1, 0, 1));
        terrainChunks.add(getTerrainChunk(2, 0, 1));

        terrainChunks.add(getTerrainChunk(0, 0, 2));
        terrainChunks.add(getTerrainChunk(1, 0, 2));
        terrainChunks.add(getTerrainChunk(2, 0, 2));
        return terrainChunks;
    }

    @Override
    public TerrainChunk getTerrainChunk(int x1, int y1, int z1) {
        TerrainChunk chunk1 = new TerrainChunk();
        chunk1.setPosition(new Vector3(x1, y1, z1));
        Set<Vector3> voxels = new HashSet<>();
        for (int x = 1; x < 11; x++) {
            for (int z = 1; z < 11; z++) {
                for (int y = 1; y < 6; y++) {

                    if((z > 1 && asList(1, 10).contains(x))
                            && !(asList(5, 6).contains(z) && asList(2, 3, 4).contains(y))) {
                        voxels.add(new Vector3(x, y, z));
                    }
                    if((x > 1 && asList(1, 10).contains(z))
                            && !(asList(5, 6).contains(x) && asList(2, 3, 4).contains(y))) {
                        voxels.add(new Vector3(x, y, z));
                    }

                    if (y == 1) voxels.add(new Vector3(x, y, z));
                }
            }
        }
        chunk1.setChildren(voxels);
        return chunk1;
    }
}

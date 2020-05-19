package com.metalheart.model.logic;

import com.metalheart.model.physic.Vector3d;
import lombok.Data;

import java.util.List;

@Data
public class TerrainChunk {
    private Vector3d position;
    private List<GameObject> walls;
}

package com.metalheart.model;

import lombok.Data;

import java.util.Set;

@Data
public class TerrainChunk {
    private Vector3 position;
    private Set<Vector3> children;
}

package com.metalheart.model.transport;

import lombok.Data;

import java.util.Set;

@Data
public class TerrainChunkDTO {
    private Vector3 position;
    private Set<Vector3> children;
}

package com.metalheart.model.logic;

import com.metalheart.model.physic.Force;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.*;

@Data
@AllArgsConstructor
public class State {

    private Map<Integer, GameObject> gameObjects;
    private Map<GameObject, List<Force>> gameObjectsForces;

    private Map<InetSocketAddress, Player> players;
    private Set<TerrainChunk> terrainChunks;

    public State() {
        this.gameObjects = new HashMap<>();
        this.players = new HashMap<>();
        this.gameObjectsForces = new HashMap<>();
        this.terrainChunks = new HashSet<>();
    }
}

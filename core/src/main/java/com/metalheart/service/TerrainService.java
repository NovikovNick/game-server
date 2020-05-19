package com.metalheart.service;

import com.metalheart.algorithm.maze.Maze;
import com.metalheart.model.logic.TerrainChunk;

import java.util.Set;

public interface TerrainService {

    Set<TerrainChunk> generateMaze();

    Set<TerrainChunk> build(Maze maze);

}

package com.metalheart.service.imp;

import com.metalheart.model.TerrainChunk;
import com.metalheart.model.Vector3;
import com.metalheart.model.physic.Point2d;
import com.metalheart.service.TerrainService;
import com.metalheart.algorithm.maze.Maze;
import com.metalheart.algorithm.maze.MazeCell;
import com.metalheart.algorithm.maze.MazeDoorDirection;
import com.metalheart.algorithm.maze.RecursiveBacktrackerMazeBuilder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.metalheart.algorithm.maze.MazeDoorDirection.*;
import static java.util.Arrays.asList;

@Component
public class TerrainServiceImpl implements TerrainService {

    public static final Random RANDOM = new Random();

    @Override
    public Set<TerrainChunk> generateSimpleRoom() {
        Set<TerrainChunk> terrainChunks = new HashSet<>();
        for (int z = 0; z < 3; z++) {
            for (int x = 0; x < 3; x++) {
                terrainChunks.add(getFourPassingRoom(x, 0, z));
            }
        }
        return terrainChunks;
    }

    @Override
    public Set<TerrainChunk> generateRandomRoom() {
        Set<TerrainChunk> terrainChunks = new HashSet<>();
        for (int z = 0; z < 3; z++) {
            for (int x = 0; x < 3; x++) {
                terrainChunks.add(RANDOM.nextInt() % 2 == 0 ? getClosedRoom(x, 0, z) : getFourPassingRoom(x, 0, z));
            }
        }
        return terrainChunks;
    }

    @Override
    public Set<TerrainChunk> getCubes(int x1, int y1, int z1) {

        Set<TerrainChunk> terrainChunks = new HashSet<>();

        TerrainChunk chunk1 = new TerrainChunk();
        chunk1.setPosition(new Vector3(x1, y1, z1));
        Set<Vector3> voxels = new HashSet<>();
        //voxels.add(new Vector3(1, 1, 1));
        voxels.add(new Vector3(1, 2, 1));
        //voxels.add(new Vector3(1, 3, 1));
        chunk1.setChildren(voxels);

        terrainChunks.add(chunk1);

        return terrainChunks;
    }

    @Override
    public Set<TerrainChunk> generateMaze() {
        RecursiveBacktrackerMazeBuilder mazeBuilder = new RecursiveBacktrackerMazeBuilder()
                .setWidth(5)
                .setHeight(5)
                .setEnter(new Point2d(0, 0))
                .setEnterDirection(MazeDoorDirection.LEFT)
                .setExit(new Point2d(5, 4))
                .setExitDirection(MazeDoorDirection.RIGHT);

        Maze maze = new Maze();
        while (!mazeBuilder.isFinished(maze)) {
            maze = mazeBuilder.buildNextStep(maze);
        }
        return build(maze);
    }

    @Override
    public Set<TerrainChunk> build(Maze maze) {

        Map<Point2d, TerrainChunk> res = new HashMap<>();

        Map<Point2d, MazeCell> data = maze.getData();

        for (int x = 0; x < 6; x += 2) {
            for (int y = 0; y < 6; y += 2) {

                TerrainChunk chunk = new TerrainChunk();
                chunk.setChildren(new HashSet<>());
                chunk.setPosition(new Vector3(x, 0, y));
                res.put(new Point2d(x, y), chunk);


                List<Point2d> keys = asList(
                        new Point2d(x, y),
                        new Point2d(x + 1, y),
                        new Point2d(x, y + 1),
                        new Point2d(x + 1, y + 1));

                for (Point2d key : keys) {
                    MazeCell cell = data.get(key);

                    Set<Vector3> voxels = buildMazeCell(cell);
                    Set<Vector3> withOffset = voxels.stream()
                            .map(v -> new Vector3(
                                    v.getX() + key.getX() * 5,
                                    v.getY(),
                                    v.getZ() + key.getY() * 5
                            ))
                            .collect(Collectors.toSet());

                    chunk.getChildren().addAll(withOffset);
                }
            }
        }
        return new HashSet<>(res.values());
    }

    private Set<Vector3> buildMazeCell(MazeCell cell) {

        final int y = 2;
        final int side = 4;

        List<MazeDoorDirection> dir = cell.getDirections();

        Set<Vector3> voxels = new HashSet<>();
        for (int x = 0; x <= side; x++) {
            for (int z = 0; z <= side; z++) {

                if (x == 0 || z == 0 || x == side || z == side) {

                    if (dir.contains(TOP) && z == side && asList(1, 2, 3).contains(x)) continue;
                    if (dir.contains(BOTTOM) && z == 0 && asList(1, 2, 3).contains(x)) continue;
                    if (dir.contains(LEFT) && x == 0 && asList(1, 2, 3).contains(z)) continue;
                    if (dir.contains(RIGHT) && x == side && asList(1, 2, 3).contains(z)) continue;
                    voxels.add(new Vector3(x, y, z));
                }
            }
        }

        return voxels;
    }

    @Override
    public TerrainChunk getFourPassingRoom(int x1, int y1, int z1) {
        TerrainChunk chunk1 = new TerrainChunk();
        chunk1.setPosition(new Vector3(x1, y1, z1));
        Set<Vector3> voxels = new HashSet<>();
        for (int x = 1; x < 11; x++) {
            for (int z = 1; z < 11; z++) {
                for (int y = 1; y < 6; y++) {

                    if ((z > 1 && asList(1, 10).contains(x))
                            && !(asList(5, 6).contains(z) && asList(2, 3, 4).contains(y))) {
                        voxels.add(new Vector3(x, y, z));
                    }
                    if ((x > 1 && asList(1, 10).contains(z))
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

    @Override
    public TerrainChunk getClosedRoom(int x1, int y1, int z1) {
        TerrainChunk chunk1 = new TerrainChunk();
        chunk1.setPosition(new Vector3(x1, y1, z1));
        Set<Vector3> voxels = new HashSet<>();
        for (int x = 1; x < 11; x++) {
            for (int z = 1; z < 11; z++) {
                for (int y = 1; y < 6; y++) {

                    if (z > 1 && asList(1, 10).contains(x)) voxels.add(new Vector3(x, y, z));
                    if (x > 1 && asList(1, 10).contains(z)) voxels.add(new Vector3(x, y, z));
                    if (y == 1) voxels.add(new Vector3(x, y, z));
                }
            }
        }
        chunk1.setChildren(voxels);
        return chunk1;
    }
}

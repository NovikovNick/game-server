package com.metalheart.service.terrain;

import com.metalheart.algorithm.maze.Maze;
import com.metalheart.algorithm.maze.MazeCell;
import com.metalheart.algorithm.maze.MazeDoorDirection;
import com.metalheart.algorithm.maze.RecursiveBacktrackerMazeBuilder;
import com.metalheart.model.logic.GameObject;
import com.metalheart.model.logic.TerrainChunk;
import com.metalheart.model.physic.*;
import com.metalheart.service.TerrainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

import static com.metalheart.algorithm.maze.MazeDoorDirection.*;
import static java.util.Arrays.asList;

@Slf4j
@Component
public class TerrainServiceImpl implements TerrainService {

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


        log.debug("Try to build maze " + data);

        for (int x = 0; x < 6; x += 2) {
            for (int z = 0; z < 6; z += 2) {

                TerrainChunk chunk = new TerrainChunk();
                chunk.setPosition(new Vector3d(x, 0, z));
                chunk.setWalls(new ArrayList<>());
                Transform transform = new Transform(new Vector3d(x, 0, z), Vector3d.UNIT_VECTOR_X);

                res.put(new Point2d(x, z), chunk);

                List<Point2d> keys = asList(
                        new Point2d(x, z),
                        new Point2d(x + 1, z),
                        new Point2d(x, z + 1),
                        new Point2d(x + 1, z + 1));

                Set<Vector3d> wallsCoords = new HashSet<>();

                for (Point2d key : keys) {
                    MazeCell cell = data.get(key);

                    if (cell == null) {
                        continue;
                    }
                    log.debug("Try to build maze with key " + key);

                    Function<Vector3d, Vector3d> addOffset = v -> new Vector3d(
                            v.d0 + key.getD0() * 5,
                            v.d1,
                            v.d2 + key.getD1() * 5
                    );

                    buildMazeCell(cell)
                            .stream()
                            .map(addOffset)
                            .forEach(wallsCoords::add);
                }

                for (Polygon2d polygon2d : TerrainConverUtil.convert(wallsCoords)) {
                    GameObject wall = new GameObject();
                    wall.setTransform(transform);
                    wall.setRigidBody(new RigidBody(polygon2d));
                    chunk.getWalls().add(wall);
                }

                chunk.setWallsCoords(wallsCoords);
            }
        }

        return new HashSet<>(res.values());
    }

    private Set<Vector3d> buildMazeCell(MazeCell cell) {

        final int side = 4;

        List<MazeDoorDirection> dir = cell.getDirections();

        Set<Vector3d> voxels = new HashSet<>();
        for (int x = 0; x <= side; x++) {
            for (int z = 0; z <= side; z++) {

                for (int y = 1; y <= 4; y++) {

                    if (y == 1) {

                        voxels.add(new Vector3d(x, y, z));

                    } else if (x == 0 || z == 0 || x == side || z == side) {

                        if (dir.contains(TOP) && z == side && asList(1, 2, 3).contains(x)) continue;
                        if (dir.contains(BOTTOM) && z == 0 && asList(1, 2, 3).contains(x)) continue;
                        if (dir.contains(LEFT) && x == 0 && asList(1, 2, 3).contains(z)) continue;
                        if (dir.contains(RIGHT) && x == side && asList(1, 2, 3).contains(z)) continue;
                        voxels.add(new Vector3d(x, y, z));
                    }
                }
            }
        }

        return voxels;
    }
}

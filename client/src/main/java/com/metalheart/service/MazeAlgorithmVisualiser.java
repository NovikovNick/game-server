package com.metalheart.service;

import com.metalheart.model.physic.Point2d;
import com.metalheart.algorithm.maze.Maze;
import com.metalheart.algorithm.maze.MazeDoorDirection;
import com.metalheart.algorithm.maze.RecursiveBacktrackerMazeBuilder;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class MazeAlgorithmVisualiser {

    private RecursiveBacktrackerMazeBuilder mazeBuilder = new RecursiveBacktrackerMazeBuilder()
            .setWidth(5)
            .setHeight(5)
            .setEnter(new Point2d(0, 0))
            .setEnterDirection(MazeDoorDirection.LEFT)
            .setExit(new Point2d(5, 4))
            .setExitDirection(MazeDoorDirection.RIGHT);

    @Getter
    private Maze maze = new Maze();

    public Maze step() {

        if (!mazeBuilder.isFinished(maze)) {
            maze = mazeBuilder.buildNextStep(maze);
        }
        return maze;
    }
}

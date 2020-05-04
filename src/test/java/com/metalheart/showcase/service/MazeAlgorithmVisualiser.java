package com.metalheart.showcase.service;

import com.metalheart.model.physic.Point2d;
import com.metalheart.service.maze.Maze;
import com.metalheart.service.maze.MazeDoorDirection;
import com.metalheart.service.maze.RecursiveBacktrackerMazeBuilder;
import lombok.Getter;

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

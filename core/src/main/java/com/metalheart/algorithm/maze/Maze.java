package com.metalheart.algorithm.maze;

import com.metalheart.model.physic.Point2d;
import lombok.Data;

import java.util.Map;
import java.util.Stack;

@Data
public class Maze {

    Stack<Point2d> buildPath;
    Map<Point2d, MazeCell> data;
}

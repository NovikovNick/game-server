package com.metalheart.algorithm.maze;

import com.metalheart.model.physic.Point2d;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;

import static com.metalheart.algorithm.maze.MazeDoorDirection.*;

@Setter
@Accessors(chain = true)
public class RecursiveBacktrackerMazeBuilder {

    private int width;
    private int height;

    private Point2d enter;
    private MazeDoorDirection enterDirection;

    private Point2d exit;
    private MazeDoorDirection exitDirection;

    public Maze buildNextStep(Maze maze) {

        if (maze.getData() == null) {
            maze.setData(new HashMap<>());
            maze.setBuildPath(new Stack<>());
            MazeCell cell = new MazeCell();
            cell.getDirections().add(enterDirection);
            maze.getData().put(enter, cell);
            maze.getBuildPath().push(enter);
            return maze;
        }


        List<MazeDoorDirection> availablePath = getAvailablePath(maze);

        if (!availablePath.isEmpty()) {

            Point2d currentCell = maze.getBuildPath().peek();

            float x = currentCell.getX();
            float y = currentCell.getY();

            Point2d toTop = new Point2d(x, y + 1);
            Point2d toBottom = new Point2d(x, y - 1);
            Point2d toLeft = new Point2d(x - 1, y);
            Point2d toRight = new Point2d(x + 1, y);

            MazeCell cell = new MazeCell();
            MazeDoorDirection randomDirection = availablePath.get(new Random().nextInt(availablePath.size()));



            Point2d nextCell = null;
            switch (randomDirection) {
                case TOP:
                    nextCell = toTop;
                    break;
                case BOTTOM:
                    nextCell = toBottom;
                    break;
                case LEFT:
                    nextCell = toLeft;
                    break;
                case RIGHT:
                    nextCell = toRight;
                    break;
                default:
                    throw new IllegalStateException();
            }
            maze.getData().get(currentCell).getDirections().add(randomDirection);
            cell.getDirections().add(randomDirection.getOpposite());

            if (nextCell.equals(exit)) {
                cell.getDirections().add(exitDirection);
            }

            maze.getData().put(nextCell, cell);
            maze.getBuildPath().push(nextCell);

            return maze;

        } else if (!maze.getBuildPath().isEmpty()) {
            maze.getBuildPath().pop();
        }
        return maze;
    }

    private List<MazeDoorDirection> getAvailablePath(Maze maze) {

        if(maze.getBuildPath().isEmpty()){
            return Collections.emptyList();
        }
        Point2d currentCell = maze.getBuildPath().peek();

        float x = currentCell.getX();
        float y = currentCell.getY();

        Point2d toTop = new Point2d(x, y + 1);
        Point2d toBottom = new Point2d(x, y - 1);
        Point2d toLeft = new Point2d(x - 1, y);
        Point2d toRight = new Point2d(x + 1, y);

        List<MazeDoorDirection> availablePath = new ArrayList<>();
        if (y < height && !maze.getData().containsKey(toTop)) {
            availablePath.add(TOP);
        }
        if (y > 0 && !maze.getData().containsKey(toBottom)) {
            availablePath.add(BOTTOM);
        }
        if (x > 0 && !maze.getData().containsKey(toLeft)) {
            availablePath.add(LEFT);
        }
        if (x < width && !maze.getData().containsKey(toRight)) {
            availablePath.add(RIGHT);
        }
        return availablePath;
    }

    public boolean isFinished(Maze maze) {

        return maze.getData() != null && maze.getData().values().size() == ((width+1) * (height+1));
    }
}

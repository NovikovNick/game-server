package com.metalheart.service.maze;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MazeCell {

    private boolean isEnter;
    private boolean isExit;
    private List<MazeDoorDirection> directions = new ArrayList<>();
}

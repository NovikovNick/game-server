package com.metalheart.repository;

import com.metalheart.model.physic.Polygon2d;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MazeRepository {

    private List<Polygon2d> maze;

    public List<Polygon2d> get() {
        if (maze == null) {

            maze = new ArrayList<>();
        }
        return maze;
    }

    public List<Polygon2d> save(List<Polygon2d> maze) {
        return this.maze = maze;
    }
}

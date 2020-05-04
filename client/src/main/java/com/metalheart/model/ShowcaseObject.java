package com.metalheart.model;

import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowcaseObject {

    private Polygon2d data;
    private Point2d direction;
}

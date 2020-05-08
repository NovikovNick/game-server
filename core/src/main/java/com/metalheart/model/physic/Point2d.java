package com.metalheart.model.physic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;


@Data
@AllArgsConstructor
public class Point2d {

    private final float x;
    private final float y;

    /*@Override
    public String toString() {
        return String.format("p2(%.3f, %.3f)", x, y);
    }*/


    @Override
    public String toString() { return String.format(Locale.US, "new Point2d(%.3ff, %.3ff)", x, y); }

}

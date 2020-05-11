package com.metalheart.model.physic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;


@Data
@AllArgsConstructor
public class Point2d {

    private final float d0;
    private final float d1;

    @Override
    public String toString() { return String.format(Locale.US, "p2(%.3f, %.3f)", d0, d1); }
}

package com.metalheart.model.physic;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import static java.lang.Math.sqrt;

@AllArgsConstructor
@EqualsAndHashCode(of = {"d0", "d1", "d2"})
public class Vector3d {

    public static final Vector3d UNIT_VECTOR_X = new Vector3d(1, 0, 0);
    public static final Vector3d UNIT_VECTOR_Y = new Vector3d(0, 1, 0);
    public static final Vector3d UNIT_VECTOR_Z = new Vector3d(0, 0, 1);

    public final float d0, d1, d2;

    public Vector3d normalize() {
        return scale(1 / magnitude());
    }

    public Vector3d plus(Vector3d v) {
        return new Vector3d(d0 + v.d0, d1 + v.d1, d2 + v.d2);
    }

    public Vector3d scale(float s) {
        return new Vector3d(d0 * s, d1 * s, d2 * s);
    }

    public float magnitude() {
        return (float) sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public float[] toArray() {
        return new float[] {d0, d1, d2};
    }

    @Override
    public String toString() {
        return String.format("v3(%.3f, %.3f, %.3f)", d0, d1, d2);
    }
}

package com.metalheart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static java.lang.Math.sqrt;

/**
 * todo: should be final
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"x", "y", "z"})
public class Vector3 {

    private float x;
    private float y;
    private float z;

    public Vector3 normalize() {
        return scale(1 / magnitude());
    }

    public Vector3 plus(Vector3 v) {
        return new Vector3(x + v.getX(), y + v.getY(), z + v.getZ());
    }

    public Vector3 scale(float s) {
        return new Vector3(x * s, y * s, z * s);
    }

    public float magnitude() {
        return (float) sqrt(x * x + y * y + z * z);
    }

    @Override
    public String toString() {
        return String.format("v3(%.3f, %.3f, %.3f)", x, y, z);
    }
}

package com.metalheart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
}

package com.metalheart.model.physic;

import java.util.Collections;
import java.util.List;

import static com.metalheart.model.physic.Vertex.*;
import static java.util.Arrays.asList;

/**
 *
 * Right-handed cartesian coordinates
 *
 <pre>
      y
      |
      v8-------v7
     /|       /|
    / |      / |
  v5-------v6  |
   |  |     |  |
   |  v4----| v3 - - x
   | /      | /
   |/       |/
   v1-------v2
  /
 z
 </pre>
 */
public enum VoxelFace {

    FRONT   (new Vector3d(  0,   0,   1), asList(V1, V2, V6, V5)),
    BACK    (new Vector3d(  0,   0,  -1), asList(V3, V4, V8, V7)),
    LEFT    (new Vector3d( -1,   0,   0), asList(V4, V1, V5, V8)),
    RIGHT   (new Vector3d(  1,   0,   0), asList(V2, V3, V7, V6)),
    TOP     (new Vector3d(  0,   1,   0), asList(V5, V6, V7, V8)),
    BOTTOM  (new Vector3d(  0,  -1,   0), asList(V4, V3, V2, V1));

    public final Vector3d direction;
    public final List<Vertex> vertices;

    VoxelFace(Vector3d direction, List<Vertex> vertices) {
        this.vertices = Collections.unmodifiableList(vertices);
        this.direction = direction;
    }
}
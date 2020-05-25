package com.metalheart.model.physic;

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
public enum Vertex {
    V1(0, 0, 1),
    V2(1, 0, 1),
    V3(1, 0, 0),
    V4(0, 0, 0),
    V5(0, 1, 1),
    V6(1, 1, 1),
    V7(1, 1, 0),
    V8(0, 1, 0);

    public final int d0, d1, d2;

    Vertex(int d0, int d1, int d2) {
        this.d0 = d0;
        this.d1 = d1;
        this.d2 = d2;
    }

    public Vector3d toVector3d() {
        return new Vector3d(d0, d1, d2);
    }
}
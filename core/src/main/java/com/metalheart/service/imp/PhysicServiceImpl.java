package com.metalheart.service.imp;

import com.metalheart.math.PhysicUtil;
import com.metalheart.model.logic.GameObject;
import com.metalheart.model.logic.State;
import com.metalheart.model.logic.TerrainChunk;
import com.metalheart.model.physic.*;
import com.metalheart.service.PhysicService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PhysicServiceImpl implements PhysicService {


    @Override
    public State step(State state) {

        Map<GameObject, List<Force>> gameObjectsForces = state.getGameObjectsForces();

        gameObjectsForces.forEach((gameObject, forces) -> {

            Transform transform = gameObject.getTransform();

            for (Force f : forces) {

                Vector3d direction = f.getDirection();
                float magnitude = f.getMagnitude();
                float dT = f.getTimeDelta();

                Vector3d pos = transform.getPosition().plus(direction.scale(magnitude * dT));

                for (TerrainChunk terrainChunk : state.getTerrainChunks()) {
                    for (GameObject wall : terrainChunk.getWalls()) {

                        Polygon2d playerCollider = gameObject.getRigidBody().getShape().withOffset(pos);
                        Polygon2d wallCollider = wall.getRigidBody().getShape();

                        CollisionResult r = PhysicUtil.detectCollision(wallCollider, playerCollider);
                        if (r.isCollide()) {
                            float depth = r.getDepth();
                            pos = new Vector3d(
                                    pos.d0 + depth * r.getNormal().d0,
                                    pos.d1 + depth * r.getNormal().d2,
                                    pos.d2 + depth * r.getNormal().d1
                            );
                        }
                    }
                }
                transform.setPosition(pos);
                transform.setRotation(direction);
            }
        });

        state.getGameObjectsForces().clear();
        return state;
    }
}

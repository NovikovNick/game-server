package com.metalheart.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Domain {

    private int fps = 30;
    private float walkSpeed = 2;
    private float runSpeed = 6;

    public Domain(int fps) {
        this.fps = fps;
    }

    private Map<Byte, PlayerRequest> requests = new HashMap<>();
    private Map<Byte, PlayerSnapshot> snapshots = new HashMap<>();

    public void playerInput(PlayerRequest request) {
        requests.put(request.getPlayerId(), request);
    }

    public void processNextFrame() {
        requests.forEach((playerId, request) -> {
            if (request != null) {

                Vector3 direction = request.getDirection();
                float multiplier = (request.getIsRunning() ? runSpeed : walkSpeed)
                        * request.getMagnitude()
                        * 1 / fps;

                PlayerSnapshot snapshot = getPlayerSnapshot(playerId);
                Vector3 previousPosition = snapshot.getPosition();
                Vector3 newPosition = new Vector3(
                        previousPosition.getX() + multiplier * direction.getX(),
                        previousPosition.getY() + multiplier * direction.getY(),
                        previousPosition.getZ() + multiplier * direction.getZ()
                );

                snapshot.setLastDatagramNumber(request.getDatagramNumber());
                snapshot.setDirection(direction);
                snapshot.setPosition(newPosition);

                snapshots.put(playerId, snapshot);
                requests.put(playerId, null);
            }
        });
    }


    public PlayerSnapshot getPlayerSnapshot(byte playerId) {
        if (!snapshots.containsKey(playerId)) {

            PlayerSnapshot snapshot = new PlayerSnapshot();
            snapshot.setPlayerId(playerId);
            snapshot.setPosition(new Vector3(5.41f, 0.55f, 7.0f));
            snapshot.setDirection(new Vector3(1.0f, 0.0f, 0.0f));

            snapshots.put(playerId, snapshot);
        }
        return snapshots.get(playerId);
    }
}

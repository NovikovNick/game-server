package com.metalheart.service.imp;

import com.metalheart.model.GameObject;
import com.metalheart.model.PlayerInput;
import com.metalheart.model.State;
import com.metalheart.model.Vector3;
import com.metalheart.service.GameStateService;
import com.metalheart.service.TerrainService;
import com.metalheart.service.TransportLayer;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;

@Data
public class GameStateServiceImpl implements GameStateService {

    private TerrainService terrainService;
    private TransportLayer transportLayer;
    private int tickRate;
    private float walkSpeed = 2;
    private float runSpeed = 6;

    private State state;

    public GameStateServiceImpl(TransportLayer transportLayer, TerrainService terrainService, int tickRate) {

        this.transportLayer = transportLayer;
        this.terrainService = terrainService;
        this.tickRate = tickRate;
        this.state = new State();
        this.state.setTerrainChunks(terrainService.generateSimpleRoom());
    }

    @Override
    public void calculateState() {
        Instant t0 = Instant.now();
        try {
            transportLayer.getPlayerInputs().forEach((player, inputs) -> {

                if (!state.getPlayers().containsKey(player)) {
                    GameObject playerState = new GameObject();
                    playerState.setPosition(new Vector3(1f, 1f, 1f));
                    playerState.setRotation(new Vector3(1.0f, 0.0f, 0.0f));
                    state.getPlayers().put(player, playerState);
                }
                GameObject playerState = state.getPlayers().get(player);

                Vector3 newPosition = playerState.getPosition();
                Vector3 direction = playerState.getRotation();

                PlayerInput input;
                while ((input = inputs.poll()) != null) {
                    direction = input.getDirection();
                    float speed = input.getIsRunning() ? runSpeed : walkSpeed;
                    float multiplier = round(speed
                            * input.getMagnitude()
                            * input.getTimeDelta());
                    newPosition = new Vector3(
                            newPosition.getX() + round(multiplier * direction.getX()),
                            newPosition.getY() + round(multiplier * direction.getY()),
                            newPosition.getZ() + round(multiplier * direction.getZ())
                    );
                }
                playerState.setPosition(newPosition);
                playerState.setRotation(direction);
            });

            System.out.println(Duration.between(t0, Instant.now()).toMillis() + "ms " + state);

            transportLayer.sendSnapshot(state);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static float round(float value) {
        return Math.round(value * 10000) / 10000f;
    }
}

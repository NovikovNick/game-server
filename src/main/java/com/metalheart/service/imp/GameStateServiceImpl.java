package com.metalheart.service.imp;

import com.metalheart.configuration.GameProperties;
import com.metalheart.model.GameObject;
import com.metalheart.model.PlayerInput;
import com.metalheart.model.State;
import com.metalheart.model.Vector3;
import com.metalheart.service.GameStateService;
import com.metalheart.service.TerrainService;
import com.metalheart.service.TransportLayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Slf4j
@Component
public class GameStateServiceImpl implements GameStateService {

    @Autowired
    private GameProperties props;

    @Autowired
    private TerrainService terrainService;

    @Autowired
    private TransportLayer transportLayer;

    private long stateNumber;
    private State state;

    @PostConstruct
    public void init() {
        this.state = new State();
        this.state.setTerrainChunks(terrainService.generateSimpleRoom());
    }

    @Override
    public State calculateState() {
        Instant t0 = Instant.now();
        try {
            transportLayer.getPlayerInputs().forEach((player, inputs) -> {

                if (!state.getPlayers().containsKey(player)) {
                    GameObject playerState = new GameObject();
                    playerState.setPosition(new Vector3(2f, 1f, 2f));
                    playerState.setRotation(new Vector3(1.0f, 0.0f, 0.0f));
                    state.getPlayers().put(player, playerState);
                }
                GameObject playerState = state.getPlayers().get(player);

                Vector3 newPosition = playerState.getPosition();
                Vector3 direction = playerState.getRotation();

                PlayerInput input;
                while ((input = inputs.poll()) != null) {
                    direction = input.getDirection();
                    float speed = input.getIsRunning() ? props.getRunSpeed() : props.getWalkSpeed();
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

            if(stateNumber++ % 100 == 0) {
                this.state.setTerrainChunks(terrainService.generateRandomRoom());
                log.info("Rebuild room!");
            }
            //log.info(Duration.between(t0, Instant.now()).toMillis() + "ms " + state);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }

    private static float round(float value) {
        return Math.round(value * 10000) / 10000f;
    }
}

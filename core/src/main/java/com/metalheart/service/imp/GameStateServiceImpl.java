package com.metalheart.service.imp;

import com.metalheart.configuration.GameProperties;
import com.metalheart.model.*;
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
        this.state.setTerrainChunks(terrainService.getCubes(0,0,0));
    }

    @Override
    public State calculateState() {
        Instant t0 = Instant.now();
        try {
            transportLayer.getPlayerInputs().forEach((playerId, inputs) -> {

                if (!state.getPlayers().containsKey(playerId)) {
                    Player player = new Player();
                    player.setPosition(new Vector3(6f, 1f, 5f));
                    player.setRotation(new Vector3(1.0f, 0.0f, 0.0f));
                    state.getPlayers().put(playerId, player);
                }
                Player player = state.getPlayers().get(playerId);

                Vector3 newPosition = player.getPosition();
                Vector3 direction = player.getRotation();

                PlayerInput input;
                float speed = 0;
                while ((input = inputs.poll()) != null) {
                    direction = input.getDirection();
                    speed = (input.getIsRunning() ? props.getRunSpeed() : props.getWalkSpeed()) * input.getMagnitude();
                    float multiplier = round(speed * input.getTimeDelta());
                    newPosition = new Vector3(
                            newPosition.getX() + round(multiplier * direction.getX()),
                            newPosition.getY() + round(multiplier * direction.getY()),
                            newPosition.getZ() + round(multiplier * direction.getZ())
                    );
                }
                player.setPosition(newPosition);
                player.setRotation(direction);
                player.setSpeed(speed);
            });

            /*if(stateNumber++ % 100 == 0) {
                this.state.setTerrainChunks(terrainService.generateRandomRoom());
                log.info("Rebuild room!");
            }*/
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

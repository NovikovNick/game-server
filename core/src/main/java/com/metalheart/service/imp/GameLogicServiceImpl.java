package com.metalheart.service.imp;

import com.metalheart.configuration.GameProperties;
import com.metalheart.model.logic.Player;
import com.metalheart.model.logic.State;
import com.metalheart.model.logic.TerrainChunk;
import com.metalheart.model.physic.*;
import com.metalheart.model.transport.PlayerInput;
import com.metalheart.model.transport.Vector3;
import com.metalheart.service.GameLogicService;
import com.metalheart.service.PhysicService;
import com.metalheart.service.TerrainService;
import com.metalheart.service.TransportLayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

@Slf4j
@Component
public class GameLogicServiceImpl implements GameLogicService {

    @Autowired
    private GameProperties props;

    @Autowired
    private TerrainService terrainService;

    @Autowired
    private TransportLayer transportLayer;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private PhysicService physicService;

    private State state;

    @PostConstruct
    public void init() {
        this.state = new State();
        Set<TerrainChunk> terrainChunks = terrainService.generateMaze();
        this.state.setTerrainChunks(terrainChunks);
    }



    @Override
    public State calculateState() {
        try {
            transportLayer.getPlayerInputs().forEach((playerId, inputs) -> {

                Player player = getPlayer(playerId);

                List<Force> forces = new ArrayList<>();
                PlayerInput input;
                float speed = 0;
                while ((input = inputs.poll()) != null) {
                    Vector3 d = input.getDirection();
                    speed = (input.getIsRunning() ? props.getRunSpeed() : props.getWalkSpeed()) * input.getMagnitude();
                    forces.add(new Force(player, new Vector3d(d.getX(), d.getY(), d.getZ()), speed, input.getTimeDelta()));
                    player.setSpeed(speed);
                }
                state.getGameObjectsForces().put(player, forces);
            });

            this.state = physicService.step(state);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }

    @Override
    public State getState() {
        return state;
    }

    private Player getPlayer(InetSocketAddress playerId) {

        if (!state.getPlayers().containsKey(playerId)) {
            Player player = new Player();
            player.setTransform(new Transform(new Vector3d(6f, 1f, 5f), Vector3d.UNIT_VECTOR_X));
            player.setRigidBody(new RigidBody(new Polygon2d(asList(
                    new Point2d(0, 1),
                    new Point2d(1, 1),
                    new Point2d(1, 0),
                    new Point2d(0, 0)
            ))));
            state.getPlayers().put(playerId, player);
        }
        return state.getPlayers().get(playerId);
    }

    private static float round(float value) {
        return Math.round(value * 10000) / 10000f;
    }
}

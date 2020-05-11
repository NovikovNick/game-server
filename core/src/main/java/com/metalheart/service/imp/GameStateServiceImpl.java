package com.metalheart.service.imp;

import com.metalheart.configuration.GameProperties;
import com.metalheart.model.*;
import com.metalheart.model.physic.CollisionResult;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Polygon2d;
import com.metalheart.service.GameStateService;
import com.metalheart.service.PhysicUtil;
import com.metalheart.service.TerrainService;
import com.metalheart.service.TransportLayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

@Slf4j
@Component
public class GameStateServiceImpl implements GameStateService {

    @Autowired
    private GameProperties props;

    @Autowired
    private TerrainService terrainService;

    @Autowired
    private TransportLayer transportLayer;

    @Autowired
    private ConversionService conversionService;

    private long stateNumber;
    private State state;

    List<Polygon2d> polygons;

    @PostConstruct
    public void init() {
        this.state = new State();
        Set<TerrainChunk> terrainChunks = terrainService.generateMaze();
        this.state.setTerrainChunks(terrainChunks);

        TypeDescriptor sourceType = TypeDescriptor.valueOf(TerrainChunk.class);
        TypeDescriptor targetType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Polygon2d.class));

        this.polygons = new ArrayList<>();
        for (TerrainChunk terrainChunk : terrainChunks) {
            this.polygons.addAll((List<Polygon2d>) conversionService.convert(terrainChunk, sourceType, targetType));
        }
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

                for (Polygon2d polygon2d : polygons) {

                    Polygon2d playerPolygon = new Polygon2d(new ArrayList<>(asList(
                            new Point2d(newPosition.getX() - 0.1f, newPosition.getZ() - 0.1f),
                            new Point2d(newPosition.getX() - 0.1f, newPosition.getZ() + 0.1f),
                            new Point2d(newPosition.getX() + 0.1f, newPosition.getZ() + 0.1f),
                            new Point2d(newPosition.getX() + 0.1f, newPosition.getZ() - 0.1f)
                    )));

                    CollisionResult r = PhysicUtil.detectCollision(polygon2d, playerPolygon);
                    if (r.isCollide()) {

                        float m = r.getDepth();
                        newPosition = new Vector3(
                                newPosition.getX() + round(m * r.getNormal().getX()),
                                newPosition.getY() + round(m * r.getNormal().getZ()),
                                newPosition.getZ() + round(m * r.getNormal().getY())
                        );
                    }
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

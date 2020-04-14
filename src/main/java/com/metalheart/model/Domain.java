package com.metalheart.model;

import com.metalheart.converter.PlayerResponseConverter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import lombok.Data;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

@Data
public class Domain {

    private int tickRate;
    private float walkSpeed = 2;
    private float runSpeed = 6;

    private Channel channel;

    private final String host = "192.168.0.102";
    private PlayerResponseConverter responseConverter = new PlayerResponseConverter();

    private Map<InetSocketAddress, Instant> playerLastInputAt = new HashMap<>();
    private Map<InetSocketAddress, Set<PlayerInput>> playerInputs = new HashMap<>();
    private Map<InetSocketAddress, Integer> playerAcknowledgmentNumber = new HashMap<>();
    private State state;
    private Map<InetSocketAddress, PlayerSnapshot> snapshots = new HashMap<>();
    private int sequenceNumber = 0;

    public Domain(int tickRate) {
        this.tickRate = tickRate;
        this.state = new State();


        Set<TerrainChunk> terrainChunks = new HashSet<>();
        terrainChunks.add(getTerrainChunk(0, 0, 0));
        terrainChunks.add(getTerrainChunk(1, 0, 0));
        terrainChunks.add(getTerrainChunk(2, 0, 0));

        terrainChunks.add(getTerrainChunk(0, 0, 1));
        terrainChunks.add(getTerrainChunk(1, 0, 1));
        terrainChunks.add(getTerrainChunk(2, 0, 1));

        terrainChunks.add(getTerrainChunk(0, 0, 2));
        terrainChunks.add(getTerrainChunk(1, 0, 2));
        terrainChunks.add(getTerrainChunk(2, 0, 2));
        this.state.setTerrainChunks(terrainChunks);
    }

    private TerrainChunk getTerrainChunk(int x1, int y1, int z1) {
        TerrainChunk chunk1 = new TerrainChunk();
        chunk1.setPosition(new Vector3(x1, y1, z1));
        Set<Vector3> voxels = new HashSet<>();
        for (int x = 1; x < 11; x++) {
            for (int z = 1; z < 11; z++) {
                for (int y = 1; y < 6; y++) {

                    if((z > 1 && asList(1, 10).contains(x)) && !(asList(5, 6).contains(z) && asList(2, 3, 4).contains(y))) {
                        voxels.add(new Vector3(x, y, z));
                    }
                    if((x > 1 && asList(1, 10).contains(z)) && !(asList(5, 6).contains(x) && asList(2, 3, 4).contains(y))) {
                        voxels.add(new Vector3(x, y, z));
                    }

                    if (y == 1) voxels.add(new Vector3(x, y, z));
                }
            }
        }
        chunk1.setChildren(voxels);
        return chunk1;
    }

    public void playerInput(InetSocketAddress address, PlayerInput request) {
        playerLastInputAt.put(address, Instant.now());
        if (!playerInputs.containsKey(address)) {
            playerInputs.put(address, new TreeSet<>(Comparator.comparingInt(PlayerInput::getSequenceNumber)));
        }
        playerAcknowledgmentNumber.put(address, request.getSequenceNumber());
        playerInputs.get(address).add(request);
    }

    public void tick() {
        Instant t0 = Instant.now();
        try {
            new HashMap<>(playerInputs).forEach((player, inputs) -> {

                Queue<PlayerInput> queue = new ArrayDeque<>(inputs);
                inputs.clear();

                if (!state.getOtherPlayers().containsKey(player)) {
                    GameObject playerState = new GameObject();
                    playerState.setPosition(new Vector3(1f, 1f, 1f));
                    playerState.setRotation(new Vector3(1.0f, 0.0f, 0.0f));
                    state.getOtherPlayers().put(player, playerState);
                }
                GameObject playerState = state.getOtherPlayers().get(player);

                Vector3 newPosition = playerState.getPosition();
                Vector3 direction = playerState.getRotation();

                PlayerInput input;
                while ((input = queue.poll()) != null) {
                    direction = input.getDirection();
                    float speed = input.getIsRunning() ? runSpeed : walkSpeed;
                    float multiplier = round(speed
                            *  input.getMagnitude()
                            *  input.getTimeDelta());
                    newPosition = new Vector3(
                            newPosition.getX() + round(multiplier * direction.getX()),
                            newPosition.getY() + round(multiplier * direction.getY()),
                            newPosition.getZ() + round(multiplier * direction.getZ())
                            );
                }

                playerState.setPosition(newPosition);
                playerState.setRotation(direction);

                PlayerSnapshot snapshot = new PlayerSnapshot();
                snapshot.setTimestamp(t0.toEpochMilli());
                snapshot.setSequenceNumber(sequenceNumber);
                snapshot.setAcknowledgmentNumber(playerAcknowledgmentNumber.get(player));
                snapshot.setPlayer(playerState);

                snapshot.setTerrainChunks(state.getTerrainChunks());

                snapshots.put(player, snapshot);
            });

            snapshots.forEach((player, snapshot) -> {
                Collection<GameObject> otherPlayers = state.getOtherPlayers().entrySet().stream()
                        .filter(map -> !map.getKey().equals(player))
                        .map(Map.Entry::getValue)
                        .collect(toSet());
                snapshot.setOtherPlayers(otherPlayers);
            });

            sequenceNumber++;
            System.out.println(Duration.between(t0, Instant.now()).toMillis() + "ms " + snapshots);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyPlayers() {

        Duration expiredDelay = Duration.of(2, ChronoUnit.SECONDS);
        Instant now = Instant.now();

        snapshots.forEach((player, snapshot) -> {
            Instant lastInputAt = playerLastInputAt.get(player);
            if(lastInputAt != null && Duration.between(lastInputAt, now).compareTo(expiredDelay) < 0) {
                send(player, responseConverter.convert(snapshot));
            } else {
                playerInputs.remove(player);
                snapshots.remove(player);
                playerLastInputAt.remove(player);
            }
        });
    }

    private void send(InetSocketAddress address, ByteBuf buf) {
        try {
            DatagramPacket packet = new DatagramPacket(buf, address);
            channel.writeAndFlush(packet).addListener(future -> {
                if(!future.isSuccess()) {
                    future.cause().printStackTrace();
                }
            } );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static float round(float value) {
        return Math.round(value * 10000) / 10000f;
    }
}

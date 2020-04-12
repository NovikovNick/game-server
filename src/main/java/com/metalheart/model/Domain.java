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
    private Map<InetSocketAddress, GameObject> state = new HashMap<>();
    private Map<InetSocketAddress, PlayerSnapshot> snapshots = new HashMap<>();


    public Domain(int tickRate) {
        this.tickRate = tickRate;
    }

    public void playerInput(InetSocketAddress address, PlayerInput request) {

        playerLastInputAt.put(address, Instant.now());
        if (!playerInputs.containsKey(address)) {
            playerInputs.put(address, new TreeSet<>(Comparator.comparingInt(PlayerInput::getDatagramNumber)));
        }
        playerInputs.get(address).add(request);
    }

    public void tick() {
        Instant t0 = Instant.now();
        try {
            new HashMap<>(playerInputs).forEach((player, inputs) -> {

                Queue<PlayerInput> queue = new ArrayDeque<>(inputs);
                inputs.clear();

                if (!state.containsKey(player)) {
                    GameObject playerState = new GameObject();
                    playerState.setPosition(new Vector3(5.41f, 0.55f, 7.0f));
                    playerState.setRotation(new Vector3(1.0f, 0.0f, 0.0f));
                    state.put(player, playerState);
                }
                GameObject playerState = state.get(player);


                Integer datagramNumber = null;
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
                    datagramNumber = input.getDatagramNumber();
                }

                playerState.setPosition(newPosition);
                playerState.setRotation(direction);


                PlayerSnapshot snapshot = new PlayerSnapshot();
                snapshot.setTimestamp(t0.toEpochMilli());
                snapshot.setLastDatagramNumber(datagramNumber);
                snapshot.setPlayer(playerState);
                snapshots.put(player, snapshot);
            });

            snapshots.forEach((player, snapshot) -> {
                Collection<GameObject> otherPlayers = state.entrySet().stream()
                        .filter(map -> !map.getKey().equals(player))
                        .map(Map.Entry::getValue)
                        .collect(toSet());
                snapshot.setOtherPlayers(otherPlayers);
            });

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

package com.metalheart.model;

import com.metalheart.converter.PlayerResponseConverter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import lombok.Data;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Data
public class Domain {

    private int tickRate;
    private float walkSpeed = 2;
    private float runSpeed = 6;

    private Channel channel;

    private final String host = "192.168.0.102";
    private PlayerResponseConverter responseConverter = new PlayerResponseConverter();

    private Map<InetSocketAddress, Set<PlayerInput>> playerInputs = new HashMap<>();
    private Map<InetSocketAddress, PlayerSnapshot> snapshots = new HashMap<>();
    private Map<InetSocketAddress, Instant> playerLastInputAt = new HashMap<>();

    private DatagramSocket datagramSocket;

    public Domain(int tickRate) {

        try {
            datagramSocket = new DatagramSocket(7778);
        } catch (SocketException e) {
            e.printStackTrace();
        }
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

                PlayerSnapshot snapshot = getPlayerSnapshot(player);
                Vector3 newPosition = snapshot.getPosition();

                PlayerInput input;
                while ((input = queue.poll()) != null) {
                    Vector3 direction = input.getDirection();
                    float speed = input.getIsRunning() ? runSpeed : walkSpeed;
                    float multiplier = round(speed
                            *  input.getMagnitude()
                            *  input.getTimeDelta());

                    newPosition = new Vector3(
                            newPosition.getX() + round(multiplier * direction.getX()),
                            newPosition.getY() + round(multiplier * direction.getY()),
                            newPosition.getZ() + round(multiplier * direction.getZ())
                            );

                    snapshot.setLastDatagramNumber(input.getDatagramNumber());
                    snapshot.setDirection(direction);
                }
                snapshot.setPosition(newPosition);
                snapshots.put(player, snapshot);
            });
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

    public PlayerSnapshot getPlayerSnapshot(InetSocketAddress address) {
        if (!snapshots.containsKey(address)) {

            PlayerSnapshot snapshot = new PlayerSnapshot();
            snapshot.setPlayerId((byte) snapshots.size());
            snapshot.setPosition(new Vector3(5.41f, 0.55f, 7.0f));
            snapshot.setDirection(new Vector3(1.0f, 0.0f, 0.0f));

            snapshots.put(address, snapshot);
        }
        return snapshots.get(address);
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

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
import java.util.HashMap;
import java.util.Map;

@Data
public class Domain {

    private int tickRate;
    private float walkSpeed = 2;
    private float runSpeed = 6;

    private Channel channel;

    private final String host = "192.168.0.102";
    private PlayerResponseConverter responseConverter = new PlayerResponseConverter();

    private Map<InetSocketAddress, PlayerInput> playerInputs = new HashMap<>();
    private Map<InetSocketAddress, PlayerSnapshot> snapshots = new HashMap<>();
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

        /*if (!playerIntputs.containsKey(address)) {
            playerIntputs.put(address, new TreeSet<>(Comparator.comparingInt(PlayerInput::getDatagramNumber)));
        }
        playerIntputs.get(address).add(request);*/
        System.out.println("input: " + request);
        playerInputs.put(address, request);
    }

    public void tick() {
        Instant t0 = Instant.now();
        playerInputs.forEach((player, input) -> {

            Vector3 direction = input.getDirection();
            float multiplier = (input.getIsRunning() ? runSpeed : walkSpeed)
                    * input.getMagnitude()
                    * 1 / tickRate;

            PlayerSnapshot snapshot = getPlayerSnapshot(player);
            Vector3 previousPosition = snapshot.getPosition();
            Vector3 newPosition = new Vector3(
                    previousPosition.getX() + multiplier * direction.getX(),
                    previousPosition.getY() + multiplier * direction.getY(),
                    previousPosition.getZ() + multiplier * direction.getZ()
            );

            snapshot.setLastDatagramNumber(input.getDatagramNumber());
            snapshot.setDirection(direction);
            snapshot.setPosition(newPosition);
            snapshots.put(player, snapshot);
        });
        System.out.println(Duration.between(t0, Instant.now()).toMillis() + "ms to calculate tick");
    }

    public void notifyPlayers() {
        snapshots.forEach((player, snapshot) -> {
            System.out.println("output: " + snapshot);
            send(player, responseConverter.convert(snapshot));
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
}

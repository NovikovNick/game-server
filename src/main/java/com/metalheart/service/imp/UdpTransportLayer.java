package com.metalheart.service.imp;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import com.metalheart.service.SnapshotService;
import com.metalheart.service.TransportLayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class UdpTransportLayer implements TransportLayer {

    private int sequenceNumber = 0;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private SnapshotService snapshotService;

    private Map<InetSocketAddress, Instant> playerLastInputAt = new HashMap<>();
    private Map<InetSocketAddress, Set<PlayerInput>> playerInputs = new HashMap<>();
    private Map<InetSocketAddress, Integer> playerSequenceNumber = new HashMap<>();
    private Map<InetSocketAddress, Integer> playerAcknowledgmentNumber = new HashMap<>();

    private Map<InetSocketAddress, PlayerSnapshot[]> snapshots = new HashMap<>();

    private Channel channel;

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Map<InetSocketAddress, Queue<PlayerInput>> getPlayerInputs() {

        Map<InetSocketAddress, Queue<PlayerInput>> result = new HashMap<>();
        playerInputs.forEach((playerId, inputs) -> {
            result.put(playerId, new ArrayDeque<>(inputs));
            inputs.clear();
        });
        return result;
    }

    @Override
    public void addPlayerInput(InetSocketAddress playerId, PlayerInput input) {
        playerLastInputAt.put(playerId, Instant.now());
        if (!playerInputs.containsKey(playerId)) {

            playerInputs.put(playerId, new TreeSet<>(Comparator.comparingInt(PlayerInput::getSequenceNumber)));

            PlayerSnapshot[] playerSnapshots = new PlayerSnapshot[32];
            for (int i = 0; i < 32; i++) {
                playerSnapshots[i] = snapshotService.getDummySnapshot();
            }
            snapshots.put(playerId, playerSnapshots);
        }
        playerSequenceNumber.put(playerId, input.getSequenceNumber());
        playerAcknowledgmentNumber.put(playerId, input.getAcknowledgmentNumber());
        playerInputs.get(playerId).add(input);
    }

    @Override
    public Map<InetSocketAddress, PlayerSnapshot> calculateSnapshots(State state) {

        Map<InetSocketAddress, PlayerSnapshot> result = new HashMap<>();

        state.getPlayers().keySet().forEach(playerId -> {

            // make current snapshot
            PlayerSnapshot masterSnapshot = snapshotService.getSnapshot(playerId, state);


            // save current snapshot
            PlayerSnapshot[] playerSnapshots = snapshots.get(playerId);
            playerSnapshots[sequenceNumber % 32] = masterSnapshot;

            // calculate delta
            Integer playerAck = playerAcknowledgmentNumber.get(playerId);
            PlayerSnapshot delta = masterSnapshot;
            for (int i = sequenceNumber - 1; i > sequenceNumber - 32 ; i--) {

                int snapshotIndex = Math.abs(i % 32);

                PlayerSnapshot s = playerSnapshots[snapshotIndex];

                delta = snapshotService.getDelta(delta, s);

                if(playerAck != null && s.getSequenceNumber() == playerAck) {
                    break;
                }
            }

            delta.setSequenceNumber(sequenceNumber);
            delta.setAcknowledgmentNumber(playerSequenceNumber.get(playerId));
            result.put(playerId, delta);
        });
        sequenceNumber++;

        return result;
    }

    @Override
    public void notifyPlayers(Map<InetSocketAddress, PlayerSnapshot> snapshots) {
        Duration expiredDelay = Duration.of(2, ChronoUnit.SECONDS);
        Instant now = Instant.now();
        snapshots.forEach((playerId, snapshot) -> {
            Instant lastInputAt = playerLastInputAt.get(playerId);
            if (lastInputAt != null && Duration.between(lastInputAt, now).compareTo(expiredDelay) < 0) {
                send(playerId, conversionService.convert(snapshot, ByteBuf.class));
            } else {
                playerInputs.remove(playerId);
                snapshots.remove(playerId);
                playerLastInputAt.remove(playerId);
            }
        });
    }

    private void send(InetSocketAddress address, ByteBuf buf) {
        try {
            DatagramPacket packet = new DatagramPacket(buf, address);
            channel.writeAndFlush(packet).addListener(future -> {
                if (!future.isSuccess()) {
                    future.cause().printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.metalheart.service.imp;

import com.metalheart.model.GameObject;
import com.metalheart.model.PlayerInput;
import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
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

import static java.util.stream.Collectors.toSet;

@Component
public class UdpTransportLayer implements TransportLayer {

    private int sequenceNumber = 0;

    @Autowired
    private ConversionService conversionService;

    private Map<InetSocketAddress, Instant> playerLastInputAt = new HashMap<>();
    private Map<InetSocketAddress, Set<PlayerInput>> playerInputs = new HashMap<>();
    private Map<InetSocketAddress, Integer> playerSequenceNumber = new HashMap<>();
    private Map<InetSocketAddress, Integer> playerAcknowledgmentNumber = new HashMap<>();

    private Map<InetSocketAddress, Deque<PlayerSnapshot>> snapshots = new HashMap<>();

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

            /*
            PlayerSnapshot dummySnapshot = getDummySnapshot();

            Deque<PlayerSnapshot> playerSnapshots = new ArrayDeque<>();
            playerSnapshots.addLast(dummySnapshot);
            snapshots.put(address, playerSnapshots);
            */
        }
        playerSequenceNumber.put(playerId, input.getSequenceNumber());
        playerAcknowledgmentNumber.put(playerId, input.getAcknowledgmentNumber());
        playerInputs.get(playerId).add(input);
    }

    @Override
    public void sendSnapshot(State state) {
        Duration expiredDelay = Duration.of(2, ChronoUnit.SECONDS);
        Instant now = Instant.now();

        state.getPlayers().keySet().forEach(playerId -> {
            Integer playerAck = playerAcknowledgmentNumber.get(playerId);
            /*while (playerAck != null && snapshots.peekFirst().getSequenceNumber() < playerAck) {
                snapshots.removeFirst();
            }*/

            PlayerSnapshot snapshot = getSnapshot(playerId, state);
            snapshot.setSequenceNumber(sequenceNumber);
            snapshot.setAcknowledgmentNumber(playerSequenceNumber.get(playerId));
            //snapshots.addLast(masterSnapshot);

            Instant lastInputAt = playerLastInputAt.get(playerId);
            if (lastInputAt != null && Duration.between(lastInputAt, now).compareTo(expiredDelay) < 0) {
                send(playerId, conversionService.convert(snapshot, ByteBuf.class));
            } else {
                playerInputs.remove(playerId);
                snapshots.remove(playerId);
                playerLastInputAt.remove(playerId);
            }
        });
        sequenceNumber++;
    }

    private PlayerSnapshot getSnapshot(InetSocketAddress playerId, State state) {
        Map<InetSocketAddress, GameObject> players = state.getPlayers();

        PlayerSnapshot snapshot = new PlayerSnapshot();
        snapshot.setTimestamp(Instant.now().toEpochMilli());
        snapshot.setPlayer(players.get(playerId));
        snapshot.setOtherPlayers(players.entrySet().stream()
                .filter(map -> !map.getKey().equals(playerId))
                .map(Map.Entry::getValue)
                .collect(toSet()));
        snapshot.setTerrainChunks(state.getTerrainChunks());
        return snapshot;
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

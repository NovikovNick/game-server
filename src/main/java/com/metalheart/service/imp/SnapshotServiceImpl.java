package com.metalheart.service.imp;

import com.metalheart.model.GameObject;
import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import com.metalheart.service.SnapshotService;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

public class SnapshotServiceImpl implements SnapshotService {

    @Override
    public PlayerSnapshot getDummySnapshot() {
        return new PlayerSnapshot();
    }

    @Override
    public PlayerSnapshot getSnapshot(InetSocketAddress playerId, State state) {

        Map<InetSocketAddress, GameObject> players = state.getPlayers();

        PlayerSnapshot snapshot = new PlayerSnapshot();
        snapshot.setTimestamp(Instant.now().toEpochMilli());
        // snapshot.setSequenceNumber(sequenceNumber);
        // snapshot.setAcknowledgmentNumber(playerSequenceNumber.get(playerId));

        snapshot.setPlayer(players.get(playerId));
        snapshot.setOtherPlayers(players.entrySet().stream()
                .filter(map -> !map.getKey().equals(playerId))
                .map(Map.Entry::getValue)
                .collect(toSet()));
        snapshot.setTerrainChunks(state.getTerrainChunks());
        return snapshot;
    }
}

package com.metalheart.service.imp;

import com.metalheart.model.*;
import com.metalheart.service.SnapshotService;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Component
public class SnapshotServiceImpl implements SnapshotService {

    private PlayerSnapshot emptySnapshot;

    @Override
    public PlayerSnapshot getDummySnapshot() {
        if(emptySnapshot == null) {
            emptySnapshot = new PlayerSnapshot();
            emptySnapshot.setTerrainChunks(new ArrayList<>());
        }
        return emptySnapshot;
    }

    @Override
    public PlayerSnapshot getSnapshot(InetSocketAddress playerId, State state) {

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

    @Override
    public PlayerSnapshot getDelta(PlayerSnapshot s1, PlayerSnapshot s2) {

        PlayerSnapshot result = new PlayerSnapshot();
        result.setSequenceNumber(s1.getSequenceNumber());
        result.setAcknowledgmentNumber(s1.getAcknowledgmentNumber());

        // player
        result.setPlayer(s1.getPlayer());

        // todo: other players
        result.setOtherPlayers(new ArrayList<>());

        // terrain
        result.setTerrainChunks(new ArrayList<>());

        Map<Vector3, List<TerrainChunk>> snap1Terrain = s1.getTerrainChunks()
                .stream()
                .collect(groupingBy(TerrainChunk::getPosition));

        Map<Vector3, List<TerrainChunk>> snap2Terrain = s2.getTerrainChunks()
                .stream()
                .collect(groupingBy(TerrainChunk::getPosition));

        snap1Terrain.forEach((position, chunks) -> {

            List<TerrainChunk> snap2TerrainChunks = snap2Terrain.get(position);
            if (!(snap2TerrainChunks != null && chunks.get(0).equals(snap2TerrainChunks.get(0)))) {
                result.getTerrainChunks().add(chunks.get(0));
            }
        });

        return result;
    }
}

package com.metalheart.service.imp;

import com.metalheart.model.logic.State;
import com.metalheart.model.transport.PlayerDTO;
import com.metalheart.model.transport.PlayerSnapshot;
import com.metalheart.model.transport.TerrainChunkDTO;
import com.metalheart.model.transport.Vector3;
import com.metalheart.service.SnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Component
public class SnapshotServiceImpl implements SnapshotService {

    private PlayerSnapshot emptySnapshot;

    @Autowired
    private ConversionService conversionService;

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

        Map<InetSocketAddress, PlayerDTO> players = new HashMap<>();
        state.getPlayers().forEach((ip, player) -> players.put(ip, conversionService.convert(player, PlayerDTO.class)));

        Set<TerrainChunkDTO> terrainChunks = new HashSet<>();
        state.getTerrainChunks()
                .stream()
                .map(chunk -> conversionService.convert(chunk, TerrainChunkDTO.class))
                .forEach(terrainChunks::add);

        PlayerSnapshot snapshot = new PlayerSnapshot();
        snapshot.setTimestamp(Instant.now().toEpochMilli());

        snapshot.setPlayer(players.get(playerId));
        snapshot.setOtherPlayers(players.entrySet().stream()
                .filter(map -> !map.getKey().equals(playerId))
                .map(Map.Entry::getValue)
                .collect(toSet()));
        snapshot.setTerrainChunks(terrainChunks);
        return snapshot;
    }

    @Override
    public PlayerSnapshot getDelta(PlayerSnapshot s1, PlayerSnapshot s2) {

        PlayerSnapshot result = new PlayerSnapshot();
        result.setSequenceNumber(s1.getSequenceNumber());
        result.setAcknowledgmentNumber(s1.getAcknowledgmentNumber());
        result.setTimestamp(s1.getTimestamp());

        // player
        result.setPlayer(s1.getPlayer());

        // todo: other players
        result.setOtherPlayers(s1.getOtherPlayers());

        // terrain
        result.setTerrainChunks(new ArrayList<>());

        Map<Vector3, List<TerrainChunkDTO>> snap1Terrain = s1.getTerrainChunks()
                .stream()
                .collect(groupingBy(TerrainChunkDTO::getPosition));

        Map<Vector3, List<TerrainChunkDTO>> snap2Terrain = s2.getTerrainChunks()
                .stream()
                .collect(groupingBy(TerrainChunkDTO::getPosition));

        snap1Terrain.forEach((position, chunks) -> {

            List<TerrainChunkDTO> snap2TerrainChunks = snap2Terrain.get(position);
            if (!(snap2TerrainChunks != null && chunks.get(0).equals(snap2TerrainChunks.get(0)))) {
                result.getTerrainChunks().add(chunks.get(0));
            }
        });

        return result;
    }
}

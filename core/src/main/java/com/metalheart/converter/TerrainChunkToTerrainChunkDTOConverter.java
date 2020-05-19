package com.metalheart.converter;

import com.metalheart.model.logic.TerrainChunk;
import com.metalheart.model.physic.Vector3d;
import com.metalheart.model.transport.TerrainChunkDTO;
import com.metalheart.model.transport.Vector3;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TerrainChunkToTerrainChunkDTOConverter implements Converter<TerrainChunk, TerrainChunkDTO> {

    public TerrainChunkDTO convert(TerrainChunk src) {
        TerrainChunkDTO dst = new TerrainChunkDTO();
        dst.setPosition(convert(src.getPosition()));

        dst.setChildren(src.getWallsCoords().stream()
                .map(this::convert)
                .collect(Collectors.toSet()));

        return dst;
    }

    private Vector3 convert(Vector3d vector) {
        return new Vector3(vector.d0, vector.d1, vector.d2);
    }
}

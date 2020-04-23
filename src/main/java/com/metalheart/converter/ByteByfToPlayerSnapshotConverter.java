package com.metalheart.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalheart.model.PlayerSnapshot;
import io.netty.buffer.ByteBuf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class ByteByfToPlayerSnapshotConverter implements Converter<ByteBuf, PlayerSnapshot> {

    @Autowired
    private ObjectMapper mapper;

    public PlayerSnapshot convert(ByteBuf src) {
        try {
            return mapper.readValue(src.toString(UTF_8), PlayerSnapshot.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

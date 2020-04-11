package com.metalheart.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalheart.model.PlayerInput;
import io.netty.buffer.ByteBuf;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PlayerRequestConverter {

    ObjectMapper mapper = new ObjectMapper();

    public PlayerInput convert(ByteBuf src) {
        try {
            return mapper.readValue(src.toString(UTF_8), PlayerInput.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

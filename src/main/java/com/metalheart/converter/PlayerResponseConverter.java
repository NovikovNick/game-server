package com.metalheart.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalheart.model.PlayerSnapshot;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PlayerResponseConverter {

    ObjectMapper mapper = new ObjectMapper();

    public ByteBuf convert(PlayerSnapshot src) {
        try {
            return Unpooled.wrappedBuffer(mapper.writeValueAsBytes(src));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

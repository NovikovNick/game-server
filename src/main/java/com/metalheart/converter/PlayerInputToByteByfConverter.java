package com.metalheart.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalheart.model.PlayerInput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlayerInputToByteByfConverter implements Converter<PlayerInput, ByteBuf> {

    @Autowired
    private ObjectMapper mapper;

    public ByteBuf convert(PlayerInput src) {
        try {
            return Unpooled.wrappedBuffer(mapper.writeValueAsBytes(src));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.metalheart.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalheart.model.transport.PlayerSnapshot;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlayerSnapshotToByteBufConverter implements Converter<PlayerSnapshot, ByteBuf> {

    @Autowired
    private ObjectMapper mapper;

    public ByteBuf convert(PlayerSnapshot src) {
        try {
            return Unpooled.wrappedBuffer(mapper.writeValueAsBytes(src));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

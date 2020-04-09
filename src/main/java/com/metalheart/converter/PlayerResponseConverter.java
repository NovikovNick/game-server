package com.metalheart.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalheart.model.PlayerSnapshot;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PlayerResponseConverter {

    ObjectMapper mapper = new ObjectMapper();

    public ByteBuf convert(ChannelHandlerContext ctx, PlayerSnapshot src) {
        ByteBuf dst = ctx.alloc().buffer();
        try {
            dst.writeBytes(mapper.writeValueAsBytes(src));
            return dst;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return dst;
    }
}

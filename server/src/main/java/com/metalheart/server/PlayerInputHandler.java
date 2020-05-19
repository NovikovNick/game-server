package com.metalheart.server;

import com.metalheart.model.transport.PlayerInput;
import com.metalheart.service.TransportLayer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class PlayerInputHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Autowired
    private TransportLayer transportLayer;

    @Autowired
    private ConversionService conversionService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        transportLayer.addPlayerInput(msg.sender(), conversionService.convert(msg.content(), PlayerInput.class));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

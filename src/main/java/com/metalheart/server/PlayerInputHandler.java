package com.metalheart.server;

import com.metalheart.converter.ByteByfToPlayerInputConverter;
import com.metalheart.service.TransportLayer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerInputHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Autowired
    private TransportLayer transportLayer;

    private ByteByfToPlayerInputConverter converter;

    public PlayerInputHandler(TransportLayer transportLayer, ByteByfToPlayerInputConverter converter) {
        this.transportLayer = transportLayer;
        this.converter = converter;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        transportLayer.addPlayerInput(msg.sender(), converter.convert(msg.content()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

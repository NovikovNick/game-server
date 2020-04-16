package com.metalheart.server.handler;

import com.metalheart.converter.PlayerRequestConverter;
import com.metalheart.service.TransportLayer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class PlayerInputHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private TransportLayer transportLayer;
    private PlayerRequestConverter converter;

    public PlayerInputHandler(TransportLayer transportLayer, PlayerRequestConverter converter) {
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

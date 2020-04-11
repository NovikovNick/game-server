package com.metalheart.server.handler;

import com.metalheart.converter.PlayerRequestConverter;
import com.metalheart.model.Domain;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class GameServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Domain domain;
    PlayerRequestConverter requestConverter = new PlayerRequestConverter();

    public GameServerHandler(Domain domain) {
        this.domain = domain;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {

        domain.playerInput(msg.sender(), requestConverter.convert(msg.content()));
        //ctx.writeAndFlush
        /*final PlayerSnapshot playerRes = domain.getPlayerSnapshot(playerReq.getPlayerId());
        ByteBuf responseData = responseConverter.convert(ctx, playerRes);

        ctx.writeAndFlush(new DatagramPacket(responseData, msg.sender()));*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
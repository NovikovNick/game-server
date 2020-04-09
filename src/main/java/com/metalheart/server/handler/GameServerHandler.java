package com.metalheart.server.handler;

import com.metalheart.converter.PlayerRequestConverter;
import com.metalheart.converter.PlayerResponseConverter;
import com.metalheart.model.Domain;
import com.metalheart.model.PlayerRequest;
import com.metalheart.model.PlayerSnapshot;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class GameServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Domain domain;
    PlayerRequestConverter requestConverter = new PlayerRequestConverter();
    PlayerResponseConverter responseConverter = new PlayerResponseConverter();

    public GameServerHandler(Domain domain) {
        this.domain = domain;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {

        PlayerRequest playerReq = requestConverter.convert(msg.content());
        domain.playerInput(playerReq);

        final PlayerSnapshot playerRes = domain.getPlayerSnapshot(playerReq.getPlayerId());
        ByteBuf responseData = responseConverter.convert(ctx, playerRes);

        ctx.writeAndFlush(new DatagramPacket(responseData, msg.sender()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
package com.metalheart.server;

import com.metalheart.converter.PlayerRequestConverter;
import com.metalheart.server.handler.PlayerInputHandler;
import com.metalheart.service.GameStateService;
import com.metalheart.service.TerrainService;
import com.metalheart.service.TransportLayer;
import com.metalheart.service.imp.GameStateServiceImpl;
import com.metalheart.service.imp.TerrainServiceImp;
import com.metalheart.service.imp.UdpTransportLayer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class GameServer {

    private final int port;
    private final int tickRate = 10;
    private final String host = "192.168.0.102";

    public GameServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {

        PlayerRequestConverter converter = new PlayerRequestConverter();

        TerrainService terrainService = new TerrainServiceImp();
        TransportLayer transportLayer = new UdpTransportLayer();
        PlayerInputHandler playerInputHandler = new PlayerInputHandler(transportLayer, converter);
        GameStateService gameStateService = new GameStateServiceImpl(transportLayer, terrainService, tickRate);

        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
        threadPool.scheduleWithFixedDelay(gameStateService::calculateState, 0, 1000 / tickRate, MILLISECONDS);

        startReceivingPlayersInput(transportLayer, playerInputHandler);
    }

    private void startReceivingPlayersInput(TransportLayer transportLayer,
                                            PlayerInputHandler playerInputHandler) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bs = new Bootstrap();
            bs.group(workerGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) throws Exception {

                            ch.pipeline().addLast(playerInputHandler);
                        }
                    });

            Channel channel = bs.bind(host, port).sync().channel();
            transportLayer.setChannel(channel);


            channel.closeFuture().await();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 7777;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new GameServer(port).run();
    }
}

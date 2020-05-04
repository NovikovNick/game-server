package com.metalheart.server;

import com.metalheart.configuration.GameProperties;
import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import com.metalheart.service.GameStateService;
import com.metalheart.service.TerrainService;
import com.metalheart.service.TransportLayer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class GameServer {

    @Autowired
    private GameProperties props;

    @Autowired
    private TerrainService terrainService;

    @Autowired
    private TransportLayer transportLayer;

    @Autowired
    private PlayerInputHandler playerInputHandler;

    @Autowired
    private GameStateService gameStateService;

    public void run() throws Exception {

        int initialDelay = 0;
        int delay = 1000 / props.getTickRate();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(() -> {

            State state = gameStateService.calculateState();
            Map<InetSocketAddress, PlayerSnapshot> snapshots = transportLayer.calculateSnapshots(state);
            transportLayer.notifyPlayers(snapshots);

        }, initialDelay, delay, MILLISECONDS);

        startReceivingPlayersInput(props.getHost(), props.getPort());
    }

    private void startReceivingPlayersInput(String host, Integer port) throws InterruptedException {
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
}

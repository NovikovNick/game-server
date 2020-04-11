package com.metalheart.server;

import com.metalheart.model.Domain;
import com.metalheart.server.handler.GameServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Discards any incoming data.
 */
public class GameServer {

    private final int port;
    private final int tickRate = 10;
    private final String host = "192.168.0.102";

    public GameServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {

        Domain domain = new Domain(tickRate);

        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(2);
        int tickRateMs = 1000 / tickRate;
        threadPool.scheduleWithFixedDelay(domain::tick, 0, tickRateMs, TimeUnit.MILLISECONDS);
        threadPool.scheduleWithFixedDelay(domain::notifyPlayers, 0, tickRateMs, TimeUnit.MILLISECONDS);

        startReceivingPlayersInput(domain);
    }

    private void startReceivingPlayersInput(Domain domain) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bs = new Bootstrap();
            bs.group(workerGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new GameServerHandler(domain));
                        }
                    });

            Channel channel = bs.bind(host, port).sync().channel();
            domain.setChannel(channel);


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

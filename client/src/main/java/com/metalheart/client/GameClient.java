package com.metalheart.client;

import com.metalheart.model.transport.PlayerInput;
import com.metalheart.model.transport.PlayerSnapshot;
import com.metalheart.model.transport.Vector3;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class GameClient {

    @Autowired
    private ConversionService conversionService;

    private Channel channel;
    private AtomicInteger sequenceNumber = new AtomicInteger(0);
    private AtomicInteger acknowledgeNumber = new AtomicInteger(0);


    public void startReceiving(String host, Integer port) {

        InetSocketAddress serverAddress = new InetSocketAddress(host, port);

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            int sn = sequenceNumber.incrementAndGet();
            PlayerInput request = getPlayerRequest(
                    (float) Math.cos(sn / 5f) * 15,
                    0.0f,
                    (float) Math.sin(sn / 5f) * 15);

            log.info("request {}", request);
            send(serverAddress, conversionService.convert(request, ByteBuf.class));

        }, 0, 50, TimeUnit.MILLISECONDS);

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            Bootstrap bs = new Bootstrap();
            bs.group(workerGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_RCVBUF, 120000)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(120000))
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                @Override
                public void initChannel(NioDatagramChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                                            DatagramPacket msg) throws Exception {
                                    PlayerSnapshot snapshot = conversionService.convert(msg.content(), PlayerSnapshot.class);
                                    acknowledgeNumber.set(snapshot.getSequenceNumber());
                                }
                            });
                }
            });
            channel = bs.bind(host, 7778).sync().channel();
            channel.closeFuture().await();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private void send(InetSocketAddress address, ByteBuf buf) {

        if (channel == null) {
            return;
        }

        try {

            DatagramPacket packet = new DatagramPacket(buf, address);
            channel.writeAndFlush(packet).addListener(future -> {
                if (!future.isSuccess()) {
                    future.cause().printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PlayerInput getPlayerRequest(float x, float y, float z) {
        PlayerInput request = new PlayerInput();
        request.setSequenceNumber(sequenceNumber.get());
        request.setAcknowledgmentNumber(acknowledgeNumber.get());
        request.setTimeDelta(0.016f);

        request.setMagnitude(1f);
        request.setDirection(new Vector3(x, y, z));
        request.setIsRunning(false);
        return request;
    }
}

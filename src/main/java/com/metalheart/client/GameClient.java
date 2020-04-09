package com.metalheart.client;

import com.metalheart.converter.PlayerRequestConverter;
import com.metalheart.model.PlayerRequest;
import com.metalheart.model.Vector3;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GameClient {

    public static void main(String args[]) {

        PlayerRequestConverter converter = new PlayerRequestConverter();

        AtomicInteger x = new AtomicInteger();

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {

            PlayerRequest request = getPlayerRequest(
                    5.41f + 0.1f *  x.getAndSet(x.incrementAndGet() % 10),
                    0.55f,
                    7f);
            //send(converter.convert(request), 7777);

        }, 0, 50, TimeUnit.MILLISECONDS);

        sleep(10);
    }

    private static void sleep(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static void send(byte[] buf, int port) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static PlayerRequest getPlayerRequest(float x, float y, float z) {
        PlayerRequest request = new PlayerRequest();
        request.setPlayerId((byte) 123);
       // request.setPosition(new Vector3(x, y, z));
        request.setDirection(new Vector3(0.4f, 0.5f, 0.6f));
        return request;
    }

}

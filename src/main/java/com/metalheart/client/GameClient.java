package com.metalheart.client;

import com.metalheart.converter.PlayerRequestConverter;
import com.metalheart.model.PlayerInput;
import com.metalheart.model.Vector3;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GameClient {

    static DatagramSocket socket;
    static AtomicInteger datagramNumber = new AtomicInteger(0);

    public static void main(String args[]) {
        try {
            socket = new DatagramSocket(7778);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        PlayerRequestConverter converter = new PlayerRequestConverter();

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {

            PlayerInput request = getPlayerRequest(
                    (float) Math.cos(datagramNumber.incrementAndGet() / 10) * 15,
                    0.0f ,
                    (float) Math.sin(datagramNumber.incrementAndGet() / 10) * 15);

            System.out.println(request);
            send(converter.convert(request), 7777);

        }, 0, 50, TimeUnit.MILLISECONDS);

        //sleep(10);
    }


    private static void sleep(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void send(byte[] buf, int port) {

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

    private static PlayerInput getPlayerRequest(float x, float y, float z) {
        PlayerInput request = new PlayerInput();
        request.setTimestamp(System.currentTimeMillis());
        request.setDatagramNumber(datagramNumber.get());
        request.setTimeDelta(0.016f);

        request.setMagnitude(1f);
        request.setDirection(new Vector3(x, y, z));
        request.setIsRunning(false);
        return request;
    }
}

package com.metalheart.service;

import com.metalheart.model.PlayerInput;
import com.metalheart.model.State;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Queue;

public interface TransportLayer {

    void setChannel(Channel channel);

    Map<InetSocketAddress, Queue<PlayerInput>> getPlayerInputs();

    void addPlayerInput(InetSocketAddress playerId, PlayerInput input);

    void sendSnapshot(State state);
}

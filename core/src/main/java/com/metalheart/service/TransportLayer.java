package com.metalheart.service;

import com.metalheart.model.transport.PlayerInput;
import com.metalheart.model.transport.PlayerSnapshot;
import com.metalheart.model.logic.State;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Queue;

public interface TransportLayer {

    void setChannel(Channel channel);

    Map<InetSocketAddress, Queue<PlayerInput>> getPlayerInputs();

    void addPlayerInput(InetSocketAddress playerId, PlayerInput input);

    Map<InetSocketAddress, PlayerSnapshot> calculateSnapshots(State state);

    void notifyPlayers(Map<InetSocketAddress, PlayerSnapshot> snapshots);
}

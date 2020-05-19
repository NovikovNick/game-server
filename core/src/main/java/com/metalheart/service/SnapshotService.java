package com.metalheart.service;

import com.metalheart.model.transport.PlayerSnapshot;
import com.metalheart.model.logic.State;

import java.net.InetSocketAddress;

public interface SnapshotService {

    PlayerSnapshot getDummySnapshot();

    PlayerSnapshot getSnapshot(InetSocketAddress playerId, State state);

    PlayerSnapshot getDelta(PlayerSnapshot s1, PlayerSnapshot s2);
}

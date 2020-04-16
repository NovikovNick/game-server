package com.metalheart.service;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;

import java.net.InetSocketAddress;

public interface SnapshotService {
    PlayerSnapshot getDummySnapshot();

    PlayerSnapshot getSnapshot(InetSocketAddress playerId, State state);
}

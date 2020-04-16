package com.metalheart;

import com.metalheart.model.GameObject;
import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.State;
import com.metalheart.model.Vector3;
import com.metalheart.service.SnapshotService;
import com.metalheart.service.imp.SnapshotServiceImpl;
import org.junit.Test;

import java.net.InetSocketAddress;

public class SnapshotServiceTest {

    @Test
    public void test() {
        SnapshotService snapshotService = new SnapshotServiceImpl();


        InetSocketAddress playerId1 = InetSocketAddress.createUnresolved("127.0.0.1", 9990);
        InetSocketAddress playerId2 = InetSocketAddress.createUnresolved("127.0.0.1", 9991);

        State state = new State();
        state.getPlayers().put(playerId1, getPlayer(0, 0, 0));
        state.getPlayers().put(playerId2, getPlayer(0, 0, 0));



        PlayerSnapshot[] snapshots = new PlayerSnapshot[32];

        for (int sequenceNumber = 0; sequenceNumber < 100; sequenceNumber++) {

            state.getPlayers().put(playerId1, getPlayer(sequenceNumber, 0, 0));

            PlayerSnapshot snapshot = snapshotService.getSnapshot(playerId1, state);
            snapshot.setSequenceNumber(sequenceNumber);
            snapshots[sequenceNumber % 32] = snapshot;

        }

        System.out.println("end");
    }

    private GameObject getPlayer(int x, int y, int z) {
        GameObject player = new GameObject();
        player.setPosition(new Vector3(x, y, z));
        player.setRotation(new Vector3(0,0,0));
        return player;
    }

}

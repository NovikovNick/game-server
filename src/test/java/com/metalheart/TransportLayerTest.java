package com.metalheart;

import com.metalheart.configuration.GameConfiguration;
import com.metalheart.model.*;
import com.metalheart.service.GameStateService;
import com.metalheart.service.TransportLayer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetSocketAddress;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = GameConfiguration.class)
public class TransportLayerTest {

    @Autowired
    private GameStateService gameStateService;

    @Autowired
    private TransportLayer transportLayer;

    @Test
    public void firstInputTest() {

        // arrange
        InetSocketAddress playerId = getPlayerId("127.0.0.1", 0);
        for (int i = 0; i < 100; i++) transportLayer.calculateSnapshots(gameStateService.calculateState());

        // act
        transportLayer.addPlayerInput(playerId, PlayerInput.builder()
                .sequenceNumber(0)
                .acknowledgmentNumber(null)
                .magnitude(.0f)
                .timeDelta(.015f)
                .isRunning(false)
                .direction(new Vector3(0, 0, 0))
                .build());
        State state = gameStateService.calculateState();
        Map<InetSocketAddress, PlayerSnapshot> snapshots = transportLayer.calculateSnapshots(state);

        // assert
        PlayerSnapshot snapshot = snapshots.get(playerId);

        Assert.assertNotNull(snapshot.getPlayer());
        Assert.assertEquals(100,  snapshot.getSequenceNumber());
        Assert.assertEquals(new Vector3(2, 1, 2),  snapshot.getPlayer().getPosition());
        Assert.assertEquals(new Vector3(0, 0, 0),  snapshot.getPlayer().getRotation());
        Assert.assertNotNull(snapshot.getOtherPlayers());
        Assert.assertTrue(snapshot.getOtherPlayers().isEmpty());
        Assert.assertNotNull(snapshot.getTerrainChunks());
        Assert.assertEquals(9,  snapshot.getTerrainChunks().size());
    }

    @Test
    public void secondInputTest() {

        // arrange
        InetSocketAddress playerId = getPlayerId("127.0.0.1", 0);
        for (int i = 0; i < 100; i++) transportLayer.calculateSnapshots(gameStateService.calculateState());

        transportLayer.addPlayerInput(playerId, PlayerInput.builder()
                .sequenceNumber(0)
                .acknowledgmentNumber(null)
                .magnitude(.0f)
                .timeDelta(.015f)
                .isRunning(false)
                .direction(new Vector3(0, 0, 0))
                .build());

        transportLayer.calculateSnapshots(gameStateService.calculateState());

        transportLayer.addPlayerInput(playerId, PlayerInput.builder()
                .sequenceNumber(1)
                .acknowledgmentNumber(0)
                .magnitude(.0f)
                .timeDelta(.015f)
                .isRunning(false)
                .direction(new Vector3(0, 0, 0))
                .build());

        // act
        State state = gameStateService.calculateState();
        Map<InetSocketAddress, PlayerSnapshot> snapshots = transportLayer.calculateSnapshots(state);


        // assert
        PlayerSnapshot snapshot = snapshots.get(playerId);

        Assert.assertNotNull(snapshot.getPlayer());
        Assert.assertEquals(new Vector3(2, 1, 2),  snapshot.getPlayer().getPosition());
        Assert.assertEquals(new Vector3(0, 0, 0),  snapshot.getPlayer().getRotation());
        Assert.assertNotNull(snapshot.getOtherPlayers());
        Assert.assertTrue(snapshot.getOtherPlayers().isEmpty());
        Assert.assertNotNull(snapshot.getTerrainChunks());
        Assert.assertTrue(snapshot.getTerrainChunks().isEmpty());
    }

    private GameObject getPlayer(int x, int y, int z) {
        GameObject player = new GameObject();
        player.setPosition(new Vector3(x, y, z));
        player.setRotation(new Vector3(0, 0, 0));
        return player;
    }

    private InetSocketAddress getPlayerId(String host, int port) {
        return InetSocketAddress.createUnresolved(host, port);
    }

}

package com.metalheart;

import com.metalheart.configuration.GameConfiguration;
import com.metalheart.model.transport.PlayerSnapshot;
import com.metalheart.service.SnapshotService;
import com.metalheart.service.TerrainService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.Arrays.asList;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = GameConfiguration.class)
@DirtiesContext
public class GetSnapshotDeltaTest {

    @Autowired
    private SnapshotService snapshotService;

    @Autowired
    private TerrainService terrainService;

    @Test
    public void firstInputTest() {

        // arrange
        PlayerSnapshot s1 = new PlayerSnapshot();
        s1.setTerrainChunks(asList(terrainService.getFourPassingRoom(0,0,0)));

        PlayerSnapshot s2 = new PlayerSnapshot();
        s2.setTerrainChunks(asList(terrainService.getClosedRoom(0,0,0)));

        // act
        PlayerSnapshot delta = snapshotService.getDelta(s1, s2);

        // assert
        Assert.assertNotNull(delta.getTerrainChunks());
        Assert.assertEquals(1, delta.getTerrainChunks().size());
    }
}

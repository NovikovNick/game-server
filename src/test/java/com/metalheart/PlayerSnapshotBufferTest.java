package com.metalheart;

import com.metalheart.model.PlayerSnapshot;
import com.metalheart.model.PlayerSnapshotBuffer;
import org.junit.Assert;
import org.junit.Test;

public class PlayerSnapshotBufferTest {

    @Test
    public void positiveSequenceNumberTest() {

        // arrange
        PlayerSnapshotBuffer buffer = new PlayerSnapshotBuffer(5);

        for (int capacity = 5; capacity < 1000; capacity++) {
            // act
            for (int i = 0; i < capacity; i++) {
                PlayerSnapshot snapshot = new PlayerSnapshot();
                snapshot.setSequenceNumber(i);
                buffer.add(i, snapshot);
            }
            // assert
            int expectedInt = capacity;
            for (PlayerSnapshotBuffer.Entry entry : buffer) {
                Assert.assertEquals(--expectedInt, entry.getData().getSequenceNumber());
            }
        }
    }

    @Test
    public void negativeSequenceNumberTest() {

        // arrange
        PlayerSnapshotBuffer buffer = new PlayerSnapshotBuffer(32);

        // act
        int sequenceNumber = Integer.MAX_VALUE - 60;
        for (int i = 0; i < 64; i++) {
            PlayerSnapshot snapshot = new PlayerSnapshot();
            snapshot.setSequenceNumber(sequenceNumber++);
            buffer.add(i, snapshot);
        }

        // assert
        int expectedInt = Integer.MIN_VALUE + 3 ;
        for (PlayerSnapshotBuffer.Entry entry : buffer) {
            Assert.assertEquals(--expectedInt, entry.getData().getSequenceNumber());
        }
    }
}

package com.metalheart.model;

import lombok.Data;

import java.util.Arrays;
import java.util.Iterator;

@Data
public class PlayerSnapshotBuffer implements Iterable<PlayerSnapshotBuffer.Entry> {

    private int capacity;
    private int headIndex;
    private Entry[] data;

    public PlayerSnapshotBuffer(int capacity) {
        this.capacity = capacity;
        this.data = new Entry[capacity];
        for (int i = 0; i < capacity; i++) {
            data[i] = new Entry();
        }
    }

    public void add(int index, PlayerSnapshot snapshot) {
        this.headIndex = Math.abs(index % capacity);
        data[headIndex].setData(snapshot);
    }

    @Override
    public Iterator<PlayerSnapshotBuffer.Entry> iterator() {
        Entry[] iterable = new Entry[capacity];
        for (int i = 0, index = headIndex; i < capacity; i++, index = index - 1 < 0 ? capacity-1: index -1) {
            iterable[i] = data[index % capacity];
        }
        return Arrays.stream(iterable).iterator();
    }

    public void markAck(int index) {
        index = Math.abs(index % capacity);
        data[index].setAck(true);
    }

    public boolean isAck(int index) {
        return data[Math.abs(index % capacity)].isAck();
    }

    @Data
    public static class Entry {
        private boolean ack;
        private PlayerSnapshot data;
    }
}

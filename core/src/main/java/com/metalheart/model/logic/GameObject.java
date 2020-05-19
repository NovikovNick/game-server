package com.metalheart.model.logic;

import com.metalheart.model.physic.RigidBody;
import com.metalheart.model.physic.Transform;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@EqualsAndHashCode(of = "id")
public class GameObject {

    private static AtomicInteger idSequence = new AtomicInteger(0);

    public GameObject() {
        this.id = idSequence.incrementAndGet();
    }

    private Integer id;
    private Transform transform;
    private RigidBody rigidBody;
}

package com.metalheart.model.transport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInput {

    public int sequenceNumber;
    public Integer acknowledgmentNumber;
    public float timeDelta;

    public Vector3 direction;
    public Float magnitude;
    public Boolean isRunning;
}

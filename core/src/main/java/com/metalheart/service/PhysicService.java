package com.metalheart.service;

import com.metalheart.model.logic.State;

public interface PhysicService {
    State step(State state);
}

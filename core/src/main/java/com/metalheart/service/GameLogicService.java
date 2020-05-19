package com.metalheart.service;

import com.metalheart.model.logic.State;

public interface GameLogicService {
    State calculateState();

    State getState();
}

package com.metalheart.service;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public interface Scene3DService {

    PerspectiveCamera getCamera();

    Group getGroup();

    Scene createScene(Pane controls);

    Group createCoordAxes();
}

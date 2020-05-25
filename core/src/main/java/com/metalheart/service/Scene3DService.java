package com.metalheart.service;

import com.metalheart.model.physic.Vector3d;
import javafx.scene.Scene;

import java.util.List;

public interface Scene3DService {
    Scene createScene(List<Vector3d> data);
}

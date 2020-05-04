package com.metalheart.showcase.service;

import com.metalheart.model.Vector3;
import com.metalheart.model.physic.Point2d;
import com.metalheart.showcase.model.Force;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import lombok.Getter;

import java.awt.*;

public class PlayerInputService {

    private static final int SPEED = 15;

    private boolean wPressed;
    private boolean aPressed;
    private boolean sPressed;
    private boolean dPressed;

    @Getter
    private EventHandler<KeyEvent> keyPressHandler = e -> {
        switch (e.getCode()) {
            case W:
                wPressed = true;
                break;
            case A:
                aPressed = true;
                break;
            case S:
                sPressed = true;
                break;
            case D:
                dPressed = true;
                break;
        }
    };

    @Getter
    private EventHandler<KeyEvent> keyReleaseHandler = e -> {
        switch (e.getCode()) {
            case W:
                wPressed = false;
                break;
            case A:
                aPressed = false;
                break;
            case S:
                sPressed = false;
                break;
            case D:
                dPressed = false;
                break;
        }
    };

    public Point2d getMousePosition() {
        return new Point2d(
                MouseInfo.getPointerInfo().getLocation().x,
                MouseInfo.getPointerInfo().getLocation().y);
    }

    public Force getInputForce() {
        Vector3 direction = new Vector3(0, 0, 0);

        if (!(wPressed || sPressed || aPressed || dPressed)) {
            return new Force(direction, 0);
        }

        if (wPressed) direction.setY(direction.getY() + 1);
        if (sPressed) direction.setY(direction.getY() - 1);

        if (aPressed) direction.setX(direction.getX() - 1);
        if (dPressed) direction.setX(direction.getX() + 1);

        return new Force(direction, SPEED);
    }

}

package com.metalheart.service;

import com.metalheart.math.PhysicUtil;
import com.metalheart.model.physic.Force;
import com.metalheart.model.physic.Point2d;
import com.metalheart.model.physic.Vector3d;
import com.metalheart.repository.PlayerRepository;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class PlayerInputService {

    private static final int SPEED = 6;

    private boolean wPressed;
    private boolean aPressed;
    private boolean sPressed;
    private boolean dPressed;

    @Autowired
    private PlayerRepository playerRepository;

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
        Vector3d direction = new Vector3d(0, 0, 0);

        if (!(wPressed || sPressed || aPressed || dPressed)) {
            return new Force(direction, 0);
        }

        Point2d m = getMousePosition();
        Point2d center = CanvasService.toLocalCoord(PhysicUtil.getCenter(playerRepository.get().getData()));

        Vector3d normalized = new Vector3d(m.getD0() - center.getD0(), -(m.getD1() - center.getD1()), 0).normalize();

        if (wPressed) direction = normalized;

        if (sPressed) direction = direction.plus(normalized.scale(-1));

        if (aPressed) {
            Point2d left = PhysicUtil.rotate(new Point2d(normalized.d0, normalized.d1), (float) Math.toRadians(90));
            direction = direction.plus(new Vector3d(left.getD0(), left.getD1(), 0));
        }

        if (dPressed){
            Point2d right = PhysicUtil.rotate(new Point2d(normalized.d0, normalized.d1), (float) Math.toRadians(-90));
            direction = direction.plus(new Vector3d(right.getD0(), right.getD1(), 0));
        }

        return new Force(direction, SPEED);
    }

}

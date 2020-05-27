package com.metalheart.service.visial;

import com.metalheart.model.physic.Point2d;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static javafx.scene.input.KeyCode.*;

@Component
public class InputService {

    private static final int SPEED = 6;

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

    public Map<KeyCode, Boolean> getInput() {
        Map<KeyCode, Boolean> res = new HashMap<>();
        res.put(W, wPressed);
        res.put(A, aPressed);
        res.put(S, sPressed);
        res.put(D, dPressed);
        return res;
    }

}

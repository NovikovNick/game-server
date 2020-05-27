package com.metalheart.service.showcase;

import com.metalheart.service.Scene3DService;
import com.metalheart.service.visial.InputService;
import javafx.animation.AnimationTimer;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.Translate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class CameraShowcase extends AnimationTimer {

    @Autowired
    private InputService inputService;

    @Autowired
    private Scene3DService scene3DService;


    private AtomicLong sequenceNumber = new AtomicLong(0);
    private Long previousAnimationAt;

    @Override
    public void handle(long now) {

        float dt = getDeltaTime(now);
        if (sequenceNumber.incrementAndGet() % 10 == 0) {
            // algorithm.step();
        }


        // 2.
        PerspectiveCamera camera = scene3DService.getCamera();


        Translate translate = new Translate(0, 0, 0);

        //Rotate rotate = new Rotate();
        //rotate.setAxis(Point3D.ZERO);

        if (inputService.getInput().get(KeyCode.A)) {
            translate.setX(translate.getX() -10);
        }
        if (inputService.getInput().get(KeyCode.D)) {
            translate.setX(translate.getX() + 10);
        }

        if (inputService.getInput().get(KeyCode.W)) {
            translate.setZ(translate.getZ() + 10);
        }
        if (inputService.getInput().get(KeyCode.S)) {
            translate.setZ(translate.getZ() - 10);
        }

        camera.getTransforms().addAll(translate);
    }

    private float getDeltaTime(long now) {
        float timeDelta;
        if (previousAnimationAt == null) {
            timeDelta = 0.0015f;
        } else {
            timeDelta = (now - previousAnimationAt) / 1000000000f;
        }
        previousAnimationAt = now;
        return timeDelta;
    }
}

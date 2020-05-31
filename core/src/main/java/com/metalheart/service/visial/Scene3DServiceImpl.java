package com.metalheart.service.visial;

import com.metalheart.model.physic.Vector3d;
import com.metalheart.service.Scene3DService;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Scene3DServiceImpl implements Scene3DService {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    @Autowired
    private InputService inputService;

    private Group group;
    private PerspectiveCamera camera;
    private VBox layout;

    public Scene3DServiceImpl() {
        group = new Group();
    }

    @Override
    public PerspectiveCamera getCamera() {
        return camera;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public Group createCoordAxes() {
        final int length = 5000;
        final int width = 3;
        Sphere center = new Sphere(width * 2);
        center.setMaterial(new PhongMaterial(Color.WHITE));

        Box x = new Box(length, width, width);
        x.setMaterial(new PhongMaterial(Color.RED));
        Box y = new Box(width, length, width);
        y.setMaterial(new PhongMaterial(Color.GREEN));
        Box z = new Box(width, width, length);
        z.setMaterial(new PhongMaterial(Color.BLUE));
        return new Group(center, x, y, z);
    }

    @Override
    public Scene createScene(Pane controls) {

        SubScene subScene = createScene3D(group);
        layout = new VBox(
                controls,
                subScene
        );

        Scene scene = new Scene(layout, Color.CORNSILK);
        scene.setOnKeyPressed(inputService.getKeyPressHandler());
        scene.setOnKeyReleased(inputService.getKeyReleaseHandler());

        CameraRotation cameraRotation = new CameraRotation();

        scene.setOnMousePressed(e -> {
            if (e.isSecondaryButtonDown()) {
                cameraRotation.pressed = true;
            }
        });
        scene.setOnMouseReleased(e -> {
            cameraRotation.pressed = false;
            cameraRotation.x = 0;
            cameraRotation.y = 0;
        });
        scene.setOnMouseDragged(e -> {

            float mousePosX = (float) e.getScreenX();
            float mousePosY = (float) e.getScreenY();

            if (cameraRotation.pressed) {
                Vector3d v = new Vector3d(
                        mousePosX - cameraRotation.x,
                        cameraRotation.y - mousePosY,
                        0f)
                        .normalize();

                addRotate(camera, new Rotate(0, Rotate.X_AXIS), v.d1);
                addRotate(camera, new Rotate(0, Rotate.Y_AXIS), v.d0);
            }

            cameraRotation.x = mousePosX;
            cameraRotation.y = mousePosY;

        });
        return scene;
    }

    private void addRotate(Node node, Rotate rotate, double angle) {

        Affine affine = new Affine();
        for (Transform transform : node.getTransforms()) {
            affine.prepend(new Affine(transform));
        }

        double A11 = affine.getMxx(), A12 = affine.getMxy(), A13 = affine.getMxz();
        double A21 = affine.getMyx(), A22 = affine.getMyy(), A23 = affine.getMyz();
        double A31 = affine.getMzx(), A32 = affine.getMzy(), A33 = affine.getMzz();

        /*Point3D pivot = new Point3D(
                affine.getTx(),
                affine.getTy(),
                affine.getTz());*/

        Point3D pivot = new Point3D(0, 0, 0);

        if (rotate.getAxis() == Rotate.X_AXIS) {
            affine.prependRotation(angle, pivot, new Point3D(A11, A21, A31));

        } else if (rotate.getAxis() == Rotate.Y_AXIS) {
            affine.prependRotation(angle, pivot, new Point3D(A12, A22, A32));

        } else if (rotate.getAxis() == Rotate.Z_AXIS) {
            affine.prependRotation(angle, pivot, new Point3D(A13, A23, A33));
        }

        node.getTransforms().setAll(affine);
    }

    private SubScene createScene3D(Group group) {
        SubScene scene3d = new SubScene(group, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        scene3d.setFill(Color.rgb(10, 10, 40));
        camera = new PerspectiveCamera();
        scene3d.setCamera(camera);
        return scene3d;
    }

    @Data
    private static class CameraRotation {
        boolean pressed = false;
        float x = 0;
        float y = 0;
    }
}

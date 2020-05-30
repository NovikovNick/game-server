package com.metalheart.service.showcase;

import com.metalheart.service.Scene3DService;
import com.metalheart.service.TerrainService;
import com.metalheart.service.visial.InputService;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CameraShowcase extends AnimationTimer {

    @Autowired
    private InputService inputService;

    @Autowired
    private Scene3DService scene3DService;

    @Autowired
    private TerrainService terrainService;

    private Shape3D mesh;


    private static final String textureLoc = "cubeSide.jpg";
    private Image texture;
    private PhongMaterial texturedMaterial = new PhongMaterial();

    private CheckBox rotateByX;
    private CheckBox rotateByY;
    private CheckBox rotateByZ;

    private RotateTransition rotate3dGroup(Group group) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(10), group);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        return rotate;
    }


    public VBox initControls() {

        scene3DService.getGroup().getChildren().addAll(scene3DService.createCoordAxes());

        texture = new Image(textureLoc);
        texturedMaterial.setDiffuseMap(texture);

        mesh = new Box();

        mesh.setScaleX(30);
        mesh.setScaleY(30);
        mesh.setScaleZ(30);

        Group group = scene3DService.getGroup();
        group.getChildren().addAll(mesh);

        return createControls(rotate3dGroup(group));
    }

    @Override
    public void handle(long now) {

        { // camera
            PerspectiveCamera camera = scene3DService.getCamera();
            Translate translate = new Translate(0, 0, 0);
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

            Affine affine = new Affine();
            for (Transform transform : camera.getTransforms()) {
                affine.prepend(new Affine(transform));
            }
            affine.prepend(translate);
            camera.getTransforms().setAll(affine);
        }

        { // rotation
            Affine affine = new Affine();
            for (Transform transform : mesh.getTransforms()) {
                affine.prepend(new Affine(transform));
            }

            double angle = 5;
            double sinTheta = Math.sin(Math.toRadians(angle));
            double cosTheta = Math.cos(Math.toRadians(angle));

            if (rotateByX.isSelected()) {
                Affine oX = new Affine(
                        1, 0, 0, 0,
                        0, cosTheta, -sinTheta, 0,
                        0, sinTheta, cosTheta, 0
                );
                affine.prepend(oX);
            }

            if (rotateByY.isSelected()) {
                Affine oY = new Affine(
                        cosTheta, 0, -sinTheta, 0,
                        0, 1, 0, 0,
                        sinTheta, 0, cosTheta, 0
                );
                affine.prepend(oY);
            }

            if (rotateByZ.isSelected()) {
                Affine oZ = new Affine(
                        cosTheta, -sinTheta, 0, 0,
                        sinTheta, cosTheta, 0, 0,
                        0, 0, 1, 0
                );
                affine.prepend(oZ);
            }
            mesh.getTransforms().setAll(affine);
        }
    }

    private VBox createControls(RotateTransition rotateTransition) {

        CheckBox wireframe = new CheckBox("Wireframe");
        mesh.drawModeProperty().bind(
                Bindings.when(
                        wireframe.selectedProperty())
                        .then(DrawMode.LINE)
                        .otherwise(DrawMode.FILL)
        );

        rotateByX = new CheckBox("Rotate By X");
        rotateByY = new CheckBox("Rotate By Y");
        rotateByZ = new CheckBox("Rotate By Z");

        VBox controls = new VBox(10, rotateByX, rotateByY, rotateByZ, wireframe);
        controls.setPadding(new Insets(10));
        return controls;
    }

}

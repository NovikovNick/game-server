package com.metalheart.service.showcase;

import com.metalheart.model.physic.Vector3d;
import com.metalheart.model.physic.Vertex;
import com.metalheart.model.physic.VoxelFace;
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
import javafx.scene.shape.*;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Terrain3dShowcase extends AnimationTimer {

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

        List<Vector3d> data = terrainService.generateMaze().stream()
                .flatMap(terrainChunk -> terrainChunk.getWallsCoords().stream())
                .collect(Collectors.toList());
        mesh = drawVoxels(data);

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
    }

    private MeshView drawVoxels(List<Vector3d> data) {

        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(new float[]{
                0, 0,// 0
                1, 0,// 1
                1, 1,// 2
                0, 1 // 3
        });

        int triangleIndex = 0;
        for (Vector3d voxelCoord : data) {
            for (VoxelFace face : VoxelFace.values()) {
                if (!data.contains(voxelCoord.plus(face.direction))) {

                    for (Vertex vertex : face.vertices) {
                        mesh.getPoints().addAll(vertex.toVector3d().plus(voxelCoord).toArray());
                    }

                    mesh.getFaces().addAll(0 + triangleIndex, 0, 1 + triangleIndex, 1, 2 + triangleIndex, 2);
                    mesh.getFaces().addAll(2 + triangleIndex, 2, 3 + triangleIndex, 3, 0 + triangleIndex, 0);

                    triangleIndex += 4;
                }
            }
        }

        return new MeshView(mesh);
    }
    private VBox createControls(RotateTransition rotateTransition) {

        CheckBox cull = new CheckBox("Cull Back");

        mesh.cullFaceProperty().bind(
                Bindings.when(
                        cull.selectedProperty())
                        .then(CullFace.BACK)
                        .otherwise(CullFace.NONE)
        );
        CheckBox wireframe = new CheckBox("Wireframe");
        mesh.drawModeProperty().bind(
                Bindings.when(
                        wireframe.selectedProperty())
                        .then(DrawMode.LINE)
                        .otherwise(DrawMode.FILL)
        );

        CheckBox rotate = new CheckBox("Rotate");
        rotate.selectedProperty().addListener(observable -> {
            if (rotate.isSelected()) {
                rotateTransition.play();
            } else {
                rotateTransition.pause();
            }
        });

        CheckBox texture = new CheckBox("Texture");
        mesh.materialProperty().bind(
                Bindings.when(
                        texture.selectedProperty())
                        .then(texturedMaterial)
                        .otherwise((PhongMaterial) null)
        );

        VBox controls = new VBox(10, rotate, texture, cull, wireframe);
        controls.setPadding(new Insets(10));
        return controls;
    }

}

package com.metalheart.service.visial;

import com.metalheart.model.physic.Vector3d;
import com.metalheart.model.physic.Vertex;
import com.metalheart.model.physic.VoxelFace;
import com.metalheart.service.Scene3DService;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Scene3DServiceImpl implements Scene3DService {

    private static final int MODEL_X_OFFSET = 0;
    private static final int MODEL_Y_OFFSET = 0;
    private static final int MODEL_Z_OFFSET = 0;

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int UNIT = 30;

    private static final String textureLoc = "cubeSide.jpg";
    private Image texture;
    private PhongMaterial texturedMaterial = new PhongMaterial();

    private MeshView meshView;

    @Override
    public Scene createScene(List<Vector3d> data) {

        texture = new Image(textureLoc);
        texturedMaterial.setDiffuseMap(texture);

        Group group = buildScene(data);

        RotateTransition rotate = rotate3dGroup(group);

        VBox layout = new VBox(
                createControls(rotate),
                createScene3D(group)
        );
        return new Scene(layout, Color.CORNSILK);
    }

    private Group buildScene(List<Vector3d> data) {

        MeshView mesh = drawVoxels(data);

        mesh.setTranslateX(WIDTH / 2 + MODEL_X_OFFSET);
        mesh.setTranslateY(HEIGHT / 2 + MODEL_Y_OFFSET);
        mesh.setTranslateZ(HEIGHT / 2 + MODEL_Z_OFFSET);
        mesh.setScaleX(UNIT);
        mesh.setScaleY(UNIT);
        mesh.setScaleZ(UNIT);

        meshView = mesh;

        return new Group(meshView);
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

    private SubScene createScene3D(Group group) {
        SubScene scene3d = new SubScene(group, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        scene3d.setFill(Color.rgb(10, 10, 40));
        scene3d.setCamera(new PerspectiveCamera());
        return scene3d;
    }

    private VBox createControls(RotateTransition rotateTransition) {
        CheckBox cull = new CheckBox("Cull Back");
        meshView.cullFaceProperty().bind(
                Bindings.when(
                        cull.selectedProperty())
                        .then(CullFace.BACK)
                        .otherwise(CullFace.NONE)
        );
        CheckBox wireframe = new CheckBox("Wireframe");
        meshView.drawModeProperty().bind(
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
        meshView.materialProperty().bind(
                Bindings.when(
                        texture.selectedProperty())
                        .then(texturedMaterial)
                        .otherwise((PhongMaterial) null)
        );

        VBox controls = new VBox(10, rotate, texture, cull, wireframe);
        controls.setPadding(new Insets(10));
        return controls;
    }

    private RotateTransition rotate3dGroup(Group group) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(10), group);
        rotate.setAxis(Rotate.X_AXIS);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        return rotate;
    }
}

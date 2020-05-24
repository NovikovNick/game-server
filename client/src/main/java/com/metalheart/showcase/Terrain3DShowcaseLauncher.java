package com.metalheart.showcase;

import com.metalheart.model.physic.Vector3d;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.metalheart.showcase.Terrain3DShowcaseLauncher.Vertex.*;
import static java.util.Arrays.asList;

public class Terrain3DShowcaseLauncher extends Application {

    private static final int VIEWPORT_SIZE = 1600;

    private static final double MODEL_SCALE_FACTOR = 400;
    private static final double MODEL_X_OFFSET = 0;
    private static final double MODEL_Y_OFFSET = 0;
    private static final double MODEL_Z_OFFSET = VIEWPORT_SIZE / 2;

    private static final String textureLoc = "cubeSide.jpg";

    private Image texture;
    private PhongMaterial texturedMaterial = new PhongMaterial();

    private MeshView meshView;

    private MeshView loadMeshView(List<Vector3d> data) {

        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(new float[] {
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

    private Group buildScene() {

        List<Vector3d> data = new ArrayList<>();
        for (int x = 0; x < 1; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 1; z++) {
                    data.add(new Vector3d(x, y, z));
                }
            }
        }
        MeshView mesh = loadMeshView(data);

        mesh.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
        mesh.setTranslateY(VIEWPORT_SIZE / 2 * 9.0 / 16 + MODEL_Y_OFFSET);
        mesh.setTranslateZ(VIEWPORT_SIZE / 2 + MODEL_Z_OFFSET);
        mesh.setScaleX(MODEL_SCALE_FACTOR);
        mesh.setScaleY(MODEL_SCALE_FACTOR);
        mesh.setScaleZ(MODEL_SCALE_FACTOR);

        meshView = mesh;

        return new Group(meshView);
    }

    @Override
    public void start(Stage stage) {
        texture = new Image(textureLoc);
        texturedMaterial.setDiffuseMap(texture);

        Group group = buildScene();

        RotateTransition rotate = rotate3dGroup(group);

        VBox layout = new VBox(
                createControls(rotate),
                createScene3D(group)
        );

        stage.setTitle("Model Viewer");

        Scene scene = new Scene(layout, Color.CORNSILK);
        stage.setScene(scene);
        stage.show();
    }

    private SubScene createScene3D(Group group) {
        SubScene scene3d = new SubScene(group, VIEWPORT_SIZE, VIEWPORT_SIZE * 9.0 / 16, true, SceneAntialiasing.BALANCED);
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
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setCycleCount(RotateTransition.INDEFINITE);

        return rotate;
    }

    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        launch(args);
    }

    /**
     <pre>
          z
          |
         v8-------v7
         /|       /|
        / |      / |
      v5-------v6  |
       |  |     |  |
       |  v4----| v3 - - y
       | /      | /
       |/       |/
      v1-------v2
      /
     x
     </pre>
     */
    public enum Vertex {
        V1(1,0,0),
        V2(1,1,0),
        V3(0,1,0),
        V4(0,0,0),
        V5(1,0,1),
        V6(1,1,1),
        V7(0,1,1),
        V8(0,0,1);

        final int d0, d1, d2;

        Vertex(int d0, int d1, int d2) {
            this.d0 = d0;
            this.d1 = d1;
            this.d2 = d2;
        }

        Vector3d toVector3d() {
            return new Vector3d(d0,d1,d2);
        }
    }

    public enum VoxelFace {

        FRONT   (new Vector3d(  1,   0,   0), asList(V1, V2, V6, V5)),
        BACK    (new Vector3d( -1,   0,   0), asList(V3, V4, V8, V7)),
        LEFT    (new Vector3d(  0,  -1,   0), asList(V4, V1, V5, V8)),
        RIGHT   (new Vector3d(  0,   1,   0), asList(V2, V3, V7, V6)),
        TOP     (new Vector3d(  0,   0,   1), asList(V5, V6, V7, V8)),
        BOTTOM  (new Vector3d(  0,   0,  -1), asList(V4, V3, V2, V1));

        final Vector3d direction;
        final List<Vertex> vertices;

        VoxelFace(Vector3d direction, List<Vertex> vertices) {
            this.vertices = vertices;
            this.direction = direction;
        }
    }
}
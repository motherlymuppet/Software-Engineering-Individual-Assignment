package org.stevenlowes.university.seassignment.guis;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.stevenlowes.university.seassignment.database.Database;
import org.stevenlowes.university.seassignment.guis.interpolators.EaseInPowInterpolator;
import org.stevenlowes.university.seassignment.guis.interpolators.EaseOutPowInterpolator;

import java.sql.SQLException;
import java.util.Stack;

public class SceneSwitcher extends Application {
    private Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    private int startWidth = (int) (screenBounds.getWidth() * 0.55);
    private int startHeight = (int) (screenBounds.getHeight() * 0.55);
    @NotNull
    private Pane root = new StackPane();
    @NotNull
    private Scene scene = new Scene(root, startWidth, startHeight);
    @NotNull
    private Stack<Node> nodes = new Stack<>();
    @NotNull
    private Stage stage;

    public static void main(String[] args) throws SQLException {
        Database.start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws SQLException {
        Node firstNode = getFirstNode();
        nodes.add(firstNode);

        root.getChildren().setAll(nodes.peek());

        stage = primaryStage;
        stage.setTitle("Patient Information Portal");
        stage.setScene(scene);
        stage.show();
    }

    private Node getFirstNode() {
        return new MainPage(this);
    }

    public void back() {
        back(1);
    }

    public void switchTo(Node newNode) {
        nodes.add(newNode);
        switchTo(newNode, HorizontalDirection.LEFT);
    }

    private void switchTo(Node newNode, HorizontalDirection direction) {
        // Create snapshots with the last state of the scenes

        double widthD = scene.getWidth();
        double heightD = scene.getHeight();
        int width = (int) widthD;
        int height = (int) heightD;

        WritableImage wi = new WritableImage(width, height);
        ImageView curIV = new ImageView(scene.snapshot(wi));

        Pane altRoot = new StackPane();
        altRoot.getChildren().setAll(newNode);
        Scene altScene = new Scene(altRoot, widthD, heightD);

        wi = new WritableImage(width, height);
        ImageView altIV = new ImageView(altScene.snapshot(wi));

        StackPane pane;
        if (direction == HorizontalDirection.RIGHT) {
            pane = new StackPane(altIV, curIV);
        }
        else {
            pane = new StackPane(curIV, altIV);
        }

        pane.setPrefSize(width, height);

        root.getChildren().setAll(pane);

        Timeline timeline = new Timeline(60);

        KeyValue kv;
        if (direction == HorizontalDirection.RIGHT) {
            curIV.setTranslateX(0);
            altIV.setTranslateX(0);
            kv = new KeyValue(curIV.translateXProperty(), width, new EaseInPowInterpolator());
        }
        else {
            curIV.setTranslateX(0);
            altIV.setTranslateX(width);
            kv = new KeyValue(altIV.translateXProperty(), 0, new EaseOutPowInterpolator());
        }
        KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        timeline.getKeyFrames().add(kf);

        timeline.setOnFinished(t -> {
            root.getChildren().setAll(newNode);
        });

        timeline.play();
    }

    public void back(int count) {
        for (int i = 0; i < count; i++) {
            nodes.pop();
        }
        Node newNode = nodes.peek();
        switchTo(newNode, HorizontalDirection.RIGHT);
    }
}

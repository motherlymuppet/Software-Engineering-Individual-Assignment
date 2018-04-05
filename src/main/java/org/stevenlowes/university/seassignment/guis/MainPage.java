package org.stevenlowes.university.seassignment.guis;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;
import org.stevenlowes.university.seassignment.dbao.Patient;

import java.util.concurrent.ExecutionException;

public class MainPage extends StackPane {
    public MainPage(SceneSwitcher switcher) {
        setPadding(new Insets(48));
        setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        setOnMouseClicked(mouseEvent -> {
            Task<PatientSearch> task = new Task<PatientSearch>() {
                @Override
                protected PatientSearch call() throws Exception {
                    return new PatientSearch(switcher, Patient.list());
                }
            };

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            task.setOnSucceeded(workerStateEvent -> {
                try {
                    switcher.switchTo(task.get());
                }
                catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        });

        Image image = new Image(MainPage.class.getResourceAsStream("/gui/logo.png"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        imageView.setFitWidth(screenBounds.getWidth() / 4);
        imageView.setFitHeight(screenBounds.getHeight() / 4);

        Text title = new Text("Patient Information Service");
        title.setFont(Font.font("Arial", 36));
        title.setFill(new Color(0.4, 0.4, 0.4, 1));

        VBox vBox = new VBox(12, imageView, title);
        vBox.setAlignment(Pos.CENTER);
        setAlignment(vBox, Pos.TOP_CENTER);

        Text text = new Text("Click Anywhere to Enter");
        text.setFont(Font.font("Arial", FontWeight.LIGHT, 24));
        setAlignment(text, Pos.BOTTOM_CENTER);

        getChildren().add(vBox);
        getChildren().add(text);

        KeyFrame transparent = new KeyFrame(Duration.millis(300), "transparent", new KeyValue(text.fillProperty(), Color.DARKGREY, Interpolator.EASE_BOTH));
        KeyFrame opaque = new KeyFrame(Duration.millis(600), "opaque", new KeyValue(text.fillProperty(), Color.BLACK, Interpolator.EASE_BOTH));
        KeyFrame wait = new KeyFrame(Duration.millis(1000), "wait");

        Timeline timeline = new Timeline(60, transparent, opaque, wait);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}

package org.stevenlowes.university.seassignment.guis;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.stevenlowes.university.seassignment.dbao.Patient;

import java.sql.SQLException;

public class DeletePatient extends StackPane {
    public DeletePatient(SceneSwitcher switcher, PatientSearch patientSearch, Patient patient) {
        setPadding(new Insets(48));
        setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        Text title = new Text("You are about to PERMANENTLY delete Patient ID " + patient.getId() + " (" + patient.getFirstName() + " " + patient.getSurname() + ")");
        title.setFont(Font.font("Tahoma", 24));
        title.setFill(Color.RED);
        //title.wrappingWidthProperty().bind(widthProperty());
        setAlignment(title, Pos.TOP_CENTER);

        Button backButton = new Button("Cancel");
        backButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/gui/back.png"))));
        backButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(4), null)));
        backButton.setOnAction(e -> switcher.back());

        Button deleteButton = new Button("Delete Patient");
        deleteButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/gui/delete.png"))));
        deleteButton.setBackground(new Background(new BackgroundFill(Color.ORANGERED, new CornerRadii(4), null)));
        deleteButton.setOnAction(e -> {
            try {
                patient.delete();
                patientSearch.removePatient(patient);
                switcher.back(2);
            }
            catch (SQLException e1) {
                e1.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error deleting patient");
                alert.setContentText("Could not delete patient from database. Try again later.");
                alert.showAndWait();
                switcher.back();
            }
        });

        HBox buttons = new HBox(12, backButton, deleteButton);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        setAlignment(buttons, Pos.BOTTOM_CENTER);

        getChildren().add(buttons);
        getChildren().add(title);
    }
}

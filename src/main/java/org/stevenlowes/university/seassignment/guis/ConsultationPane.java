package org.stevenlowes.university.seassignment.guis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import jfxtras.scene.control.CalendarTimePicker;
import jfxtras.scene.control.ImageViewButton;
import org.stevenlowes.university.seassignment.dbao.Consultant;
import org.stevenlowes.university.seassignment.dbao.Consultation;
import org.stevenlowes.university.seassignment.dbao.Patient;
import org.stevenlowes.university.seassignment.dbao.Practice;
import org.stevenlowes.university.seassignment.guis.utils.FixedLabel;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.function.Consumer;

public class ConsultationPane extends GridPane {
    public ConsultationPane(SceneSwitcher switcher, Patient patient, Consumer<Consultation> consultationAdder) throws SQLException {
        setPadding(new Insets(24));
        setVgap(12);
        setHgap(12);
        setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col1.setHalignment(HPos.CENTER);

        Image backIcon = new Image(getClass().getResourceAsStream("/gui/back.png"));
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> switcher.back());
        backButton.setGraphic(new ImageView(backIcon));

        Text title = new Text("Patient View - ID " + patient.getId());
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 24.0));
        HBox titleBox = new HBox(12.0, backButton, title);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        add(titleBox, 0, 0, 4, 1);

        List<Practice> practiceList = Practice.list();
        ObservableList<Practice> practices = FXCollections.observableList(practiceList);
        ComboBox<Practice> practiceComboBox = new ComboBox<>(practices);
        practiceComboBox.setValue(patient.getPractice());
        Label practiceLabel = new FixedLabel("Practice", practiceComboBox);
        add(practiceLabel, 1, 1);
        add(practiceComboBox, 2, 1);

        List<Consultant> consultantsList = Consultant.list();
        ObservableList<Consultant> consultants = FXCollections.observableList(consultantsList);
        ComboBox<Consultant> consultantComboBox = new ComboBox<>(consultants);
        consultantComboBox.setValue(patient.getConsultant());
        Label consultantLabel = new FixedLabel("Consultant", consultantComboBox);
        add(consultantLabel, 1, 2);
        add(consultantComboBox, 2, 2);

        consultantComboBox.prefWidthProperty().bind(practiceComboBox.widthProperty());

        CalendarTimePicker timePicker = new CalendarTimePicker();
        timePicker.setMinuteStep(5);
        Label timeLabel = new FixedLabel("Time", timePicker);
        add(timeLabel, 1, 3);
        add(timePicker, 2, 3);

        Button saveButton = new Button("Save", new ImageView(new Image(getClass().getResourceAsStream("/gui/save.png"))));
        saveButton.setOnAction(e -> {
            long practiceId = practiceComboBox.getValue().getId();
            long consultantId = consultantComboBox.getValue().getId();
            long patientId = patient.getId();

            Calendar calendar = timePicker.getCalendar();
            Instant time = calendar.toInstant();

            try {
                Consultation consultation = new Consultation(time, practiceId, consultantId, patientId);
                consultationAdder.accept(consultation);
                switcher.back();
            }
            catch (SQLException ex){
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("An error has occured when trying to save the consultation. Try again later.");
                alert.showAndWait();
            }
        });

        add(saveButton, 2, 4);

        Pane leftPane = new Pane();
        Pane rightPane = new Pane();

        add(leftPane, 0, 1, 1, 4);
        add(rightPane, 3, 1, 1, 4);

        GridPane.setHgrow(leftPane, Priority.ALWAYS);
        GridPane.setHgrow(timePicker, Priority.ALWAYS);
        GridPane.setHgrow(rightPane, Priority.ALWAYS);
    }
}

package org.stevenlowes.university.seassignment.guis;

import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.stevenlowes.university.seassignment.dbao.Consultation;
import org.stevenlowes.university.seassignment.dbao.Medicine;
import org.stevenlowes.university.seassignment.dbao.Patient;
import org.stevenlowes.university.seassignment.dbao.Treatment;
import org.stevenlowes.university.seassignment.guis.utils.DeleteCell;
import org.stevenlowes.university.seassignment.guis.utils.FixedLabel;
import org.stevenlowes.university.seassignment.guis.utils.UnselectableModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PatientView extends GridPane {

    private static final Background background = new Background(new BackgroundFill(new Color(0.9, 0.9, 0.9, 1.0), new CornerRadii(4), null));
    private static final Border border = new Border(new BorderStroke(Color.DARKGREY, BorderStrokeStyle.SOLID, new CornerRadii(4), null));
    private final SceneSwitcher switcher;

    public PatientView(@NotNull SceneSwitcher switcher, PatientSearch patientSearch, Patient patient) throws SQLException {
        this.switcher = switcher;
        try {
            setPadding(new Insets(24));
            setVgap(12);
            setHgap(12);
            setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

            Image backIcon = new Image(getClass().getResourceAsStream("/gui/back.png"));
            Button backButton = new Button("Back");
            backButton.setOnAction(event -> switcher.back());
            backButton.setGraphic(new ImageView(backIcon));

            Text title = new Text("Patient View - ID " + patient.getId());
            title.setFont(Font.font("Tahoma", FontWeight.BOLD, 24.0));
            HBox titleBox = new HBox(12.0, backButton, title);
            titleBox.setAlignment(Pos.CENTER_LEFT);

            add(titleBox, 0, 0, 2, 1);

            Text infoColumnTitle = new Text("Info");
            infoColumnTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16.0));
            add(infoColumnTitle, 0, 1);
            GridPane.setHalignment(infoColumnTitle, HPos.CENTER);
            ScrollPane infoPane = getInfoPane(patient);
            infoPane.setFocusTraversable(false);
            infoPane.setMouseTransparent(true);
            add(infoPane, 0, 2, 1, 3);

            Text treatmentColumnTitle = new Text("Prescribed Treatment");
            treatmentColumnTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
            add(treatmentColumnTitle, 1, 1);
            ListView<Treatment> treatmentsPane = getTreatmentsPane(patient, treatmentColumnTitle.xProperty());
            add(treatmentsPane, 1, 2);

            Text medicineColumnTitle = new Text("Prescribed Medicine");
            medicineColumnTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
            add(medicineColumnTitle, 1, 3);
            ListView<Medicine> medicinesPane = getMedicinesPane(patient, medicineColumnTitle.xProperty());
            add(medicinesPane, 1, 4);

            Text consultationsColumnTitle = new Text("Booked Consultations");
            consultationsColumnTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16.0));
            add(consultationsColumnTitle, 2, 1);
            GridPane.setHalignment(consultationsColumnTitle, HPos.CENTER);
            Region consultationsPane = getConsultationsPane(patient);
            add(consultationsPane, 2, 2, 1, 3);

            Button editButton = new Button("Edit Details/Prescriptions");
            editButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/gui/edit.png"))));
            editButton.setOnAction(e -> {
                try {
                    switcher.switchTo(new PatientEditor(switcher, patientSearch, patient));
                }
                catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });
            Button deleteButton = new Button("Delete Patient");
            deleteButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/gui/delete.png"))));
            deleteButton.setOnAction(e -> switcher.switchTo(new DeletePatient(switcher, patientSearch, patient)));
            HBox buttons = new HBox(12.0, editButton, deleteButton);
            buttons.setAlignment(Pos.CENTER);
            add(buttons, 0, 5, 3, 1);

            GridPane.setHgrow(infoPane, Priority.NEVER);
            GridPane.setHgrow(consultationsPane, Priority.ALWAYS);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static void setReadOnly(TextField field) {
        field.setMouseTransparent(true);
        field.setFocusTraversable(false);
        field.setEditable(false);
        field.setBackground(background);
        field.setBorder(border);
    }

    private ListView<Treatment> getTreatmentsPane(Patient patient, DoubleProperty widthProperty) throws SQLException {
        return getListPane(patient.getTreatments(), "No Prescribed Treatments", widthProperty);
    }

    private ListView<Medicine> getMedicinesPane(Patient patient, DoubleProperty widthProperty) throws SQLException {
        return getListPane(patient.getMedicines(), "No Prescribed Medicine", widthProperty);
    }

    private <T> ListView<T> getListPane(List<T> list, String placeHolder, DoubleProperty widthProperty){
        ObservableList<T> obsList = FXCollections.observableList(list);
        ListView<T> listView = new ListView<>(obsList);
        listView.setPlaceholder(new Label(placeHolder));
        listView.setMouseTransparent(true);
        listView.setFocusTraversable(false);
        listView.setEditable(false);
        listView.prefWidthProperty().bind(widthProperty);
        return listView;
    }

    private Region getConsultationsPane(Patient patient) throws SQLException {
        List<Consultation> currentConsultations = patient.getConsultations();
        ObservableList<Consultation> currentObs = FXCollections.observableList(currentConsultations);

        Consumer<Integer> delete = i -> {
            try {
                int index = i;
                Consultation deleted = currentObs.get(index);
                deleted.delete();
                currentObs.remove(index);
            }
            catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("Unable to delete consultation. Try again later");
                alert.showAndWait();
            }
        };

        ListView<Consultation> listView = new ListView<>(currentObs);
        listView.setPlaceholder(new Label("No Consultations Booked"));
        listView.setCellFactory(param -> new DeleteCell<>(delete));
        listView.setEditable(false);
        listView.setSelectionModel(new UnselectableModel<>());

        Button addButton = new Button("Add Consultation");
        addButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/gui/add_16.png"))));

        Consumer<Consultation> consultationAdder = consultation -> listView.getItems().add(consultation);
        addButton.setOnAction(e -> {
            try {
                switcher.switchTo(new ConsultationPane(switcher, patient, consultationAdder));
            }
            catch (SQLException e1) {
                e1.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("Could not load the new consultation screen. Try again later");
                alert.showAndWait();
            }
        });

        addButton.prefWidthProperty().bind(listView.widthProperty());

        GridPane gridPane = new GridPane();
        gridPane.setVgap(4);
        gridPane.setHgap(4);
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        gridPane.setBorder(null);

        gridPane.add(listView, 0, 0);
        gridPane.add(addButton, 0, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.CENTER);
        col1.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(col1);

        GridPane.setVgrow(listView, Priority.ALWAYS);

        return gridPane;
    }

    private ScrollPane getInfoPane(Patient patient) throws SQLException {
        GridPane gridPane = new GridPane();
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setHgap(12);
        gridPane.setVgap(4);
        gridPane.setPadding(new Insets(12));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.NEVER);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(col1, col2, col3, col4);

        int row = 0;
        TextField firstNameField = new TextField(patient.getFirstName());
        firstNameField.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(firstNameField);
        Label firstNameLabel = new FixedLabel("First Name:", firstNameField);
        gridPane.add(firstNameLabel, 0, row);
        gridPane.add(firstNameField, 1, row);

        row += 2;
        TextField dobField = new TextField(patient.getDateOfBirth().toString());
        dobField.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(dobField);
        Label dobLabel = new FixedLabel("Date of Birth", dobField);
        gridPane.add(dobLabel, 0, row);
        gridPane.add(dobField, 1, row);

        row++;
        TextField phoneField = new TextField(patient.getPhoneNumber());
        phoneField.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(phoneField);
        Label phoneLabel = new FixedLabel("Phone Number:", phoneField);
        gridPane.add(phoneLabel, 0, row);
        gridPane.add(phoneField, 1, row);

        row += 2;
        TextField nokFirstNameField = new TextField(patient.getNextOfKinFirstName());
        nokFirstNameField.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(nokFirstNameField);
        Label nokFirstNameLabel = new FixedLabel("NOK First Name:", nokFirstNameField);
        gridPane.add(nokFirstNameLabel, 0, row);
        gridPane.add(nokFirstNameField, 1, row);

        row++;
        TextField nokSurnameField = new TextField(patient.getNextOfKinSurname());
        nokSurnameField.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(nokSurnameField);
        Label nokSurnameLabel = new FixedLabel("NOK Surname:", nokSurnameField);
        nokSurnameLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        nokSurnameLabel.setLabelFor(nokSurnameField);
        gridPane.add(nokSurnameLabel, 0, row);
        gridPane.add(nokSurnameField, 1, row);

        row++;
        TextField nokPhoneNumberField = new TextField(patient.getNextOfKinPhoneNumber());
        nokPhoneNumberField.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(nokPhoneNumberField);
        Label nokPhoneNumberLabel = new FixedLabel("NOK Phone:", nokPhoneNumberField);
        gridPane.add(nokPhoneNumberLabel, 0, row);
        gridPane.add(nokPhoneNumberField, 1, row);

        row = 0;
        TextField surnameField = new TextField(patient.getSurname());
        surnameField.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(surnameField);
        Label surnameLabel = new FixedLabel("Surname:", surnameField);
        gridPane.add(surnameLabel, 2, row);
        gridPane.add(surnameField, 3, row);

        row += 2;
        TextField practiceField = new TextField(patient.getPractice().toString());
        practiceField.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(practiceField);
        Label practiceLabel = new FixedLabel("Practice:", practiceField);
        gridPane.add(practiceLabel, 2, row);
        gridPane.add(practiceField, 3, row);

        row++;
        TextField consultantField = new TextField(patient.getConsultant().toString());
        consultantField.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(consultantField);
        Label consultantLabel = new FixedLabel("Consultant:", consultantField);
        gridPane.add(consultantLabel, 2, row);
        gridPane.add(consultantField, 3, row);

        row++;
        TextField riskCategoryField = new TextField(patient.getRiskCategory().toString());
        riskCategoryField.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(riskCategoryField);
        Label riskCategoryLabel = new FixedLabel("Risk Category:", riskCategoryField);
        gridPane.add(riskCategoryLabel, 2, row);
        gridPane.add(riskCategoryField, 3, row);

        row++;
        TextField address1Field = new TextField(patient.getAddress1());
        address1Field.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(address1Field);
        Label address1Label = new FixedLabel("Address (1):", address1Field);
        gridPane.add(address1Label, 2, row);
        gridPane.add(address1Field, 3, row);

        row++;
        TextField address2Field = new TextField(patient.getAddress2());
        address2Field.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(address2Field);
        Label address2Label = new FixedLabel("Address (2):", address2Field);
        gridPane.add(address2Label, 2, row);
        gridPane.add(address2Field, 3, row);

        row++;
        TextField address3Field = new TextField(patient.getAddress3());
        address3Field.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(address3Field);
        Label address3Label = new FixedLabel("Address (3):", address3Field);
        gridPane.add(address3Label, 2, row);
        gridPane.add(address3Field, 3, row);

        row++;
        TextField address4Field = new TextField(patient.getAddress4());
        address4Field.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(address4Field);
        Label address4Label = new FixedLabel("Address (4):", address4Field);
        gridPane.add(address4Label, 2, row);
        gridPane.add(address4Field, 3, row);

        row++;
        TextField address5Field = new TextField(patient.getAddress5());
        address5Field.setMinSize(TextField.USE_PREF_SIZE, TextField.USE_PREF_SIZE);
        setReadOnly(address5Field);
        Label address5Label = new FixedLabel("Address (5):", address5Field);
        gridPane.add(address5Label, 2, row);
        gridPane.add(address5Field, 3, row);

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
}


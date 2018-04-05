package org.stevenlowes.university.seassignment.guis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
import org.jetbrains.annotations.Nullable;
import org.stevenlowes.university.seassignment.dbao.Consultant;
import org.stevenlowes.university.seassignment.dbao.Medicine;
import org.stevenlowes.university.seassignment.dbao.Patient;
import org.stevenlowes.university.seassignment.dbao.Practice;
import org.stevenlowes.university.seassignment.dbao.RiskCategory;
import org.stevenlowes.university.seassignment.dbao.Treatment;
import org.stevenlowes.university.seassignment.guis.utils.DeleteCell;
import org.stevenlowes.university.seassignment.guis.utils.FixedLabel;
import org.stevenlowes.university.seassignment.guis.utils.LimitedTextField;
import org.stevenlowes.university.seassignment.guis.utils.UnselectableModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PatientEditor extends GridPane {

    private Background background = new Background(new BackgroundFill(new Color(0.98, 0.98, 0.98, 1.0), new CornerRadii(4), null));
    private Border border = new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(4), null));

    public PatientEditor(@NotNull SceneSwitcher switcher, @NotNull PatientSearch patientSearch, @Nullable Patient patient) throws SQLException {
        try {
            setPadding(new Insets(24));
            setVgap(12);
            setHgap(12);
            setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHalignment(HPos.CENTER);
            col1.setHgrow(Priority.NEVER);

            ColumnConstraints col2 = new ColumnConstraints();
            col2.setHalignment(HPos.CENTER);
            col2.setHgrow(Priority.ALWAYS);

            ColumnConstraints col3 = new ColumnConstraints();
            col3.setHalignment(HPos.CENTER);
            col3.setHgrow(Priority.ALWAYS);

            getColumnConstraints().addAll(col1, col2, col3);

            Image backIcon = new Image(getClass().getResourceAsStream("/gui/back.png"));
            Button backButton = new Button("Cancel");
            backButton.setOnAction(event -> switcher.back());
            backButton.setGraphic(new ImageView(backIcon));

            String titleText = "Patient Edit - ";
            if (patient == null) {
                titleText += "New Patient";
            }
            else {
                titleText += "ID " + patient.getId();
            }

            Text title = new Text(titleText);
            title.setFont(Font.font("Tahoma", FontWeight.BOLD, 24.0));

            HBox titleBox = new HBox(12.0, backButton, title);
            titleBox.setAlignment(Pos.CENTER_LEFT);
            add(titleBox, 0, 0, 3, 1);


            Pair<Region, Supplier<PatientInfo>> infoPanePair = getInfoPane(patient);
            Region infoPane = infoPanePair.getKey();
            Supplier<PatientInfo> infoPaneGetter = infoPanePair.getValue();
            infoPane.setMinSize(Region.USE_PREF_SIZE, 0);
            add(infoPane, 0, 1);
            GridPane.setVgrow(infoPane, Priority.ALWAYS);


            List<Treatment> patientTreatments;
            if (patient == null) {
                patientTreatments = null;
            }
            else {
                patientTreatments = patient.getTreatments();
            }

            List<Treatment> allTreatments = Treatment.list();

            Pair<Region, Supplier<List<Treatment>>> treatmentPair = getPane(patientTreatments, allTreatments, "Treatment");
            Region treatmentPane = treatmentPair.getKey();
            Supplier<List<Treatment>> treatmentGetter = treatmentPair.getValue();
            add(treatmentPane, 1, 1);
            GridPane.setVgrow(treatmentPane, Priority.ALWAYS);

            List<Medicine> patientMedicine;
            if (patient == null) {
                patientMedicine = null;
            }
            else {
                patientMedicine = patient.getMedicines();
            }

            List<Medicine> allMedicines = Medicine.list();

            Pair<Region, Supplier<List<Medicine>>> medicinePair = getPane(patientMedicine, allMedicines, "Medicine");
            Region medicinePane = medicinePair.getKey();
            Supplier<List<Medicine>> medicineGetter = medicinePair.getValue();
            add(medicinePane, 2, 1);
            GridPane.setVgrow(medicinePane, Priority.ALWAYS);

            Button saveButton = new Button("Save Patient");
            saveButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/gui/save.png"))));
            HBox buttons = new HBox(12.0, saveButton);

            saveButton.setOnAction(event -> {
                List<Medicine> medicines = medicineGetter.get();
                List<Treatment> treatments = treatmentGetter.get();
                PatientInfo patientInfo = infoPaneGetter.get();
                boolean valid = patientInfo.validate();

                if (valid) {

                    List<Long> medicineIDs = medicines.stream().map(Medicine::getId).collect(Collectors.toList());
                    List<Long> treatmentIDs = treatments.stream().map(Treatment::getId).collect(Collectors.toList());
                    try {
                        if (patient == null) {
                            Patient newPatient = new Patient(patientInfo.surname,
                                                             patientInfo.firstName,
                                                             patientInfo.address1,
                                                             patientInfo.address2,
                                                             patientInfo.address3,
                                                             patientInfo.address4,
                                                             patientInfo.address5,
                                                             patientInfo.dateOfBirth,
                                                             patientInfo.phoneNumber,
                                                             patientInfo.practice.getId(),
                                                             patientInfo.nextOfKinSurname,
                                                             patientInfo.nextOfKinFirstName,
                                                             patientInfo.nextOfKinPhoneNumber,
                                                             patientInfo.riskCategory.getId(),
                                                             patientInfo.consultant.getId(),
                                                             medicineIDs,
                                                             treatmentIDs);
                            patientSearch.addPatient(newPatient);
                            switcher.back();
                        }
                        else {
                            patient.update(patientInfo.surname,
                                           patientInfo.firstName,
                                           patientInfo.address1,
                                           patientInfo.address2,
                                           patientInfo.address3,
                                           patientInfo.address4,
                                           patientInfo.address5,
                                           patientInfo.dateOfBirth,
                                           patientInfo.phoneNumber,
                                           patientInfo.practice.getId(),
                                           patientInfo.nextOfKinSurname,
                                           patientInfo.nextOfKinFirstName,
                                           patientInfo.nextOfKinPhoneNumber,
                                           patientInfo.riskCategory.getId(),
                                           patientInfo.consultant.getId(),
                                           medicineIDs,
                                           treatmentIDs);
                            patientSearch.refreshList();
                            switcher.back(2);
                        }
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error Updating Information");
                        alert.setContentText("Could not update patient details in database. Try again later.");
                        alert.showAndWait();
                    }
                }
            });

            if (patient != null) {
                Button deleteButton = new Button("Delete Patient");
                deleteButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/gui/delete.png"))));
                deleteButton.setOnAction(e -> switcher.switchTo(new DeletePatient(switcher, patientSearch, patient)));
                buttons.getChildren().add(deleteButton);
            }

            buttons.setAlignment(Pos.CENTER);
            add(buttons, 0, 2, 3, 1);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Pair<Region, Supplier<PatientInfo>> getInfoPane(Patient patient) throws SQLException {
        Text infoColumnTitle = new Text("Info");
        infoColumnTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16.0));

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setHgap(24);
        gridPane.setVgap(4);
        gridPane.setPadding(new Insets(12));
        gridPane.setBackground(background);
        gridPane.getColumnConstraints().add(new ColumnConstraints(80));


        String firstName = patient == null ? "" : patient.getFirstName();
        String surname = patient == null ? "" : patient.getSurname();
        String phoneNumber = patient == null ? "" : patient.getPhoneNumber();
        String address1 = patient == null ? "" : patient.getAddress1();
        String address2 = patient == null ? "" : patient.getAddress2();
        String address3 = patient == null ? "" : patient.getAddress3();
        String address4 = patient == null ? "" : patient.getAddress4();
        String address5 = patient == null ? "" : patient.getAddress5();
        LocalDate dateOfBirth = patient == null ? null : patient.getDateOfBirth();
        String nextOfKinFirstName = patient == null ? "" : patient.getNextOfKinFirstName();
        String nextOfKinSurname = patient == null ? "" : patient.getNextOfKinSurname();
        String nextOfKinPhoneNumber = patient == null ? "" : patient.getNextOfKinPhoneNumber();
        Consultant consultant = patient == null ? null : patient.getConsultant();
        Practice practice = patient == null ? null : patient.getPractice();
        RiskCategory riskCategory = patient == null ? null : patient.getRiskCategory();

        gridPane.add(infoColumnTitle, 0, 0, 2, 1);

        int row = 1;
        TextField firstNameField = new LimitedTextField(firstName, 35);
        Label firstNameLabel = new FixedLabel("First Name*:", firstNameField);
        gridPane.add(firstNameLabel, 0, row);
        gridPane.add(firstNameField, 1, row);

        row++;
        TextField surnameField = new LimitedTextField(surname, 35);
        Label surnameLabel = new FixedLabel("Surname*:", surnameField);
        gridPane.add(surnameLabel, 0, row);
        gridPane.add(surnameField, 1, row);

        row++;
        DatePicker datePicker = new DatePicker(dateOfBirth);
        datePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            datePicker.setValue(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
        });
        Label dobLabel = new FixedLabel("Date of Birth*", datePicker);
        gridPane.add(dobLabel, 0, row);
        gridPane.add(datePicker, 1, row);

        row++;
        TextField phoneField = new LimitedTextField(phoneNumber, 12);
        Label phoneLabel = new FixedLabel("Phone Number*:", phoneField);
        gridPane.add(phoneLabel, 0, row);
        gridPane.add(phoneField, 1, row);

        row++;
        ComboBox<Practice> practiceComboBox = new ComboBox<>(FXCollections.observableList(Practice.list()));
        practiceComboBox.prefWidthProperty().bind(firstNameField.widthProperty());
        practiceComboBox.setValue(practice);
        Label practiceLabel = new FixedLabel("Practice*:", practiceComboBox);
        gridPane.add(practiceLabel, 0, row);
        gridPane.add(practiceComboBox, 1, row);

        row++;
        ComboBox<Consultant> consultantComboBox = new ComboBox<>(FXCollections.observableList(Consultant.list()));
        consultantComboBox.prefWidthProperty().bind(firstNameField.widthProperty());
        consultantComboBox.setValue(consultant);
        Label consultantLabel = new FixedLabel("Consultant*:", consultantComboBox);
        gridPane.add(consultantLabel, 0, row);
        gridPane.add(consultantComboBox, 1, row);

        row++;
        ComboBox<RiskCategory> riskCategoryComboBox = new ComboBox<>(FXCollections.observableList(RiskCategory.list()));
        riskCategoryComboBox.prefWidthProperty().bind(firstNameField.widthProperty());
        riskCategoryComboBox.setValue(riskCategory);
        Label riskCategoryLabel = new FixedLabel("Risk Category*:", riskCategoryComboBox);
        gridPane.add(riskCategoryLabel, 0, row);
        gridPane.add(riskCategoryComboBox, 1, row);

        row++;
        TextField address1Field = new LimitedTextField(address1, 35);
        Label address1Label = new FixedLabel("Address (1)*:", address1Field);
        gridPane.add(address1Label, 0, row);
        gridPane.add(address1Field, 1, row);

        row++;
        TextField address2Field = new LimitedTextField(address2, 35);
        Label address2Label = new FixedLabel("Address (2)*:", address2Field);
        gridPane.add(address2Label, 0, row);
        gridPane.add(address2Field, 1, row);

        row++;
        TextField address3Field = new LimitedTextField(address3, 35);
        Label address3Label = new FixedLabel("Address (3):", address3Field);
        gridPane.add(address3Label, 0, row);
        gridPane.add(address3Field, 1, row);

        row++;
        TextField address4Field = new LimitedTextField(address4, 35);
        Label address4Label = new FixedLabel("Address (4):", address4Field);
        gridPane.add(address4Label, 0, row);
        gridPane.add(address4Field, 1, row);

        row++;
        TextField address5Field = new LimitedTextField(address5, 35);
        Label address5Label = new FixedLabel("Address (5):", address5Field);
        gridPane.add(address5Label, 0, row);
        gridPane.add(address5Field, 1, row);

        row++;
        TextField nokFirstNameField = new LimitedTextField(nextOfKinFirstName, 35);
        Label nokFirstNameLabel = new FixedLabel("NOK First Name*:", nokFirstNameField);
        gridPane.add(nokFirstNameLabel, 0, row);
        gridPane.add(nokFirstNameField, 1, row);

        row++;
        TextField nokSurnameField = new LimitedTextField(nextOfKinSurname, 35);
        Label nokSurnameLabel = new FixedLabel("NOK Surname*:", nokSurnameField);
        gridPane.add(nokSurnameLabel, 0, row);
        gridPane.add(nokSurnameField, 1, row);

        row++;
        TextField nokPhoneNumberField = new LimitedTextField(nextOfKinPhoneNumber, 35);
        Label nokPhoneNumberLabel = new FixedLabel("NOK Phone*:", nokPhoneNumberField);
        gridPane.add(nokPhoneNumberLabel, 0, row);
        gridPane.add(nokPhoneNumberField, 1, row);

        ScrollPane infoScrollPane = new ScrollPane(gridPane);
        infoScrollPane.setFitToHeight(true);
        infoScrollPane.setFitToWidth(true);

        infoScrollPane.setBorder(border);

        Supplier<PatientInfo> supplier = () -> new PatientInfo(firstNameField.getText(),
                                                               surnameField.getText(),
                                                               phoneField.getText(),
                                                               address1Field.getText(),
                                                               address2Field.getText(),
                                                               address3Field.getText(),
                                                               address4Field.getText(),
                                                               address5Field.getText(),
                                                               datePicker.getValue(),
                                                               nokFirstNameField.getText(),
                                                               nokSurnameField.getText(),
                                                               nokPhoneNumberField.getText(),
                                                               consultantComboBox.getValue(),
                                                               practiceComboBox.getValue(),
                                                               riskCategoryComboBox.getValue());

        return new Pair<>(infoScrollPane, supplier);
    }

    private <T> Pair<Region, Supplier<List<T>>> getPane(List<T> current, List<T> all, String word) {
        Text title = new Text("Prescribed " + word);
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));

        if (current == null) {
            current = new ArrayList<>();
        }

        ObservableList<T> currentObs = FXCollections.observableList(current);

        ComboBox<T> comboBox = new ComboBox<>(FXCollections.observableList(all.stream().filter(t -> !currentObs.contains(t)).collect(Collectors.toList())));
        Label comboBoxLabel = new FixedLabel("Add " + word, comboBox);

        Consumer<Integer> delete = i -> {
            int index = i;
            currentObs.remove(index);
            comboBox.setItems(FXCollections.observableList(all.stream().filter(t -> !currentObs.contains(t)).collect(Collectors.toList())));
        };

        ListView<T> listView = new ListView<>(currentObs);
        listView.setPlaceholder(new Label("No Prescribed " + word));
        listView.setCellFactory(param -> new DeleteCell<>(delete));
        listView.setEditable(false);
        listView.setSelectionModel(new UnselectableModel<>());

        Button addButton = new Button();
        addButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/gui/add_16.png"))));
        addButton.setOnAction(e -> {
            T selected = comboBox.getSelectionModel().getSelectedItem();
            if (selected != null) {
                currentObs.add(selected);
                currentObs.sort(Comparator.comparing(T::toString));
                comboBox.setItems(FXCollections.observableList(all.stream().filter(t -> !currentObs.contains(t)).collect(Collectors.toList())));
            }
        });

        HBox comboBoxBox = new HBox(comboBox);
        comboBox.prefWidthProperty().bind(comboBoxBox.widthProperty());

        GridPane gridPane = new GridPane();
        gridPane.setVgap(4);
        gridPane.setHgap(4);
        gridPane.setPadding(new Insets(12));
        gridPane.setBackground(background);
        gridPane.setBorder(border);
        gridPane.add(title, 0, 0, 3, 1);
        gridPane.add(listView, 0, 1, 3, 1);
        gridPane.add(comboBoxLabel, 0, 2);
        gridPane.add(comboBoxBox, 1, 2);
        gridPane.add(addButton, 2, 2);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.CENTER);
        col1.setHgrow(Priority.NEVER);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHalignment(HPos.CENTER);
        col2.setHgrow(Priority.ALWAYS);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHalignment(HPos.CENTER);
        col3.setHgrow(Priority.NEVER);

        gridPane.getColumnConstraints().addAll(col1, col2, col3);

        GridPane.setVgrow(listView, Priority.ALWAYS);

        return new Pair<>(gridPane, () -> currentObs);
    }

    private static class PatientInfo {
        public final String firstName;
        public final String surname;
        public final String phoneNumber;
        public final String address1;
        public final String address2;
        public final String address3;
        public final String address4;
        public final String address5;
        public final LocalDate dateOfBirth;
        public final String nextOfKinFirstName;
        public final String nextOfKinSurname;
        public final String nextOfKinPhoneNumber;
        public final Consultant consultant;
        public final Practice practice;
        public final RiskCategory riskCategory;

        public PatientInfo(String firstName,
                           String surname,
                           String phoneNumber,
                           String address1,
                           String address2,
                           String address3,
                           String address4,
                           String address5,
                           LocalDate dateOfBirth,
                           String nextOfKinFirstName,
                           String nextOfKinSurname,
                           String nextOfKinPhoneNumber,
                           Consultant consultant,
                           Practice practice,
                           RiskCategory riskCategory) {
            this.firstName = firstName;
            this.surname = surname;
            this.phoneNumber = phoneNumber;
            this.address1 = address1;
            this.address2 = address2;
            this.address3 = address3;
            this.address4 = address4;
            this.address5 = address5;
            this.dateOfBirth = dateOfBirth;
            this.nextOfKinFirstName = nextOfKinFirstName;
            this.nextOfKinSurname = nextOfKinSurname;
            this.nextOfKinPhoneNumber = nextOfKinPhoneNumber;
            this.consultant = consultant;
            this.practice = practice;
            this.riskCategory = riskCategory;
        }

        /**
         * @return True if valid, false if invalid
         */
        public boolean validate() {
            List<String> errors = new ArrayList<>();

            if (nullEmpty(firstName)) {
                errors.add("You must enter a first name");
            }
            else if (firstName.length() > 35) {
                errors.add("First name cannot be more than 35 characters long");
            }

            if (nullEmpty(surname)) {
                errors.add("You must enter a surname");
            }
            else if (surname.length() > 35) {
                errors.add("surname cannot be more than 35 characters long");
            }

            if (nullEmpty(phoneNumber)) {
                errors.add("You must enter a phone number");
            }
            else if (phoneNumber.length() > 35) {
                errors.add("First name cannot be more than 12 characters long");
            }

            if (nullEmpty(address1)) {
                errors.add("Address line 1 is required");
            }
            else if (address1.length() > 35) {
                errors.add("Address line 1 cannot be more than 35 characters long");
            }

            if (nullEmpty(address2)) {
                errors.add("Address line 2 is required");
            }
            else if (address2.length() > 35) {
                errors.add("Address line 2 cannot be more than 35 characters long");
            }

            if (address3 != null && address3.length() > 35) {
                errors.add("Address line 3 cannot be more than 35 characters long");
            }

            if (address4 != null && address4.length() > 35) {
                errors.add("Address line 4 cannot be more than 35 characters long");
            }

            if (address5 != null && address5.length() > 35) {
                errors.add("Address line 5 cannot be more than 35 characters long");
            }

            if (dateOfBirth == null) {
                errors.add("You must select a date of birth");
            }

            if (nullEmpty(nextOfKinFirstName)) {
                errors.add("You must enter a next of kin first name");
            }
            else if (nextOfKinFirstName.length() > 35) {
                errors.add("Next of Kin First Name cannot be more than 35 characters long");
            }

            if (nullEmpty(nextOfKinSurname)) {
                errors.add("You must enter a next of kin surname");
            }
            else if (nextOfKinFirstName.length() > 35) {
                errors.add("Next of Kin Surname cannot be more than 35 characters long");
            }

            if (nullEmpty(nextOfKinPhoneNumber)) {
                errors.add("You must enter a next of kin phone number");
            }
            else if (nextOfKinFirstName.length() > 12) {
                errors.add("Next of Kin Phone Number cannot be more than 12 characters long");
            }

            if (consultant == null) {
                errors.add("You must select a consultant");
            }

            if (practice == null) {
                errors.add("You must select a practice");
            }

            if (riskCategory == null) {
                errors.add("You must select a risk category");
            }

            if (!errors.isEmpty()) {
                StringJoiner sj = new StringJoiner(System.lineSeparator());
                errors.forEach(sj::add);
                String error = sj.toString();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText(error);

                alert.showAndWait();
                return false;
            }
            else {
                return true;
            }
        }

        private boolean nullEmpty(@Nullable String string) {
            return string == null || string.isEmpty();
        }
    }
}


package org.stevenlowes.university.seassignment.guis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stevenlowes.university.seassignment.dbao.Consultant;
import org.stevenlowes.university.seassignment.dbao.Patient;
import org.stevenlowes.university.seassignment.dbao.Practice;
import org.stevenlowes.university.seassignment.dbao.RiskCategory;
import org.stevenlowes.university.seassignment.guis.utils.FixedLabel;
import org.stevenlowes.university.seassignment.guis.utils.IdSpinner;
import org.stevenlowes.university.seassignment.guis.utils.LimitedTextField;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PatientSearch extends GridPane {

    private static final Border errorBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1)));
    ListView<Patient> listView;
    private ObservableList<Patient> patients;

    public PatientSearch(@NotNull SceneSwitcher switcher, List<Patient> patientList) throws SQLException {
        patients = FXCollections.observableArrayList(patientList);
        patients.sort(Comparator.comparing(Objects::toString));

        try {
            setPadding(new Insets(24));
            setVgap(12);
            setHgap(12);
            setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setMinWidth(300);
            col1.setMaxWidth(300);
            col1.setHalignment(HPos.CENTER);
            getColumnConstraints().add(col1);

            ColumnConstraints col2 = new ColumnConstraints();
            col2.setHalignment(HPos.CENTER);
            getColumnConstraints().add(col2);

            Image backIcon = new Image(getClass().getResourceAsStream("/gui/back.png"));
            Button backButton = new Button("Back");
            backButton.setOnAction(event -> switcher.back());
            backButton.setGraphic(new ImageView(backIcon));

            Text title = new Text("Patient Search");
            title.setFont(Font.font("Tahoma", FontWeight.BOLD, 24.0));

            Pane pane = new Pane();

            Button exportButton = new Button("Export", new ImageView(new Image(getClass().getResourceAsStream("/gui/export.png"))));
            exportButton.prefHeightProperty().bind(backButton.prefHeightProperty());
            exportButton.setOnAction(event -> {
                try {
                    switcher.switchTo(new PatientExport(switcher, patientList));
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error");
                    alert.setContentText("Unable to export from database. Try again later.");
                    alert.showAndWait();
                }
            });

            HBox titleBox = new HBox(12.0, backButton, title, pane, exportButton);
            HBox.setHgrow(pane, Priority.ALWAYS);
            titleBox.setAlignment(Pos.CENTER_LEFT);

            FilteredList<Patient> filteredPatients = new FilteredList<>(patients, p -> true);

            listView = new ListView<>(filteredPatients);
            listView.setPlaceholder(new Label("No Patients Fit the Criteria"));

            MutableRunnable clearRunnable = new MutableRunnable();

            Pair<TitledPane, Runnable> idPair = getIdPane(filteredPatients, clearRunnable);
            Pair<TitledPane, Runnable> patientPair = getInfoPane(filteredPatients, clearRunnable);
            Pair<TitledPane, Runnable> nokPair = getNextOfKinPane(filteredPatients, clearRunnable);

            clearRunnable.setRunnable(() -> {
                idPair.getValue().run();
                patientPair.getValue().run();
                nokPair.getValue().run();
                filteredPatients.setPredicate(p -> true);
            });

            Accordion accordion = new Accordion(idPair.getKey(), patientPair.getKey(), nokPair.getKey());
            accordion.setExpandedPane(idPair.getKey());

            Image createIcon = new Image(getClass().getResourceAsStream("/gui/create.png"));
            Button createButton = new Button("Create Patient");
            createButton.setGraphic(new ImageView(createIcon));
            createButton.setOnAction(event -> {
                try {
                    clearRunnable.run();
                    PatientEditor patientEditor = new PatientEditor(switcher, this, null);
                    switcher.switchTo(patientEditor);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error creating patient");
                    alert.setContentText("Could not create patient. Try again later");
                    alert.showAndWait();
                }
            });

            Runnable viewPatientHandler = () -> {
                Patient patient = listView.getSelectionModel().getSelectedItem();
                if (patient == null) {
                    listView.setBorder(errorBorder);
                }
                else {
                    clearRunnable.run();
                    listView.setBorder(null);
                    try {
                        switcher.switchTo(new PatientView(switcher, this, patient));
                    }
                    catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            };

            Image ViewIcon = new Image(getClass().getResourceAsStream("/gui/view.png"));
            Button viewButton = new Button("View Patient");
            viewButton.setGraphic(new ImageView(ViewIcon));
            viewButton.setOnAction(e -> viewPatientHandler.run());

            listView.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
                    viewPatientHandler.run();
                }
            });

            add(titleBox, 0, 0, 2, 1);
            add(accordion, 0, 2);
            add(listView, 1, 2);
            add(createButton, 0, 3);
            add(viewButton, 1, 3);

            setHgrow(accordion, Priority.NEVER);
            setHgrow(listView, Priority.SOMETIMES);

            setVgrow(accordion, Priority.ALWAYS);
            setVgrow(listView, Priority.ALWAYS);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Pair<TitledPane, Runnable> getIdPane(FilteredList<Patient> filtered, Runnable clearRunnable) {
        GridPane gridPane = new GridPane();
        ScrollPane scrollPane = new ScrollPane(gridPane);
        TitledPane pane = new TitledPane("ID", scrollPane);

        gridPane.setPadding(new Insets(12, 12, 0, 12));
        gridPane.setVgap(4);
        gridPane.setHgap(4);

        Spinner<Integer> spinner = new IdSpinner();
        Label label = new FixedLabel("Patient ID:", spinner);
        gridPane.add(label, 0, 0);
        gridPane.add(spinner, 1, 0);

        Image clearIcon = new Image(getClass().getResourceAsStream("/gui/clear_24.png"));
        Button clearButton = new Button("Clear", new ImageView(clearIcon));
        clearButton.setGraphic(new ImageView(clearIcon));
        clearButton.setOnAction(e -> clearRunnable.run());
        gridPane.add(clearButton, 0, 1);

        Image searchIcon = new Image(getClass().getResourceAsStream("/gui/search.png"));
        Button searchButton = new Button("Search");
        searchButton.setGraphic(new ImageView(searchIcon));
        gridPane.add(searchButton, 1, 1);

        Runnable searchHandler = () -> {
            String stringID = spinner.getEditor().getText();

            Integer id;
            try {
                id = Integer.parseInt(stringID);
            }
            catch (NumberFormatException ex) {
                id = null;
            }

            colorErrors(id == null, label, spinner);

            if (id != null) {
                int finalId = id;
                filtered.setPredicate(p -> p.getId() == finalId);
            }
        };

        searchButton.setOnAction(e -> searchHandler.run());

        spinner.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchHandler.run();
            }
        });

        Runnable clear = () -> {
            spinner.getValueFactory().setValue(null);
            spinner.getEditor().setText("");
            colorErrors(false, label, spinner);
        };

        return new Pair<>(pane, clear);
    }

    private Pair<TitledPane, Runnable> getInfoPane(FilteredList<Patient> filtered, Runnable clearRunnable) throws SQLException {
        GridPane gridPane = new GridPane();
        ScrollPane scrollPane = new ScrollPane(gridPane);
        TitledPane pane = new TitledPane("Patient Info", scrollPane);

        gridPane.setPadding(new Insets(12, 12, 0, 12));
        gridPane.setVgap(4);
        gridPane.setHgap(4);

        int row = 0;
        TextField firstNameField = new LimitedTextField(35);
        firstNameField.setPromptText("enter first name");
        Label firstNameLabel = new FixedLabel("First Name:", firstNameField);
        gridPane.add(firstNameLabel, 0, row);
        gridPane.add(firstNameField, 1, row);

        row++;
        TextField surnameField = new LimitedTextField(35);
        surnameField.setPromptText("enter surname");
        Label surnameLabel = new FixedLabel("Surname:", surnameField);
        gridPane.add(surnameLabel, 0, row);
        gridPane.add(surnameField, 1, row);

        row++;
        TextField phoneField = new LimitedTextField(12);
        phoneField.setPromptText("enter phone number");
        Label phoneLabel = new FixedLabel("Phone:", phoneField);
        gridPane.add(phoneLabel, 0, row);
        gridPane.add(phoneField, 1, row);

        row++;
        TextField addressField = new LimitedTextField(35);
        addressField.setPromptText("enter first line of address");
        Label addressLabel = new FixedLabel("Address Line 1:", addressField);
        gridPane.add(addressLabel, 0, row);
        gridPane.add(addressField, 1, row);

        row++;
        TextField postcodeField = new LimitedTextField(35);
        postcodeField.setPromptText("enter post code");
        Label postcodeLabel = new FixedLabel("Postcode:", postcodeField);
        gridPane.add(postcodeLabel, 0, row);
        gridPane.add(postcodeField, 1, row);

        row++;
        DatePicker dateOfBirthPicker = new DatePicker();
        dateOfBirthPicker.setPromptText("enter date of birth");
        Label dateOfBirthLabel = new FixedLabel("Date of Birth:", dateOfBirthPicker);
        gridPane.add(dateOfBirthLabel, 0, row);
        gridPane.add(dateOfBirthPicker, 1, row);

        row++;
        ObservableList<Practice> practices = FXCollections.observableList(Practice.list());
        practices.add(0, null);
        ComboBox<Practice> practiceComboBox = new ComboBox<>(practices);
        practiceComboBox.prefWidthProperty().bind(firstNameField.widthProperty());
        Label practiceLabel = new FixedLabel("Practice:", practiceComboBox);
        gridPane.add(practiceLabel, 0, row);
        gridPane.add(practiceComboBox, 1, row);

        row++;
        ObservableList<Consultant> consultants = FXCollections.observableList(Consultant.list());
        consultants.add(0, null);
        ComboBox<Consultant> consultantComboBox = new ComboBox<>(consultants);
        consultantComboBox.prefWidthProperty().bind(firstNameField.widthProperty());
        Label consultantLabel = new FixedLabel("Consultant:", consultantComboBox);
        gridPane.add(consultantLabel, 0, row);
        gridPane.add(consultantComboBox, 1, row);

        row++;
        ObservableList<RiskCategory> riskCategories = FXCollections.observableList(RiskCategory.list());
        riskCategories.add(0, null);
        ComboBox<RiskCategory> riskCategoryComboBox = new ComboBox<>(riskCategories);
        riskCategoryComboBox.prefWidthProperty().bind(firstNameField.widthProperty());
        Label riskCategoryLabel = new FixedLabel("Risk Categories:", riskCategoryComboBox);
        gridPane.add(riskCategoryLabel, 0, row);
        gridPane.add(riskCategoryComboBox, 1, row);

        Image clearIcon = new Image(getClass().getResourceAsStream("/gui/clear_24.png"));
        Button clearButton = new Button("Clear", new ImageView(clearIcon));
        clearButton.setGraphic(new ImageView(clearIcon));
        clearButton.setOnAction(e -> clearRunnable.run());
        gridPane.add(clearButton, 0, 9);

        Image searchIcon = new Image(getClass().getResourceAsStream("/gui/search.png"));
        Button searchButton = new Button("Search");
        searchButton.setGraphic(new ImageView(searchIcon));
        gridPane.add(searchButton, 1, 9);

        Runnable searchHandler = () -> {
            String firstName = nullableLowercase(firstNameField.getText());
            String surname = nullableLowercase(surnameField.getText());
            String phoneNumber = nullableStripSpaces(nullableLowercase(phoneField.getText()));
            String address = nullableLowercase(addressField.getText());
            String postCode = nullableStripSpaces(nullableLowercase(postcodeField.getText()));
            LocalDate dateOfBirth = dateOfBirthPicker.getValue();
            Practice practice = practiceComboBox.getValue();
            Consultant consultant = consultantComboBox.getValue();
            RiskCategory riskCategory = riskCategoryComboBox.getValue();

            boolean error = nullEmpty(firstName) && nullEmpty(surname) && nullEmpty(phoneNumber) && nullEmpty(address) && nullEmpty(postCode) && nullEmpty(phoneNumber) && dateOfBirth == null && practice == null && consultant == null && riskCategory == null;

            colorErrors(error, firstNameLabel, firstNameField);
            colorErrors(error, surnameLabel, surnameField);
            colorErrors(error, addressLabel, addressField);
            colorErrors(error, postcodeLabel, postcodeField);
            colorErrors(error, phoneLabel, phoneField);
            colorErrors(error, dateOfBirthLabel, dateOfBirthPicker);
            colorErrors(error, practiceLabel, practiceComboBox);
            colorErrors(error, consultantLabel, consultantComboBox);
            colorErrors(error, riskCategoryLabel, riskCategoryComboBox);

            if (error) {
                gridPane.setVgap(2);
            }
            else {
                gridPane.setVgap(4);
            }

            if (!error) {
                filtered.setPredicate(p -> {
                    if (nullEmpty(firstName) || p.getFirstName().toLowerCase().contains(firstName) || p.getSurname().contains(firstName)) {
                        if (nullEmpty(surname) || p.getFirstName().toLowerCase().contains(surname) || p.getSurname().contains(surname)) {
                            if (nullEmpty(address) || p.getAddress1().toLowerCase().contains(address)) {
                                if (nullEmpty(phoneNumber) || nullableStripSpaces(nullableLowercase(p.getPhoneNumber())).contains(phoneNumber)) {
                                    if (nullEmpty(postCode) || nullableStripSpaces(nullableLowercase(p.getPostcode())).contains(postCode)) {
                                        if (dateOfBirth == null || p.getDateOfBirth().equals(dateOfBirth)) {
                                            if (practice == null || p.getPracticeId() == practice.getId()) {
                                                if (consultant == null || p.getConsultantId() == consultant.getId()) {
                                                    //noinspection RedundantIfStatement
                                                    if (riskCategory == null || p.getRiskCategoryId() == riskCategory.getId()) {
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return false;
                });
            }
        };

        searchButton.setOnAction(e -> searchHandler.run());

        firstNameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchHandler.run();
            }
        });

        surnameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchHandler.run();
            }
        });

        addressField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchHandler.run();
            }
        });

        phoneField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchHandler.run();
            }
        });

        postcodeField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchHandler.run();
            }
        });

        dateOfBirthPicker.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchHandler.run();
            }
        });

        Runnable clear = () -> {
            firstNameField.clear();
            surnameField.clear();
            phoneField.clear();
            addressField.clear();
            postcodeField.clear();
            dateOfBirthPicker.setValue(null);
            practiceComboBox.setValue(null);
            consultantComboBox.setValue(null);
            riskCategoryComboBox.setValue(null);
            colorErrors(false, firstNameLabel, firstNameField);
            colorErrors(false, surnameLabel, surnameField);
            colorErrors(false, phoneLabel, phoneField);
            colorErrors(false, addressLabel, addressField);
            colorErrors(false, postcodeLabel, postcodeField);
            colorErrors(false, dateOfBirthLabel, dateOfBirthPicker);
            colorErrors(false, practiceLabel, practiceComboBox);
            colorErrors(false, consultantLabel, consultantComboBox);
            colorErrors(false, riskCategoryLabel, riskCategoryComboBox);
            gridPane.setVgap(4);
        };

        return new Pair<>(pane, clear);
    }

    private Pair<TitledPane, Runnable> getNextOfKinPane(FilteredList<Patient> filtered, Runnable clearRunnable) {
        GridPane gridPane = new GridPane();
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        TitledPane pane = new TitledPane("Next of Kin Info", scrollPane);

        gridPane.setPadding(new Insets(12, 12, 0, 12));
        gridPane.setVgap(4);
        gridPane.setHgap(4);

        TextField firstNameField = new LimitedTextField(35);
        firstNameField.setPromptText("enter first name");
        Label firstNameLabel = new FixedLabel("First Name:", firstNameField);
        gridPane.add(firstNameLabel, 0, 0);
        gridPane.add(firstNameField, 1, 0);

        TextField surnameField = new LimitedTextField(35);
        surnameField.setPromptText("enter surname");
        Label surnameLabel = new FixedLabel("Surname:", surnameField);
        gridPane.add(surnameLabel, 0, 1);
        gridPane.add(surnameField, 1, 1);

        TextField phoneField = new LimitedTextField(12);
        phoneField.setPromptText("enter phone number");
        Label phoneLabel = new FixedLabel("Phone:", phoneField);
        gridPane.add(phoneLabel, 0, 2);
        gridPane.add(phoneField, 1, 2);

        Image clearIcon = new Image(getClass().getResourceAsStream("/gui/clear_24.png"));
        Button clearButton = new Button("Clear", new ImageView(clearIcon));
        clearButton.setGraphic(new ImageView(clearIcon));
        clearButton.setOnAction(e -> clearRunnable.run());
        gridPane.add(clearButton, 0, 3);

        Image searchIcon = new Image(getClass().getResourceAsStream("/gui/search.png"));
        Button searchButton = new Button("Search");
        searchButton.setGraphic(new ImageView(searchIcon));
        gridPane.add(searchButton, 1, 3);

        Runnable searchHandler = () -> {
            final String firstName = nullableLowercase(firstNameField.getText());
            final String surname = nullableLowercase(surnameField.getText());
            final String phoneNumber = nullableStripSpaces(nullableLowercase(phoneField.getText()));

            boolean error = nullEmpty(firstName) && nullEmpty(surname) && nullEmpty(phoneNumber);
            colorErrors(error, firstNameLabel, firstNameField);
            colorErrors(error, surnameLabel, surnameField);
            colorErrors(error, phoneLabel, phoneField);

            if(error){
                gridPane.setVgap(2);
            }
            else{
                gridPane.setVgap(4);
            }

            if (!error) {
                filtered.setPredicate(p -> {
                    if (nullEmpty(firstName) || p.getNextOfKinFirstName().toLowerCase().contains(firstName) || p.getNextOfKinSurname().toLowerCase().contains(firstName)) {
                        if (nullEmpty(surname) || p.getNextOfKinFirstName().toLowerCase().contains(surname) || p.getNextOfKinSurname().toLowerCase().contains(surname)) {
                            //noinspection RedundantIfStatement
                            if (nullEmpty(phoneNumber) || nullableStripSpaces(nullableLowercase(p.getNextOfKinPhoneNumber())).contains(phoneNumber)) {
                                return true;
                            }
                        }
                    }
                    return false;
                });
            }
        };

        firstNameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchHandler.run();
            }
        });

        surnameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchHandler.run();
            }
        });

        phoneField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchHandler.run();
            }
        });

        searchButton.setOnAction(e -> searchHandler.run());

        Runnable clear = () -> {
            firstNameField.clear();
            surnameField.clear();
            phoneField.clear();
            colorErrors(false, firstNameLabel, firstNameField);
            colorErrors(false, surnameLabel, surnameField);
            colorErrors(false, phoneLabel, phoneField);
            gridPane.setVgap(4);
        };

        return new Pair<>(pane, clear);
    }

    private void colorErrors(boolean errorOccured, @NotNull Labeled labeled, @NotNull Control control) {
        if (errorOccured) {
            control.setBorder(errorBorder);
            labeled.setTextFill(Color.RED);
        }
        else {
            control.setBorder(null);
            labeled.setTextFill(Color.BLACK);
        }
    }

    @Nullable
    private String nullableLowercase(@Nullable String uppercase) {
        if (uppercase != null) {
            return uppercase.toLowerCase();
        }
        return null;
    }

    @Nullable
    private String nullableStripSpaces(@Nullable String withSpaces) {
        if (withSpaces != null) {
            return withSpaces.replaceAll("\\s", "");
        }
        else {
            return null;
        }
    }

    private boolean nullEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    public void addPatient(Patient newPatient) {
        patients.add(newPatient);
        patients.sort(Comparator.comparing(Objects::toString));
    }

    public void removePatient(Patient toRemove) {
        patients.remove(toRemove);
        refreshList();
    }

    public void refreshList() {
        listView.refresh();
    }

    private static class MutableRunnable implements Runnable {
        private Runnable runnable = null;

        @Override
        public void run() {
            if (runnable != null) {
                runnable.run();
            }
        }

        public void setRunnable(Runnable runnable) {
            this.runnable = runnable;
        }


    }
}


package org.stevenlowes.university.seassignment.guis;

import com.opencsv.CSVWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.stevenlowes.university.seassignment.dbao.Consultant;
import org.stevenlowes.university.seassignment.dbao.Patient;
import org.stevenlowes.university.seassignment.dbao.Practice;
import org.stevenlowes.university.seassignment.dbao.RiskCategory;
import org.stevenlowes.university.seassignment.guis.utils.PatientTableColumn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientExport extends GridPane {
    public PatientExport(SceneSwitcher switcher, List<Patient> patientList) throws SQLException {

        setPadding(new Insets(24));
        setVgap(12);
        setHgap(12);
        setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.CENTER);
        col1.setHgrow(Priority.NEVER);
        getColumnConstraints().add(col1);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHalignment(HPos.CENTER);
        col2.setHgrow(Priority.ALWAYS);
        getColumnConstraints().add(col2);

        ObservableList<Patient> obsList = FXCollections.observableList(patientList);

        TableView<Patient> tableView = new TableView<>(obsList);
        tableView.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        tableView.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(4), null)));

        TableColumn<Patient, Long> idColumn = new PatientTableColumn<>("Id", Patient::getId);
        TableColumn<Patient, String> firstnameColumn = new PatientTableColumn<>("First Name", Patient::getFirstName);
        TableColumn<Patient, String> surnameColumn = new PatientTableColumn<>("Surname", Patient::getSurname);
        TableColumn<Patient, LocalDate> dobColumn = new PatientTableColumn<>("Date of Birth", Patient::getDateOfBirth);

        ObservableList<TableColumn<Patient, ?>> columns = tableView.getColumns();
        columns.add(idColumn);
        columns.add(firstnameColumn);
        columns.add(surnameColumn);
        columns.add(dobColumn);

        VBox checkBoxes = new VBox(8);

        Button selectAllButton = new Button("All", new ImageView(new Image(getClass().getResourceAsStream("/gui/tick_16.png"))));
        Button selectNoneButton = new Button("None", new ImageView(new Image(getClass().getResourceAsStream("/gui/cross_16.png"))));
        HBox buttonBox = new HBox(8, selectAllButton, selectNoneButton);
        checkBoxes.getChildren().add(buttonBox);

        List<CheckBox> checkBoxesList = new ArrayList<>();

        CheckBox phoneNumberCheckBox = new CheckBox("Phone Number");
        TableColumn<Patient, String> phoneNumberColumn = new PatientTableColumn<>("Phone Number", Patient::getPhoneNumber);
        phoneNumberCheckBox.setOnAction(event -> {
            if(phoneNumberCheckBox.isSelected()){
                columns.add(phoneNumberColumn);
            }
            else{
                columns.remove(phoneNumberColumn);
            }
        });
        checkBoxes.getChildren().add(phoneNumberCheckBox);
        checkBoxesList.add(phoneNumberCheckBox);

        CheckBox address1CheckBox = new CheckBox("Address (1)");
        TableColumn<Patient, String> address1Column = new PatientTableColumn<>("Address (1)", Patient::getAddress1);
        address1CheckBox.setOnAction(event -> {
            if(address1CheckBox.isSelected()){
                columns.add(address1Column);
            }
            else{
                columns.remove(address1Column);
            }
        });
        checkBoxes.getChildren().add(address1CheckBox);
        checkBoxesList.add(address1CheckBox);

        CheckBox address2CheckBox = new CheckBox("Address (2)");
        TableColumn<Patient, String> address2Column = new PatientTableColumn<>("Address (2)", Patient::getAddress2);
        address2CheckBox.setOnAction(event -> {
            if(address2CheckBox.isSelected()){
                columns.add(address2Column);
            }
            else{
                columns.remove(address2Column);
            }
        });
        checkBoxes.getChildren().add(address2CheckBox);
        checkBoxesList.add(address2CheckBox);

        CheckBox address3CheckBox = new CheckBox("Address (3)");
        TableColumn<Patient, String> address3Column = new PatientTableColumn<>("Address (3)", Patient::getAddress3);
        address3CheckBox.setOnAction(event -> {
            if(address3CheckBox.isSelected()){
                columns.add(address3Column);
            }
            else{
                columns.remove(address3Column);
            }
        });
        checkBoxes.getChildren().add(address3CheckBox);
        checkBoxesList.add(address3CheckBox);

        CheckBox address4CheckBox = new CheckBox("Address (4)");
        TableColumn<Patient, String> address4Column = new PatientTableColumn<>("Address (4)", Patient::getAddress4);
        address4CheckBox.setOnAction(event -> {
            if(address4CheckBox.isSelected()){
                columns.add(address4Column);
            }
            else{
                columns.remove(address4Column);
            }
        });
        checkBoxes.getChildren().add(address4CheckBox);
        checkBoxesList.add(address4CheckBox);

        CheckBox address5CheckBox = new CheckBox("Address (5)");
        TableColumn<Patient, String> address5Column = new PatientTableColumn<>("Address (5)", Patient::getAddress5);
        address5CheckBox.setOnAction(event -> {
            if(address5CheckBox.isSelected()){
                columns.add(address5Column);
            }
            else{
                columns.remove(address5Column);
            }
        });
        checkBoxes.getChildren().add(address5CheckBox);
        checkBoxesList.add(address5CheckBox);

        CheckBox postcodeCheckBox = new CheckBox("Postcode");
        TableColumn<Patient, String> postcodeColumn = new PatientTableColumn<>("Postcode", Patient::getPostcode);
        postcodeCheckBox.setOnAction(event -> {
            if(postcodeCheckBox.isSelected()){
                columns.add(postcodeColumn);
            }
            else{
                columns.remove(postcodeColumn);
            }
        });
        checkBoxes.getChildren().add(postcodeCheckBox);
        checkBoxesList.add(postcodeCheckBox);

        List<Practice> practices = Practice.list();
        CheckBox practiceCheckBox = new CheckBox("Practice");
        TableColumn<Patient, Practice> practiceColumn = new PatientTableColumn<>("Practice", patient -> {
            long id = patient.getPracticeId();
            Optional<Practice> first = practices.stream().filter(practice -> practice.getId() == id).findFirst();
            return first.orElse(null);
        });
        practiceCheckBox.setOnAction(event -> {
            if(practiceCheckBox.isSelected()){
                columns.add(practiceColumn);
            }
            else{
                columns.remove(practiceColumn);
            }
        });
        checkBoxes.getChildren().add(practiceCheckBox);
        checkBoxesList.add(practiceCheckBox);

        List<Consultant> consultants = Consultant.list();
        CheckBox consultantCheckBox = new CheckBox("Consultant");
        TableColumn<Patient, Consultant> consultantColumn = new PatientTableColumn<>("Consultant", patient -> {
            long id = patient.getConsultantId();
            Optional<Consultant> first = consultants.stream().filter(consultant -> consultant.getId() == id).findFirst();
            return first.orElse(null);
        });
        consultantCheckBox.setOnAction(event -> {
            if(consultantCheckBox.isSelected()){
                columns.add(consultantColumn);
            }
            else{
                columns.remove(consultantColumn);
            }
        });
        checkBoxes.getChildren().add(consultantCheckBox);
        checkBoxesList.add(consultantCheckBox);

        List<RiskCategory> riskCategories = RiskCategory.list();
        CheckBox riskCategoryCheckBox = new CheckBox("Risk Category");
        TableColumn<Patient, RiskCategory> riskCategoryColumn = new PatientTableColumn<>("Risk Category", patient -> {
            long id = patient.getRiskCategoryId();
            Optional<RiskCategory> first = riskCategories.stream().filter(riskCategory -> riskCategory.getId() == id).findFirst();
            return first.orElse(null);
        });
        riskCategoryCheckBox.setOnAction(event -> {
            if(riskCategoryCheckBox.isSelected()){
                columns.add(riskCategoryColumn);
            }
            else{
                columns.remove(riskCategoryColumn);
            }
        });
        checkBoxes.getChildren().add(riskCategoryCheckBox);
        checkBoxesList.add(riskCategoryCheckBox);

        selectAllButton.setOnAction(event -> {
            checkBoxesList.forEach(c -> c.setSelected(true));
            checkBoxesList.forEach(CheckBox::fire);
            checkBoxesList.forEach(CheckBox::fire);
        });

        selectNoneButton.setOnAction(event -> {
            checkBoxesList.forEach(c -> c.setSelected(true));
            checkBoxesList.forEach(CheckBox::fire);
        });

        Button saveToCSV = new Button("Save to CSV", new ImageView(new Image(getClass().getResourceAsStream("/gui/export.png"))));
        saveToCSV.setOnAction(event -> {
            try {
                File file = new File("export.csv");
                CSVWriter writer = new CSVWriter(new FileWriter(file));
                List<String> header = new ArrayList<>();
                header.add("ID");
                header.add("First Name");
                header.add("Surname");
                header.add("Date of Birth");
                if (phoneNumberCheckBox.isSelected()) {
                    header.add("Phone Number");
                }
                if (address1CheckBox.isSelected()) {
                    header.add("Address (1)");
                }
                if (address2CheckBox.isSelected()) {
                    header.add("Address (2)");
                }
                if (address3CheckBox.isSelected()) {
                    header.add("Address (3)");
                }
                if (address4CheckBox.isSelected()) {
                    header.add("Address (4)");
                }
                if (address5CheckBox.isSelected()) {
                    header.add("Address (5)");
                }
                if (postcodeCheckBox.isSelected()) {
                    header.add("Postcode");
                }
                if (practiceCheckBox.isSelected()) {
                    header.add("Practice");
                }
                if (consultantCheckBox.isSelected()) {
                    header.add("Consultant");
                }
                if (riskCategoryCheckBox.isSelected()) {
                    header.add("Risk Category");
                }
                writer.writeNext(header.toArray(new String[header.size()]), true);

                patientList.forEach(p -> {
                    List<String> list = new ArrayList<>();
                    list.add(Long.toString(p.getId()));
                    list.add(p.getFirstName());
                    list.add(p.getSurname());
                    list.add(p.getDateOfBirth().toString());
                    if (phoneNumberCheckBox.isSelected()) {
                        list.add(p.getPhoneNumber());
                    }
                    if (address1CheckBox.isSelected()) {
                        list.add(p.getAddress1());
                    }
                    if (address2CheckBox.isSelected()) {
                        list.add(p.getAddress2());
                    }
                    if (address3CheckBox.isSelected()) {
                        list.add(p.getAddress3());
                    }
                    if (address4CheckBox.isSelected()) {
                        list.add(p.getAddress4());
                    }
                    if (address5CheckBox.isSelected()) {
                        list.add(p.getAddress5());
                    }
                    if (postcodeCheckBox.isSelected()) {
                        list.add(p.getPostcode());
                    }
                    if (practiceCheckBox.isSelected()) {
                        long id = p.getPracticeId();
                        Optional<Practice> first = practices.stream().filter(practice -> practice.getId() == id).findFirst();
                        if (first.isPresent()) {
                            list.add(first.get().getName());
                        }
                        else {
                            list.add("ERROR");
                        }
                    }
                    if (consultantCheckBox.isSelected()) {
                        long id = p.getConsultantId();
                        Optional<Consultant> first = consultants.stream().filter(consultant -> consultant.getId() == id).findFirst();
                        if (first.isPresent()) {
                            list.add(first.get().toString());
                        }
                        else {
                            list.add("ERROR");
                        }
                    }
                    if (riskCategoryCheckBox.isSelected()) {
                        long id = p.getRiskCategoryId();
                        Optional<RiskCategory> first = riskCategories.stream().filter(riskCategory -> riskCategory.getId() == id).findFirst();
                        if (first.isPresent()) {
                            list.add(first.get().getName());
                        }
                        else {
                            list.add("ERROR");
                        }
                    }
                    writer.writeNext(list.toArray(new String[list.size()]), true);
                });


                writer.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Export Complete!");
                alert.setContentText("File saved as \""+file.getAbsolutePath()+"\"");
                alert.showAndWait();
            }
            catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("Could not export to CSV. Try again later.");
                alert.showAndWait();
            }
        });
        checkBoxes.getChildren().add(saveToCSV);

        Image backIcon = new Image(getClass().getResourceAsStream("/gui/back.png"));
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> switcher.back());
        backButton.setGraphic(new ImageView(backIcon));

        Text title = new Text("Database Export");
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 24.0));
        HBox titleBox = new HBox(12.0, backButton, title);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        add(titleBox, 0, 0, 2, 1);
        add(checkBoxes, 0, 1);
        add(tableView, 1, 1);

        GridPane.setVgrow(tableView, Priority.ALWAYS);
        GridPane.setHgrow(tableView, Priority.ALWAYS);
    }
}

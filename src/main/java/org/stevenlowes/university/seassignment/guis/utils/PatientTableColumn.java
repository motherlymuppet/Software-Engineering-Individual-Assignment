package org.stevenlowes.university.seassignment.guis.utils;

import javafx.scene.control.TableColumn;
import org.stevenlowes.university.seassignment.dbao.Patient;

import java.util.function.Function;

public class PatientTableColumn<T> extends TableColumn<Patient, T> {
    public PatientTableColumn(String text, Function<Patient, T> function) {
        super(text);
        setCellValueFactory(new CellValueFactory<T>(function));
    }
}

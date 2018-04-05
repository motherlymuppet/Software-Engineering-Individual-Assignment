package org.stevenlowes.university.seassignment.guis.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.stevenlowes.university.seassignment.dbao.Patient;

import java.util.function.Function;

public class CellValueFactory<T> implements Callback<TableColumn.CellDataFeatures<Patient, T>, ObservableValue<T>> {
    private final Function<Patient, T> function;

    public CellValueFactory(Function<Patient, T> function) {
        this.function = function;
    }

    @Override
    public ObservableValue<T> call(TableColumn.CellDataFeatures<Patient, T> param) {
        Patient patient = param.getValue();
        return new ObservableValue<T>() {
            @Override
            public void addListener(ChangeListener<? super T> listener) {

            }

            @Override
            public void removeListener(ChangeListener<? super T> listener) {

            }

            @Override
            public T getValue() {
                return function.apply(patient);
            }

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }
        };
    }
}

package org.stevenlowes.university.seassignment.guis.utils;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

public class IdSpinner extends Spinner<Integer> {
    public IdSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory<Integer>() {
            {
                setValue(null);
            }

            @Override
            public void decrement(int steps) {
                if (getValue() == null) {
                    setValue(1);
                }
                else {
                    setValue(Math.max(1, getValue() - steps));
                }
            }

            @Override
            public void increment(int steps) {
                if (getValue() == null) {
                    setValue(1);
                }
                else {
                    setValue(getValue() + steps);
                }
            }
        };

        StringConverter<Integer> stringConverter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object == null) {
                    return "";
                }
                else {
                    return object.toString();
                }
            }

            @Override
            public Integer fromString(String string) {
                if (string.matches("\\d+")) {
                    try {
                        return Integer.valueOf(string);
                    }
                    catch (NumberFormatException e) {
                        return null;
                    }
                }
                return null;
            }
        };

        setEditable(true);
        setValueFactory(valueFactory);
        valueFactory.setConverter(stringConverter);

        //getEditor().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    }
}

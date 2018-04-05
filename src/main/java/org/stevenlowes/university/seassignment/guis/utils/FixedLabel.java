package org.stevenlowes.university.seassignment.guis.utils;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class FixedLabel extends Label {
    public FixedLabel(String label, Node attachedTo) {
        super(label);
        setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        setLabelFor(attachedTo);
    }
}

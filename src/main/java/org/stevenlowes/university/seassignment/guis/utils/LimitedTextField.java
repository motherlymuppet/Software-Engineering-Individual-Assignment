package org.stevenlowes.university.seassignment.guis.utils;

import javafx.scene.control.TextField;

public class LimitedTextField extends TextField {

    private final int limit;

    public LimitedTextField(int limit) {
        super();
        this.limit = limit;
    }

    public LimitedTextField(String initial, int limit) {
        super(initial);
        this.limit = limit;
    }

    @Override
    public void replaceText(int start, int end, String text) {
        super.replaceText(start, end, text);
        verify();
    }

    @Override
    public void replaceSelection(String text) {
        super.replaceSelection(text);
        verify();
    }

    private void verify() {
        if (getText().length() > limit) {
            int caret = getCaretPosition();
            setText(getText().substring(0, limit));
            positionCaret(caret);
        }
    }
};

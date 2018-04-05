package org.stevenlowes.university.seassignment.guis.utils;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.function.Consumer;

public class DeleteCell<T> extends ListCell<T> {

    private HBox hBox = new HBox();
    private Label label = new Label("(empty)");
    private Pane pane = new Pane();
    private Button button = new Button();

    public DeleteCell(Consumer<Integer> delete) {
        super();
        hBox.getChildren().addAll(label, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        button.setGraphic(new ImageView(new Image(DeleteCell.class.getResourceAsStream("/gui/del_16.png"))));
        button.setOnAction(event -> delete.accept(getIndex()));
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        }
        else {
            label.setText(item != null ? item.toString() : "");
            setGraphic(hBox);
        }
    }
}

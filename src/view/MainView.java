/*
 Copyright (c) UICHUIMI 02/2016

 This file is part of WhiteSuit.

 WhiteSuit is free software: you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 WhiteSuit is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with Foobar.
 If not, see <http://www.gnu.org/licenses/>.
 */

package view;

import core.WTask;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Date created 8/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class MainView extends BorderPane {

    private final ToolsList toolsList = new ToolsList(this);
    private final Button back = new Button("Back", new SizableImage("img/arrow-left.png", SizableImage.MEDIUM));
    private final TabPane progressPane = new TabPane();
    private final SplitPane splitPane = new SplitPane(toolsList);

    public MainView() {
        toolsList.addTools(new AlignerMenuEntry(), new CallerMenuEntry());
        toolsList.setMaxWidth(Double.MAX_VALUE);
        toolsList.getStyleClass().add("tools-list");
        back.setOnAction(event -> setView(toolsList));
        back.setMaxWidth(9999);
        back.setAlignment(Pos.CENTER_LEFT);

        setPadding(new Insets(5));
        splitPane.setOrientation(Orientation.VERTICAL);
        progressPane.getTabs().addListener((ListChangeListener<? super Tab>) c -> {
            if (progressPane.getTabs().isEmpty()) splitPane.getItems().remove(progressPane);
            else if (!splitPane.getItems().contains(progressPane)) splitPane.getItems().add(progressPane);
        });
        setCenter(splitPane);
    }

    public void select(Wtool selectedItem) {
        setView(new VBox(5, back, selectedItem));
    }

    public void executeTask(WTask wTask) {
        final Tab tab = getTab(wTask);
        final TextArea textArea = getTextArea(wTask);
        final Button cancel = getCancelButton(wTask, tab);

        final VBox vBox = new VBox(5, cancel, textArea);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5));
        tab.setContent(vBox);

        addOnSuccessMethod(wTask, tab, cancel);

        progressPane.getTabs().add(tab);
        new Thread(wTask).start();
    }

    private void addOnSuccessMethod(WTask wTask, Tab tab, Button cancel) {
        wTask.terminatedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tab.setClosable(true);
                cancel.setDisable(true);
            }
        });
    }

    private Button getCancelButton(WTask wTask, Tab tab) {
        final Button cancel = new Button("Cancel", new SizableImage("img/stop.png", SizableImage.SMALL));
        cancel.setOnAction(event -> {
            wTask.cancel();
            tab.setClosable(true);
        });
        return cancel;
    }

    private TextArea getTextArea(WTask wTask) {
        final TextArea textArea = new TextArea();
        textArea.setEditable(false);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        final PrintStream printStream = new PrintStream(new TextAreaOutputStream(textArea));
        wTask.setPrintStream(printStream);
        return textArea;
    }

    private Tab getTab(WTask wTask) {
        final Tab tab = new Tab();
        wTask.titleProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> tab.setText(newValue)));
        tab.setClosable(false);
        return tab;
    }

    private class TextAreaOutputStream extends OutputStream {

        private final TextArea textArea;

        private TextAreaOutputStream(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
        }
    }

    private void setView(Node node) {
        splitPane.getItems().set(0, node);
    }

}

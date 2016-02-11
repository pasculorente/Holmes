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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    private final ListView<WToolMenuEntry> toolsList = new ListView<>();
    private final Button back = new Button("Back", new SizableImage("img/arrow-left.png", SizableImage.MEDIUM));
    private final TabPane progressPane = new TabPane();

    public MainView() {
        toolsList.getItems().addAll(new AlignerMenuEntry(), new CallerMenuEntry());
        toolsList.setCellFactory(param -> new MenuEntryCell());
        toolsList.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) select(toolsList.getSelectionModel().getSelectedItem());
        });
        toolsList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) select(toolsList.getSelectionModel().getSelectedItem());
        });
        toolsList.setMaxWidth(Double.MAX_VALUE);

        back.setOnAction(event -> setCenter(toolsList));
        back.setMaxWidth(9999);
        back.setAlignment(Pos.CENTER_LEFT);

        setPadding(new Insets(5));
        setCenter(toolsList);
        setBottom(progressPane);
    }

    private void select(WToolMenuEntry selectedItem) {
        setCenter(new VBox(5, back, selectedItem.getTool()));
    }

    public void executeTask(WTask wTask) {
        final Button cancel = new Button("Cancel", new SizableImage("img/stop.png", SizableImage.SMALL));
        cancel.setOnAction(event -> wTask.cancel());

        final TextArea textArea = new TextArea();
        textArea.setEditable(false);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        final PrintStream printStream = new PrintStream(new TextAreaOutputStream(textArea));
        wTask.setPrintStream(printStream);

        final Tab tab = new Tab();
        wTask.titleProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> tab.setText(newValue)));

        final VBox vBox = new VBox(5, cancel, textArea);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5));
        tab.setContent(vBox);

        progressPane.getTabs().add(tab);
        new Thread(wTask).start();
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

}

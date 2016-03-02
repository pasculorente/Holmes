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
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Date created 8/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class MainView extends BorderPane {

    private final static MainView MAIN_VIEW = new MainView();

    private final ToolsList toolsList = new ToolsList(this);
    private final Button back = new Button("Tools", new SizableImage("img/arrow-left.png", SizableImage.MEDIUM));
    private final Button start = new Button("Start", new SizableImage("img/start.png", SizableImage.MEDIUM));
    private final TabPane progressPane = new TabPane();
    private final SplitPane splitPane = new SplitPane(toolsList);

    private MainView() {
        toolsList.addTools(new AlignerMenuEntry(), new CallerMenuEntry(), new MistMenuEntry(), new AnnotatorMenuEntry());
        toolsList.setMaxWidth(Double.MAX_VALUE);
        toolsList.getStyleClass().add("tools-list");
        back.setOnAction(event -> setView(toolsList));
        back.setMaxWidth(9999);
        back.setAlignment(Pos.CENTER_LEFT);
        back.setCancelButton(true);
        back.setGraphicTextGap(10);
        start.setMaxWidth(9999);
        start.setAlignment(Pos.CENTER_RIGHT);
        start.setDefaultButton(true);
        start.setContentDisplay(ContentDisplay.RIGHT);
        start.setGraphicTextGap(10);
        HBox.setHgrow(back, Priority.ALWAYS);
        HBox.setHgrow(start, Priority.ALWAYS);

        splitPane.setOrientation(Orientation.VERTICAL);
        progressPane.getTabs().addListener((ListChangeListener<? super Tab>) c -> {
            if (progressPane.getTabs().isEmpty()) splitPane.getItems().remove(progressPane);
            else if (!splitPane.getItems().contains(progressPane)) splitPane.getItems().add(progressPane);
        });
        splitPane.setPadding(new Insets(20));
        setCenter(splitPane);
    }

    public static MainView getInstance() {
        return MAIN_VIEW;
    }

    public void select(ToolInterface selectedItem) {
        start.setOnAction(event -> {
            disable(start, 3000);
            selectedItem.start();
        });
        setView(new VBox(5, new HBox(5, back, start), selectedItem));
    }

    private void disable(Button button, long millis) {
        button.setDisable(true);
        final Timer lateButton = new Timer();
        lateButton.schedule(new TimerTask() {
            @Override
            public void run() {
                button.setDisable(false);
            }
        }, millis);
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
        progressPane.getSelectionModel().select(tab);
        new Thread(wTask).start();
    }

    private void addOnSuccessMethod(WTask wTask, Tab tab, Button cancel) {
        wTask.completedProperty().addListener((observable, oldValue, newValue) -> {
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

    private void setView(Node node) {
        splitPane.getItems().set(0, node);
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

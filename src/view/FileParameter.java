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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A TextField that stores a <code>Property&lt File &gt</code>, and provides several ways to select a File from the
 * system.
 * <p>
 * <p>
 * Date created 8/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class FileParameter extends HBox {

    private static File lastPath = null;

    public enum Mode {
        OPEN, SAVE
    }

    private Mode mode;
    private String title;

    private final List<FileChooser.ExtensionFilter> filters = new ArrayList<>();

    private final TextField textField = new TextField();
    private final Property<File> file = new SimpleObjectProperty<>();


    public FileParameter(Mode mode, String title) {
        this.mode = mode;
        this.title = title;
        setSpacing(5);
        setAlignment(Pos.CENTER_LEFT);

        final Button button = new Button(null, new SizableImage("img/folder.png", SizableImage.SMALL));
        button.setOnAction(event -> open());

        getChildren().setAll(textField, button);

        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setEditable(false);
        textField.setOnAction(event -> open());
        textField.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) open();
        });
        textField.setPromptText(title);
        textField.setTooltip(new Tooltip(title));

        file.addListener((observable, oldValue, newValue) -> {
            textField.setText(newValue.getAbsolutePath());
            lastPath = newValue.getParentFile();
        });
    }

    public List<FileChooser.ExtensionFilter> getFilters() {
        return filters;
    }

    private void open() {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().setAll(filters);
        if (file.getValue() != null) chooser.setInitialDirectory(file.getValue().getParentFile());
        else if (lastPath != null) chooser.setInitialDirectory(lastPath);
        chooser.setTitle("Select " + title);
        final File file = mode == Mode.OPEN
                ? chooser.showOpenDialog(WhiteSuit.getPrimaryStage())
                : chooser.showSaveDialog(WhiteSuit.getPrimaryStage());
        if (file != null) this.file.setValue(file);
    }

    public Property<File> fileProperty() {
        return file;
    }

    public void setFile(File file) {
        this.file.setValue(file);
    }

    public File getFile() {
        return file.getValue();
    }
}

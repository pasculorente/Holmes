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

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * This StackPane shows a list that holds files. New files can be added with 'add' button on the BOTTOM_RIGHT corner.
 * User can add multiple Files at once. To delete a file, user can press the 'Delete' button or keyboard 'Del' key when
 * File is selected.
 *
 * @author Lorente Arencibia, Pascual (pasculorente@gmail.com)
 */
public class FileList extends StackPane {

    private final ListView<File> listView = new ListView<>();
    private final Button add = new Button("Add file", new SizableImage("img/add.png", SizableImage.MEDIUM));
    private final Button delete = new Button("Remove", new SizableImage("img/delete.png", SizableImage.MEDIUM));
    private final MenuItem deleteFile = new MenuItem("delete");
    private final ContextMenu contextMenu = new ContextMenu(deleteFile);

    private final List<FileChooser.ExtensionFilter> filters = new ArrayList<>();
    private File lastPath;

    public FileList() {
        allowDragAndDrop();
        initializeContextMenu();
        initializeButtons();
        initializeListView();
        configureButtonPositions();
        getChildren().addAll(listView, add, delete);
    }

    private void allowDragAndDrop() {
        setOnDragEntered(this::dragEntered);
        setOnDragExited(this::dragExited);
        setOnDragOver(this::acceptDrag);
        setOnDragDropped(this::dropFiles);
    }

    private void dragEntered(DragEvent event) {
        listView.getStyleClass().add("drop-entered");
    }

    private void dragExited(DragEvent dragEvent) {
        listView.getStyleClass().remove("drop-entered");
    }

    private void acceptDrag(DragEvent event) {
        if (event.getGestureSource() != this && event.getDragboard().hasString())
            event.acceptTransferModes(TransferMode.LINK);
        event.consume();
    }

    private void dropFiles(DragEvent event) {
        final List<File> files = event.getDragboard().getFiles();
        files.stream().
                filter(file -> !getFiles().contains(file)).
                filter(this::matchesAnyExtension).
                forEach(file -> getFiles().add(file));
    }

    private boolean matchesAnyExtension(File file) {
        return filters.stream().anyMatch(filter -> filter.getExtensions().stream().
                anyMatch(extension -> file.getName().endsWith(extension.replace("*", ""))));
    }

    private void configureButtonPositions() {
        StackPane.setAlignment(add, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(add, new Insets(10));
        StackPane.setAlignment(delete, Pos.BOTTOM_LEFT);
        StackPane.setMargin(delete, new Insets(10));
    }

    private void initializeContextMenu() {
        deleteFile.setGraphic(new SizableImage("img/delete.png", SizableImage.SMALL));
        deleteFile.setOnAction(event -> listView.getItems().remove(listView.getSelectionModel().getSelectedItem()));
        listView.setContextMenu(contextMenu);
    }

    private void initializeButtons() {
        add.setOnAction(e -> addInclude());
        add.getStyleClass().add("graphic-button");
        add.setContentDisplay(ContentDisplay.RIGHT);
        delete.setOnAction(event -> listView.getItems().remove(listView.getSelectionModel().getSelectedItem()));
        delete.getStyleClass().add("graphic-button");
        // Delete button disappears when no file selected
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> delete.setVisible(newValue != null));
        delete.setVisible(false);
    }

    private void initializeListView() {
        listView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.DELETE)
                listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
        });
    }

    private void addInclude() {
        final FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(lastPath);
        chooser.getExtensionFilters().addAll(filters);
//        chooser.setTitle(title);
        final List<File> f = chooser.showOpenMultipleDialog(WhiteSuit.getPrimaryStage());
        if (f != null && !f.isEmpty()) {
            lastPath = f.get(0).getParentFile();
            listView.getItems().addAll(f);
        }
    }

    public ObservableList<File> getFiles() {
        return listView.getItems();
    }

    public List<FileChooser.ExtensionFilter> getFilters() {
        return filters;
    }
}

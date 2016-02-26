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

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Created on 15/02/16.
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 *
 */
public class ToolsList extends VBox {

    private final ObservableList<WToolMenuEntry> entries = FXCollections.observableArrayList();
    private final MainView mainView;


    public ToolsList(MainView mainView) {
        this.mainView = mainView;
        entries.addListener((ListChangeListener<? super WToolMenuEntry>) c -> {
            if (c.next()) {
                getChildren().clear();
                entries.stream().map(this::getButton).forEach(this.getChildren()::add);
            }

        });
        setSpacing(20);
    }

    private Button getButton(WToolMenuEntry entry) {
        final MenuEntry menuEntry = new MenuEntry(entry);
        menuEntry.setOnAction(event -> userSelected(entry));
        return menuEntry;
    }

    private void userSelected(WToolMenuEntry entry) {
        mainView.select(entry.getTool());
    }

    public void addTool(WToolMenuEntry entry) {
        entries.add(entry);
    }

    public void  addTools(WToolMenuEntry... entries) {
        this.entries.addAll(entries);
    }
}

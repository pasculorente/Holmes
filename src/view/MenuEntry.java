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

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Pattern for each cell that represents a MenuEntryTools in a list.
 * <p>
 * Created by uichuimi on 8/02/16.
 */
public class MenuEntry extends Button {

    final Label title = new Label();
    final Label description = new Label();
    final VBox vBox = new VBox(title, description);
    final ImageView imageView = new ImageView();
    final HBox hBox = new HBox(10, imageView, vBox);

    public MenuEntry(WToolMenuEntry item) {
        title.getStyleClass().add("title");
        getStyleClass().add("menu-entry");
        title.setText(item.getName());
        description.setText(item.getDescription());
        imageView.setImage(item.getIcon());
        setText(null);
        setGraphic(hBox);
    }
}

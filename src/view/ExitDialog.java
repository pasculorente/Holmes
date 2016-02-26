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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class ExitDialog {

    private final String text;
    private final String yesText;
    private final String noText;
    private boolean exit = false;

    public ExitDialog(String text, String yesText, String noText) {
        this.text = text;
        this.yesText = yesText;
        this.noText = noText;
    }

    public void show(Stage primaryStage) {
        final Stage closingStage = new Stage();
        final VBox vBox = new VBox(10);
        vBox.getChildren().add(new TextFlow(new Text(text)));
        final Button yes = new Button(yesText);
        final Button no = new Button(noText);
        final HBox hBox = new HBox(10, yes, no);
        hBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(hBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        final Scene scene = new Scene(vBox);
        closingStage.setScene(scene);
        yes.setOnAction(event -> {
            closingStage.close();
            exit = true;
        });
        no.setOnAction(event -> closingStage.close());
        closingStage.initOwner(primaryStage);
        closingStage.initModality(Modality.APPLICATION_MODAL);
        closingStage.showAndWait();
    }

    public boolean isExit() {
        return exit;
    }

}

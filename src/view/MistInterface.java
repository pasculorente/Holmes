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

import core.Mist;
import core.WExtensions;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class MistInterface extends ToolInterface {

    private final static MistInterface MIST_INTERFACE = new MistInterface();

    private final FileParameter input = new FileParameter(FileParameter.Mode.OPEN, "Input BAM");
    //    private final FileParameter ensembl = new FileParameter(FileParameter.Mode.OPEN, "Ensembl exons (GRCh37)");
    private final TextField length = new TextField("1");
    private final TextField threshold = new TextField("10");

    private MistInterface() {
        input.getFilters().add(WExtensions.BAM_FILTER);
//        setBackUp(ensembl, "ensembl.exons");

        length.setOnKeyTyped(this::checkIntegerIntegrity);
        length.setMaxWidth(9999);
        final HBox lengthHBox = new HBox(DEFAULT_SPACING, new Label("Length"), length);
        lengthHBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(length, Priority.ALWAYS);

        threshold.setOnKeyTyped(this::checkIntegerIntegrity);
        threshold.setMaxWidth(9999);
        final HBox thresholdHBox = new HBox(DEFAULT_SPACING, new Label("Threshold"), threshold);
        thresholdHBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(threshold, Priority.ALWAYS);

        final VBox vBox = new VBox(DEFAULT_SPACING, input, lengthHBox, thresholdHBox);
        vBox.setPadding(new Insets(DEFAULT_PADDING));

        final ScrollPane center = new ScrollPane(vBox);
        center.setFitToHeight(true);
        center.setFitToWidth(true);

        setCenter(center);
    }

    public static MistInterface getInstance() {
        return MIST_INTERFACE;
    }

    private void checkIntegerIntegrity(KeyEvent event) {
        if (!Character.isDigit(event.getCharacter().charAt(0))) event.consume();
    }

    @Override
    void start() {
        if (input.getFile() == null) return;
        File output = selectOutput();
        if (output != null) {
            if (!output.getName().endsWith(".mist")) output = new File(output.getParent(), output.getName() + ".mist");
            final Mist mist = new Mist(input.getFile(), output, Integer.valueOf(threshold.getText()), Integer.valueOf(length.getText()));
            WhiteSuit.executeTask(mist);
        }
    }

    private File selectOutput() {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(WExtensions.MIST_FILTER);
        chooser.setTitle("Save MIST file");
        chooser.setInitialFileName(input.getFile().getName().replace(".bam", ".mist"));
        chooser.setInitialDirectory(input.getFile().getParentFile());
        return chooser.showSaveDialog(WhiteSuit.getPrimaryStage());
    }
}

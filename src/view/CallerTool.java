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

import core.Caller;
import core.WExtensions;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Date created 11/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class CallerTool extends Wtool {

    private final FileParameter input = new FileParameter(FileParameter.Mode.OPEN, "Alignments");
    private final FileParameter genome = new FileParameter(FileParameter.Mode.OPEN, "Reference genome");
    private final FileParameter dbSNP = new FileParameter(FileParameter.Mode.OPEN, "dbSNP");

    private final Button start = new Button("Start", new SizableImage("img/call.png", SizableImage.MEDIUM));

    public CallerTool() {
        input.getFilters().add(WExtensions.BAM_FILTER);
        genome.getFilters().add(WExtensions.FASTA_FILTER);
        dbSNP.getFilters().add(WExtensions.VCF_FILTER);

        final VBox vBox = new VBox(5, input, new Separator(Orientation.HORIZONTAL), genome, dbSNP, start);

        start.setOnAction(event -> start());
        start.setMaxWidth(9999);
        start.setAlignment(Pos.CENTER);

        vBox.setPadding(new Insets(5));
        setCenter(vBox);

    }

    private void start() {
        if (input.getFile() == null || genome.getFile() == null || dbSNP == null) return;
        File output = selectOutput();
        if (output != null) {
            if (!output.getName().endsWith(".vcf")) output = new File(output.getParent(), output.getName() + ".vcf");
            final Caller caller = new Caller(input.getFile(), genome.getFile(), dbSNP.getFile(), output);
            WhiteSuit.executeTask(caller);
            disableStartButton(3000);
        }
    }

    private void disableStartButton(long millis) {
        start.setDisable(true);
        final Timer lateButton = new Timer();
        lateButton.schedule(new TimerTask() {
            @Override
            public void run() {
                start.setDisable(false);
            }
        }, millis);
    }

    private File selectOutput() {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(WExtensions.VCF_FILTER);
        chooser.setTitle("Save VCF file");
        chooser.setInitialFileName(input.getFile().getName().replace(".bam", ".vcf"));
        chooser.setInitialDirectory(input.getFile().getParentFile());
        return chooser.showSaveDialog(WhiteSuit.getPrimaryStage());
    }
}

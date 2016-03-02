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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Date created 11/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class CallerInterface extends ToolInterface {

    private final FileList inputList = new FileList();
    private final FileParameter genome = new FileParameter(FileParameter.Mode.OPEN, "Reference genome");
    private final FileParameter dbSNP = new FileParameter(FileParameter.Mode.OPEN, "dbSNP");

    public CallerInterface() {
        inputList.getFilters().add(WExtensions.BAM_FILTER);
        genome.getFilters().add(WExtensions.FASTA_FILTER);
        dbSNP.getFilters().add(WExtensions.VCF_FILTER);
        setBackUp(genome, "reference.genome");
        setBackUp(dbSNP, "dbSNP");

        final VBox vBox = new VBox(DEFAULT_SPACING, inputList, new Separator(Orientation.HORIZONTAL), genome, dbSNP);
        vBox.setPadding(new Insets(DEFAULT_PADDING));
        final ScrollPane center = new ScrollPane(vBox);
        center.setFitToHeight(true);
        center.setFitToWidth(true);

        setCenter(center);
    }

    @Override
    void start() {
        if (genome.getFile() == null || dbSNP.getFile() == null || inputList.getFiles().isEmpty()) return;
        File output = selectOutput();
        if (output != null) {
            if (!output.getName().endsWith(".vcf")) output = new File(output.getParent(), output.getName() + ".vcf");
            final Caller caller = new Caller(inputList.getFiles(), genome.getFile(), dbSNP.getFile(), output);
            WhiteSuit.executeTask(caller);
        }
    }

    private File selectOutput() {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(WExtensions.VCF_FILTER);
        chooser.setTitle("Save VCF file");
        chooser.setInitialFileName(inputList.getFiles().get(0).getName().replace(".bam", ".vcf"));
        chooser.setInitialDirectory(inputList.getFiles().get(0).getParentFile());
        return chooser.showSaveDialog(WhiteSuit.getPrimaryStage());
    }
}

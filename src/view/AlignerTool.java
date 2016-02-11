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

import core.Aligner;
import core.Encoding;
import core.WExtensions;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Date created 8/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class AlignerTool extends Wtool {

    private final FileParameter forward = new FileParameter(FileParameter.Mode.OPEN, "Forward sequences");
    private final FileParameter reverse = new FileParameter(FileParameter.Mode.OPEN, "Reverse sequences");
    private final FileParameter genome = new FileParameter(FileParameter.Mode.OPEN, "Genome (GRCh37)");
    private final FileParameter dbSNP = new FileParameter(FileParameter.Mode.OPEN, "dbSNP");
    private final FileParameter mills = new FileParameter(FileParameter.Mode.OPEN, "Mills");
    private final FileParameter phase1 = new FileParameter(FileParameter.Mode.OPEN, "1000 genomes phase 1 indels");
    private final ComboBox<Encoding> encoding = new ComboBox<>(FXCollections.observableArrayList(Encoding.values()));

    /**
     * Graphical interface to call an Aligner
     **/
    public AlignerTool() {
        forward.getFilters().add(WExtensions.FASTQ_FILTER);
        reverse.getFilters().add(WExtensions.FASTQ_FILTER);
        genome.getFilters().add(WExtensions.FASTA_FILTER);
        dbSNP.getFilters().add(WExtensions.VCF_FILTER);
        mills.getFilters().add(WExtensions.VCF_FILTER);
        phase1.getFilters().add(WExtensions.VCF_FILTER);
        encoding.setPromptText("Encoding");

        final Button start = new Button("Start", new SizableImage("img/align.png", SizableImage.MEDIUM));
        start.setMaxWidth(Double.MAX_VALUE);
        start.setAlignment(Pos.CENTER);
        start.setOnAction(event -> start(removeExtension(forward.getFile().getName())));

        final VBox center = new VBox(5, forward, reverse, new Separator(Orientation.HORIZONTAL), genome, dbSNP, mills, phase1, encoding, start);
        center.setPadding(new Insets(5));
        center.setAlignment(Pos.CENTER);

        final ScrollPane scrollPane = new ScrollPane(center);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        setCenter(scrollPane);
    }

    private String removeExtension(String fileName) {
        int indexOf = fileName.indexOf('.');
        return fileName.substring(0, indexOf);
    }

    private void start(String name) {
        File output = selectOuput(name);
        if (output != null) {
            if (!output.getName().endsWith(".bam")) output = new File(output.getParent(), output.getName() + ".bam");
            final Aligner aligner = new Aligner(forward.getFile(), reverse.getFile(), genome.getFile(), dbSNP.getFile(),
                    mills.getFile(), phase1.getFile(), encoding.getValue(), output);
            WhiteSuit.executeTask(aligner);
        }

    }

    private File selectOuput(String name) {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(WExtensions.BAM_FILTER);
        chooser.setTitle("Save BAM file");
        chooser.setInitialFileName(name + ".bam");
        return chooser.showSaveDialog(WhiteSuit.getPrimaryStage());
    }

}

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * This is the Graphical User Interface to call the Aligner Tool. This class is a singleton, you must call
 * <code>AlignerInterface.getInstance()</code> method.
 * <p>
 * Date created 8/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class AlignerInterface extends ToolInterface {

    private final static AlignerInterface ALIGNER_INTERFACE = new AlignerInterface();
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
    private AlignerInterface() {
        forward.getFilters().add(WExtensions.FASTQ_FILTER);
        reverse.getFilters().add(WExtensions.FASTQ_FILTER);
        genome.getFilters().add(WExtensions.FASTA_FILTER);
        dbSNP.getFilters().add(WExtensions.VCF_FILTER);
        mills.getFilters().add(WExtensions.VCF_FILTER);
        phase1.getFilters().add(WExtensions.VCF_FILTER);
        encoding.setPromptText("Encoding");
        setBackUp(genome, "reference.genome");
        setBackUp(dbSNP, "dbSNP");
        setBackUp(mills, "mills");
        setBackUp(phase1, "phase1");

        final VBox center = new VBox(20, forward, reverse, new Separator(Orientation.HORIZONTAL), genome, dbSNP, mills, phase1, encoding);
        center.setPadding(new Insets(20));
        center.setAlignment(Pos.CENTER);

        final ScrollPane scrollPane = new ScrollPane(center);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        setCenter(scrollPane);
    }

    /**
     * Gets the Graphical User Interface to call the Aligner.
     *
     * @return the AlignerInterface
     */
    public static AlignerInterface getInstance() {
        return ALIGNER_INTERFACE;
    }

    private String removeExtension(String fileName) {
        int indexOf = fileName.indexOf('.');
        return fileName.substring(0, indexOf);
    }

    @Override
    public void start() {
        if (forward.getFile() == null || reverse.getFile() == null) return;
        File output = selectOutput(removeExtension(forward.getFile().getName()));
        if (output != null) {
            output = addExtensionIfNeeded(output, ".bam");
            final Aligner aligner = new Aligner(forward.getFile(), reverse.getFile(), genome.getFile(), dbSNP.getFile(),
                    mills.getFile(), phase1.getFile(), encoding.getValue(), output);
            WhiteSuit.executeTask(aligner);
        }
    }

    private File addExtensionIfNeeded(File file, String extension) {
        return file.getName().endsWith(extension) ? file : new File(file.getParent(), file.getName() + extension);
    }

    private File selectOutput(String name) {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(WExtensions.BAM_FILTER);
        chooser.setTitle("Save BAM file");
        chooser.setInitialFileName(name + ".bam");
        chooser.setInitialDirectory(forward.getFile().getParentFile());
        return chooser.showSaveDialog(WhiteSuit.getPrimaryStage());
    }

}

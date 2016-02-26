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

import core.Annotator;
import core.WExtensions;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class AnnotatorInterface extends ToolInterface {

    private final static AnnotatorInterface ANNOTATOR_INTERFACE = new AnnotatorInterface();

    private final FileParameter input = new FileParameter(FileParameter.Mode.OPEN, "Input VCF");

    private AnnotatorInterface() {
        input.getFilters().add(WExtensions.VCF_FILTER);
        setCenter(new VBox(DEFAULT_SPACING, input));
    }

    public static AnnotatorInterface getInstance() {
        return ANNOTATOR_INTERFACE;
    }

    @Override
    void start() {
        if (input.getFile() == null) return;
        File output = selectOutput();
        if (output != null) {
            if (!output.getName().endsWith(".vcf")) output = new File(output.getParent(), output.getName() + ".vcf");
            final Annotator annotator = new Annotator(input.getFile(), output);
            WhiteSuit.executeTask(annotator);
        }
    }

    private File selectOutput() {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(WExtensions.VCF_FILTER);
        chooser.setTitle("Save VCF file");
        chooser.setInitialFileName(input.getFile().getName().replace(".vcf", ".vep.vcf"));
        chooser.setInitialDirectory(input.getFile().getParentFile());
        return chooser.showSaveDialog(WhiteSuit.getPrimaryStage());
    }
}

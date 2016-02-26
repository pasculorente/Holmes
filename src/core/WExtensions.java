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

package core;

import javafx.stage.FileChooser;

/**
 * Date created 11/02/16
 * <p>
 * Constants with the extensions of files.
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class WExtensions {
    public static final FileChooser.ExtensionFilter FASTQ_FILTER = new FileChooser.ExtensionFilter("FASTQ", "*.fq", "*.fq.gz", "*.fastq", "*.fastq.gz");
    public static final FileChooser.ExtensionFilter FASTA_FILTER = new FileChooser.ExtensionFilter("FASTA", "*fasta", "*.fa", "*.fasta.gz", "*.fa.gz");
    public static final FileChooser.ExtensionFilter VCF_FILTER = new FileChooser.ExtensionFilter("Variant Call Format", "*.vcf", "*.vcf.gz");
    public static final FileChooser.ExtensionFilter BAM_FILTER = new FileChooser.ExtensionFilter("Binary Alingment/Mapping Format", "*.bam");
    public static final FileChooser.ExtensionFilter TXT_FILTER = new FileChooser.ExtensionFilter("Text file", "*.txt");
    /**
     * Filters MIST files (.mist)
     */
    public static final FileChooser.ExtensionFilter MIST_FILTER = new FileChooser.ExtensionFilter("Missing sequences tool format (.mist)", "*.mist");
}

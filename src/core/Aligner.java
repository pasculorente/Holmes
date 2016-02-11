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

import htsjdk.samtools.SAMFileHeader;
import picard.sam.*;
import picard.sam.markduplicates.MarkDuplicates;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

/**
 * Date created 10/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class Aligner extends WTask {

    private final static String[] VOID_ARGS = {};
    private final static File GATK = new File("lib", "GenomeAnalysisTK.jar");
    private final static int TOTAL_STEPS = 10;
    private int currentStep = 1;

    private final File forward;
    private final File reverse;
    private final File genome;
    private final File dbSNP;
    private final File mills;
    private final File phase1;
    private final Encoding encoding;
    private final File output;
    private final int cores;

    public Aligner(File forward, File reverse, File genome, File dbSNP, File mills, File phase1, Encoding encoding, File output) {
        this.forward = forward;
        this.reverse = reverse;
        this.genome = genome;
        this.dbSNP = dbSNP;
        this.mills = mills;
        this.phase1 = phase1;
        this.encoding = encoding;
        this.output = output;
        this.cores = Runtime.getRuntime().availableProcessors();
    }

    @Override
    public void start() {
        setTitle("Aligning " + output.getName());
        println("Input: ");
        println("forward=" + forward);
        println("reverse=" + reverse);
        println("genome=" + genome);
        println("dbsnp=" + dbSNP);
        println("mills=" + mills);
        println("phase1=" + phase1);
        println("output=" + output);
        println("Encoding=" + encoding);
        File align = align();
        if (align == null) {
            println("USer canceled");
            setTitle("Error during alignments");
        }
        setTitle("Aligned correctly");
    }

    private File align() {
        final File raw_alignments = bwaAlign();
        if (raw_alignments == null) return null;
        final File picard_alignments = picard(raw_alignments);
        if (picard_alignments == null) return null;
        final File returnFile = gatk(picard_alignments);
        if (returnFile == null) return null;
        try {
            Files.move(returnFile.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
            buildBamIndex(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnFile;
    }

    private File bwaAlign() {
        setTitle("Aligning forward sequences" + getProgress());
        final File seq1 = alignSequences(forward);
        if (seq1 == null) return null;
        setTitle("Aligning reverse sequences" + getProgress());
        final File seq2 = alignSequences(reverse);
        if (seq2 == null) return null;
        setTitle("Matching alignments" + getProgress());
        return matchPairs(seq1, seq2);
    }

    private String getProgress() {
        return String.format("(%d/%d)", currentStep++, TOTAL_STEPS);
    }

    private File alignSequences(File sequences) {
        try {
            final File tempFile = File.createTempFile("seq1", ".sai");
            final int ret = (encoding == Encoding.PHRED33)
                    ? execute("bwa", "aln", "-t", cores, "-I", genome, sequences, "-f", tempFile)
                    : execute("bwa", "aln", "-t", cores, genome, sequences, "-f", tempFile);
            return ret == 0 ? tempFile : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File matchPairs(File seq1, File seq2) {
        try {
            final File tempFile = File.createTempFile("alignments", ".bam");
            int ret = execute("bwa", "sampe", "-P", "-f", tempFile, genome, seq1, seq2, forward, reverse);
            return ret == 0 ? tempFile : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private File picard(File raw_alignments) {
        try {
            setTitle("Cleaning alignments" + getProgress());
            final File picard1 = cleanSam(raw_alignments);
            if (picard1 == null) return null;
            setTitle("Sorting alignments" + getProgress());
            final File picard2 = sortSam(picard1);
            if (picard2 == null) return null;
            setTitle("Deleting duplicate alignments" + getProgress());
            final File picard3 = markDuplicates(picard2);
            if (picard3 == null) return null;
            setTitle("Repairing headers in alignments" + getProgress());
            final File picard4 = repairHeaders(picard3);
            if (picard4 == null) return null;
            setTitle("Building alignments index" + getProgress());
            final boolean is = buildBamIndex(picard4);
            if (is) return picard4;
            else return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File cleanSam(File input) throws IOException {
        final File tempFile = File.createTempFile("picard1", ".bam");
        final CleanSam cleanSam = new CleanSam();
        cleanSam.INPUT = input;
        cleanSam.OUTPUT = tempFile;
        return cleanSam.instanceMain(VOID_ARGS) == 0 ? tempFile : null;
    }

    private File sortSam(File input) throws IOException {
        final File tempFile = File.createTempFile("picard2", ".bam");
        final SortSam sortSam = new SortSam();
        sortSam.INPUT = input;
        sortSam.SORT_ORDER = SAMFileHeader.SortOrder.coordinate;
        sortSam.OUTPUT = tempFile;
        return sortSam.instanceMain(VOID_ARGS) == 0 ? tempFile : null;
    }

    private File markDuplicates(File input) throws IOException {
        final File tempFile = File.createTempFile("picard3", ".bam");
        final MarkDuplicates markDuplicates = new MarkDuplicates();
        markDuplicates.ASSUME_SORTED = true;
        markDuplicates.INPUT = Collections.singletonList(input.getAbsolutePath());
        markDuplicates.OUTPUT = tempFile;
        markDuplicates.REMOVE_DUPLICATES = true;
        markDuplicates.METRICS_FILE = File.createTempFile("metrics", null);
        return markDuplicates.instanceMain(VOID_ARGS) == 0 ? tempFile : null;
    }

    private File repairHeaders(File input) throws IOException {
        final File tempFile = File.createTempFile("picard4", ".bam");
        final AddOrReplaceReadGroups addOrReplaceReadGroups = new AddOrReplaceReadGroups();
        addOrReplaceReadGroups.INPUT = input.getAbsolutePath();
        addOrReplaceReadGroups.OUTPUT = tempFile;
        addOrReplaceReadGroups.RGPL = "ILLUMINA";
        addOrReplaceReadGroups.RGSM = output.getName().replace(".bam", "");
        addOrReplaceReadGroups.RGPU = "flowcell-barcode.lane";
        addOrReplaceReadGroups.RGLB = "BAITS";
        return addOrReplaceReadGroups.instanceMain(VOID_ARGS) == 0 ? tempFile : null;
    }

    private boolean buildBamIndex(File input) {
        final BuildBamIndex buildBamIndex = new BuildBamIndex();
        buildBamIndex.INPUT = input.getAbsolutePath();
        return buildBamIndex.instanceMain(VOID_ARGS) == 0;
    }

    private File gatk(File alignments) {
        final File dict = new File(genome.getParent(), genome.getName().replace(".fasta", ".fa").replace(".fa", ".dict"));
        if (!dict.exists()) createDictionary();
        final File realignments = realign(alignments);
        return reacalibrate(realignments);
    }

    private void createDictionary() {
        final CreateSequenceDictionary createSequenceDictionary = new CreateSequenceDictionary();
        createSequenceDictionary.REFERENCE = genome;
        createSequenceDictionary.OUTPUT = new File(genome.getParent(), genome.getName().replace(".fasta", ".fa").replace(".fa", ".dict"));
        createSequenceDictionary.instanceMain(VOID_ARGS);
        int ret = execute("samtools", "faidx", genome);
    }

    private File realign(File alignments) {
        setTitle("Realigning alignments" + getProgress());
        final File targets = createTargets(alignments);
        if (targets == null) return null;
        return realign(alignments, targets);
    }

    private File createTargets(File alignments) {
        try {
            final File targets = File.createTempFile("targets", ".intervals");
            int ret = execute("java", "-jar", GATK.getAbsolutePath(),
                    "-T", "RealignerTargetCreator",
                    "-R", genome, "-I", alignments,
                    "-known", mills, "-known", phase1,
                    "-o", targets);
            return ret == 0 ? targets : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File realign(File alignments, File intervals) {
        try {
            final File realignments = File.createTempFile("realignments", ".bam");
            int ret = execute("java", "-jar", "lib/GenomeAnalysisTK.jar",
                    "-T", "IndelRealigner",
                    "-R", genome, "-I", alignments,
                    "-known", mills, "-known", phase1,
                    "-targetIntervals", intervals,
                    "-o", realignments);
            return ret == 0 ? realignments : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File reacalibrate(File alignments) {
        setTitle("Recalibrating alignments" + getProgress());
        final File table = createRecalibratorTable(alignments);
        return recalibrate(alignments, table);
    }

    private File createRecalibratorTable(File alignments) {
        try {
            final File table = File.createTempFile("recal", null);
            int ret = execute("java", "-jar", GATK,
                    "-T", "BaseRecalibrator",
                    "-I", alignments,
                    "-R", genome,
                    "--knownSites", dbSNP,
                    "--knownSites", mills,
                    "--knownSites", phase1,
                    "-o", table);
            if (ret == 0) return table;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File recalibrate(File alignments, File table) {
        try {
            final File realignments = File.createTempFile("realignments", ".bam");
            int ret = execute("java", "-jar", GATK,
                    "-T", "PrintReads",
                    "-R", genome,
                    "-I", alignments,
                    "-BQSR", table,
                    "-o", realignments);
            if (ret == 0) return realignments;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}

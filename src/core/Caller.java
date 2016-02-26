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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date created 11/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class Caller extends WTask {

    private final static File GATK = new File("lib", "GenomeAnalysisTK.jar");

    private final List<File> input;
    private final File genome;
    private final File dbSNP;
    private final File output;

    public Caller(List<File> input, File genome, File dbSNP, File output) {
        this.input = input;
        this.genome = genome;
        this.dbSNP = dbSNP;
        this.output = output;
    }

    @Override
    public void start() {
        setTitle("Calling variants");
        final List<String> command = new ArrayList<>();
        command.addAll(Arrays.asList("java", "-jar", GATK.getAbsolutePath(),
                "-T", "HaplotypeCaller", "-R", genome.getAbsolutePath(),
                "-o", output.getAbsolutePath(), "-D", dbSNP.getAbsolutePath()));
        input.forEach(file -> command.addAll(Arrays.asList("-I", file.getAbsolutePath())));
        execute(command);
    }


}

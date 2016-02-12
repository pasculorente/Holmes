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

/**
 * Date created 11/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class Caller extends WTask {
    private final static File GATK = new File("lib", "GenomeAnalysisTK.jar");

    private final File input;
    private final File genome;
    private final File dbSNP;
    private final File output;

    public Caller(File input, File genome, File dbSNP, File output) {
        this.input = input;
        this.genome = genome;
        this.dbSNP = dbSNP;
        this.output = output;
    }

    @Override
    public void start() {
        setTitle("Calling variants");
        execute("java", "-jar", GATK,
                "-T", "HaplotypeCaller", "-R", genome,
                "-I", input, "-o", output,
                "--dbsnp", dbSNP);
    }


}

/*
 Copyright (c) UICHUIMI 03/2016

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

package core.gatk;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides access to GATK (Genome Analysis ToolKit).
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class GenomeAnalysisTK {

    private final static File USER_DIR = new File(System.getProperty("user.dir"));
    private final static File file = findGATK();

    public static File getGatk() {
        return file;
    }

    private static File findGATK() {
        try {
            final Path path1 = Files.find(USER_DIR.toPath(), 10,
                    (path, basicFileAttributes) -> path.getFileName().toString().matches("GenomeAnalysisTK.jar"),
                    FileVisitOption.FOLLOW_LINKS, FileVisitOption.FOLLOW_LINKS).findFirst().orElse(null);
            if (path1 != null) return path1.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("GATK not found in system");
        return null;
    }
}

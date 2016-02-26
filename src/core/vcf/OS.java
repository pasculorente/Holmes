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

package core.vcf;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class OS {
    private static List<String> standardContigs = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y");

    public static List<String> getStandardContigs() {
        return standardContigs;
    }

    /**
     * Converts an Array to String using tab as separator. Omits the last separator. [value1 value2
     * value3] to value1,value2,value3
     *
     * @param values    a list of values
     * @return the stringified list
     */
    public static String asString(String... values) {
        if (values.length == 0) {
            return "";
        }
        String s = values[0];
        int i = 1;
        while (i < values.length) {
            s += "\t" + values[i++];
        }
        return s;
    }

    /**
     * Converts an Array to String using the separator. Omits the last separator. [value1 value2
     * value3] to value1,value2,value3
     *
     * @param separator something like "\t" or ","
     * @param values    a list of values
     * @return the stringified list
     */
    public static String asString(String separator, List<String> values) {
        if (values.isEmpty()) {
            return "";
        }
        String s = "";
        int i = 0;
        while (i < values.size() - 1) {
            s += values.get(i++) + separator;
        }
        return s + values.get(i);
    }
}

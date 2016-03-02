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

package core.vcf;

/**
 * Factory to create Variants. Use method <code>createVariant(line, file)</code> to get a new Variant. Line should be
 * a String corresponding to a VCF line in a text VCF file.
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class VariantFactory {


    /**
     * Generates a new Variant using line to populate.
     *
     * @param line a VCF line
     * @param file the owner VcfFile
     * @return a variant representing the line in the VCF file
     */
    public static Variant createVariant(String line, VcfFile file) {
        final String[] v = line.split("\t");
        final Variant variant = getBasicVariant(v);
        variant.setVcfFile(file);
        setInfos(variant, v[7]);
        setFormats(variant, v);
        return variant;
    }

    private static Variant getBasicVariant(String[] splitLine) {
        final String chrom = splitLine[0];
        final int pos = Integer.valueOf(splitLine[1]);
        final String id = splitLine[2];
        final String ref = splitLine[3];
        final String alt = splitLine[4];
        final String filter = splitLine[6];
        double qual;
        try {
            qual = Double.valueOf(splitLine[5]);
        } catch (Exception ignored) {
            qual = 0;
        }
        return new Variant(chrom, pos, id, ref, alt, qual, filter);
    }

    private static void setInfos(Variant variant, String infoField) {
        final String[] fields = infoField.split(";");
        for (String field : fields) setInfo(variant, field);
    }

    private static void setInfo(Variant variant, String field) {
        if ((field.contains("="))) {
            final String[] pair = field.split("=");
            variant.setInfo(pair[0], pair[1]);
        } else {
            variant.setInfo(field, true);
        }
    }

    private static void setFormats(Variant variant, String[] v) {
        if (v.length > 8) {
            final String[] keys = v[8].split(":");
            final int numberOfSamples = v.length - 9;
            for (int i = 0; i < numberOfSamples; i++) {
                final int sampleIndex = i + 9;
                String[] values = v[sampleIndex].split(":");
                for (int j = 0; j < values.length; j++) variant.setFormat(i, keys[j], values[j]);
            }
        }
    }

}

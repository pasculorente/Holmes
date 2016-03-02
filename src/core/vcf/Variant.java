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

import java.util.*;

/**
 * Stores a variant. chrom, pos, ref, alt, filter and format are Strings. pos is an integer, qual a
 * double. Info is stored as a map of key==value. If value is null, key is treated as a flag.
 *
 * @author Lorente Arencibia, Pascual (pasculorente@gmail.com)
 */
public class Variant implements Comparable<Variant> {

    private static final List<String> DICTIONARY = new LinkedList<>();

    private final VariantInfo variantInfo = new VariantInfo();

    private VcfFile vcfFile;
    private String chrom;
    private String ref;
    private String alt;
    private String filter;
    private int pos;
    private double qual;
    private String id;
    private Map<Integer, Integer>[] formats;
    private int chromIndex;
    private VariantFormat variantFormat = new VariantFormat();

    Variant(String chrom, int pos, String id, String ref, String alt, double qual, String filter) {
        this.chrom = chrom;
        this.pos = pos;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.qual = qual;
        this.filter = filter;
        chromIndex = OS.getStandardContigs().indexOf(chrom);
    }

    /**
     * Gets the chromosome of the variant.
     *
     * @return the chromosome of the variant
     */
    public String getChrom() {
        return chrom;
    }

    /**
     * Gets the ID of the variant.
     *
     * @return the ID of the variant
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the REF value of the variant.
     *
     * @return the ref value
     */
    public String getRef() {
        return ref;
    }

    /**
     * Gets the ALT value of the variant.
     *
     * @return the alt value
     */
    public String getAlt() {
        return alt;
    }

    /**
     * Gets the position of the variant.
     *
     * @return the position
     */
    public int getPos() {
        return pos;
    }

    /**
     * Gets the QUAL of the variant.
     *
     * @return the quality
     */
    public double getQual() {
        return qual;
    }

    public void setQual(double qual) {
        this.qual = qual;
    }

    @Override
    public int compareTo(Variant variant) {
        // Variants with no standard chromosome goes to the end
        if (chromIndex != -1 && variant.chromIndex == -1) return -1;
        if (chromIndex == -1 && variant.chromIndex != -1) return 1;
        // Non-standard chromosomes are ordered alphabetically
        int compare = (chromIndex == -1)
                ? chrom.compareTo(variant.chrom)
                : Integer.compare(chromIndex, variant.chromIndex);
        if (compare != 0) return compare;
        return Integer.compare(pos, variant.pos);
    }

    public String getFilter() {
        return filter;
    }

    public VcfFile getVcfFile() {
        return vcfFile;
    }

    public void setVcfFile(VcfFile vcfFile) {
        this.vcfFile = vcfFile;
    }

    public Object getInfo(String key) {
        return variantInfo.getInfo(key);
    }

    public String getFormat(int index, String key) {
        return variantFormat.getFormat(index, key);
    }

    @Override
    public String toString() {
        return chrom +
                "\t" + pos +
                "\t" + id +
                "\t" + ref +
                "\t" + alt +
                "\t" + qual +
                "\t" + filter +
                "\t" + getInfoString() +
                getFormatString();
    }

    private String getInfoString() {
        return variantInfo.toString();
    }

    private String getFormatString() {
        return variantFormat.toString();
    }

    /**
     * Sets the given info value.
     *
     * @param key   info key
     * @param value info value
     */
    public void setInfo(String key, Object value) {
        variantInfo.setInfo(key, value);
    }

    public void setFormat(int index, String key, String value) {
        variantFormat.setFormat(index, key, value);
    }
}

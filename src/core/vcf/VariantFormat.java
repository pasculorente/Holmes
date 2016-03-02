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

import java.util.*;

/**
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class VariantFormat {

    private final static List<String> DICTIONARY = new LinkedList<>();

    private Map<Integer, Integer>[] formats;

    public void setFormat(int index, String key, String value) {
        resize(index);
        final Map<Integer, Integer> format = formats[index];
        final int keyIndex = addToDictionary(key);
        final int valueIndex = addToDictionary(value);
        format.put(keyIndex, valueIndex);
    }

    private void resize(int index) {
        if (formats == null) {
            formats = new Map[index + 1];
            formats[index] = new HashMap<>();
        } else if (formats.length <= index) {
            final Map<Integer, Integer>[] newFormats = new Map[index + 1];
            System.arraycopy(formats, 0, newFormats, 0, formats.length);
            formats = newFormats;
            formats[index] = new HashMap<>();
        }
    }

    private int addToDictionary(String value) {
        if (!DICTIONARY.contains(value)) DICTIONARY.add(value);
        return DICTIONARY.indexOf(value);
    }

    public String getFormat(int index, String key) {
        final int keyIndex = DICTIONARY.indexOf(key);
        return DICTIONARY.get(formats[index].get(keyIndex));
    }

    @Override
    public String toString() {
        if (formats == null || formats.length == 0) return "";
        final List<String> keys = new ArrayList<>();
        final List<List<String>> samples = new ArrayList<>();
        for (Map<Integer, Integer> format : formats) {
            if (format == null) samples.add(Collections.emptyList());
            else {
                final List<String> sample = new ArrayList<>();
                for (Map.Entry<Integer, Integer> entry : format.entrySet()) {
                    String key = DICTIONARY.get(entry.getKey());
                    String value = DICTIONARY.get(entry.getValue());
                    if (!keys.contains(key)) keys.add(key);
                    sample.add(value);
                }
                samples.add(sample);
            }
        }
        final StringBuilder builder = new StringBuilder("\t");
        builder.append(OS.asString(":", keys));
        for (List<String> list : samples) builder.append("\t").append(OS.asString(":", list));
        return builder.toString();
//        if (formats.length == 0) return "";
//        final StringBuilder builder = new StringBuilder();
//        final List<String> fId = vcfFile.getHeader().getFormats();
//        fId.sort((key1, key2) -> Integer.compare(vcfFile.getHeader().getFormatIndex(key1), vcfFile.getHeader().getFormatIndex(key2)));
//        final List<Integer> formatKeys = new ArrayList<>();
//        for (String formatKey : fId) formatKeys.add(vcfFile.getHeader().getFormatIndex(formatKey));
//        builder.append("\t").append(OS.asString(":", fId));
//        for (Map<Integer, Integer> format : formats) {
//            final List<String> vals = new ArrayList<>();
//            for (int formatIndex : formatKeys)
//                vals.add(DICTIONARY.get(format.getOrDefault(formatIndex, addToDictionary("."))));
//            builder.append("\t").append(OS.asString(":", vals));
//        }
//        return builder.toString();
    }
}

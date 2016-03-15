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
public class VariantInfo {

    private final static List<String> DICTIONARY = new ArrayList<>();


    private final Map<Integer, Object> values = new HashMap<>();

    private final Map<Integer, Integer> stringValues = new HashMap<>();

    public void setInfo(String key, Object value) {
        final int keyIndex = addToDictionary(key);
        if (value.getClass().equals(String.class)) {
            final int valueIndex = addToDictionary((String) value);
            stringValues.put(keyIndex, valueIndex);
        } else values.put(keyIndex, value);
    }

    public Object getInfo(String key) {
        final int keyIndex = DICTIONARY.indexOf(key);
        if (keyIndex < 0) return null;
        if (values.containsKey(keyIndex)) return values.get(keyIndex);
        if (stringValues.containsKey(keyIndex)) return DICTIONARY.get(stringValues.get(keyIndex));
        return null;
    }

    private int addToDictionary(String value) {
        if (!DICTIONARY.contains(value)) DICTIONARY.add(value);
        return DICTIONARY.indexOf(value);
    }

    public String getString(String key) {
        return (String) getInfo(key);
    }

    public Number getNumber(String key) {
        return (Number) getInfo(key);
    }

    public Boolean getBoolean(String key) {
        return (boolean) getInfo(key);
    }

    @Override
    public String toString() {
        final List<String> infos = new ArrayList<>();
        values.forEach((keyIndex, value) -> {
            final String key = DICTIONARY.get(keyIndex);
            if (value.getClass().equals(Boolean.class)) infos.add(key);
            else infos.add(key + "=" + value.toString());
        });
        stringValues.forEach((keyIndex, valueIndex) -> {
            final String key = DICTIONARY.get(keyIndex);
            final String value = DICTIONARY.get(valueIndex);
            infos.add(key + "=" + value);
        });
        Collections.sort(infos);
        return OS.asString(";", infos);

    }
}

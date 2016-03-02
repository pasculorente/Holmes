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

    private final static List<Object> DICTIONARY = new ArrayList<>();

    private final Map<Integer, Integer> properties = new HashMap<>();


    public void setInfo(String key, Object value) {
        final int keyIndex = addToDictionary(key);
        final int valueIndex = addToDictionary(value);
        properties.put(keyIndex, valueIndex);
    }

    public Object getInfo(String key) {
        final int keyIndex = DICTIONARY.indexOf(key);
        if (keyIndex < 0) return null;
        final int valueIndex = properties.getOrDefault(keyIndex, -1);
        if (valueIndex < 0) return null;
        return DICTIONARY.get(valueIndex);
    }

    private int addToDictionary(Object value) {
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
        properties.forEach((keyIndex, valueIndex) -> {
            final String key = (String) DICTIONARY.get(keyIndex);
            final Object value = DICTIONARY.get(valueIndex);
            if (value != null) {
                if (value.getClass().equals(Boolean.class)) infos.add(key);
                else infos.add(key + "=" + value.toString());
            }
        });
        Collections.sort(infos);
        return OS.asString(";", infos);

    }
}

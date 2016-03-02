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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Thought to store a large amount of Strings. Each String is given an int index.
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class Dictionary {

    private final LinkedList<String> dictionary = new LinkedList<>();

    public int add(String value) {
        if (!dictionary.contains(value)) dictionary.add(value);
        return dictionary.indexOf(value);
    }

    public int indexOf(String value) {
        return dictionary.indexOf(value);
    }

    public String get(int index) {
        return dictionary.get(index);
    }

    public List<String> getValues() {
        return new ArrayList<>(dictionary);
    }
}

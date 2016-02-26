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
 * A dictionary is a container of Strings that ensures that each String is stored only once. Each String is given an
 * Integer key, so you can get the word anytime using the key.
 *
 * @author Lorente Arencibia, Pascual (pasculorente@gmail.com)
 */
public class Dictionary {

    private final List<String> dictionary = new LinkedList<>();

    public int add(String word) {
        if (dictionary.contains(word)) return dictionary.indexOf(word);
        dictionary.add(word);
        return dictionary.size() -1;
    }

    /**
     * Tells if this dictionary contains the given word.
     *
     * @param word the word to query
     * @return true if the dictionary contains the word, false otherwise
     */
    public boolean has(String word) {
        return dictionary.contains(word);
    }

    /**
     * Returns the index of the given word.
     *
     * @param word the word to query
     * @return the index of the word in the dictionary. -1 if the word is not in the dictionary
     */
    public int indexOf(String word) {
        return dictionary.indexOf(word);
    }

    /**
     * Get the word at the specific index
     * @param index index of the word
     * @return the word at the index
     */
    public String get(int index) {
        return dictionary.get(index);
    }

    public List<String> getWordList() {
        return new ArrayList<>(dictionary);
    }
}

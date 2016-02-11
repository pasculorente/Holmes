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

package view;

import javafx.scene.image.Image;

/**
 * Date created 8/02/16
 *
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class AlignerMenuEntry implements WToolMenuEntry {

    final AlignerTool alignerTool = new AlignerTool();

    @Override
    public String getName() {
        return "Aligner";
    }

    @Override
    public String getDescription() {
        return "Align paired-end sequences";
    }

    @Override
    public Image getIcon() {
        return new Image("img/align.png");
    }

    @Override
    public Wtool getTool() {
        return alignerTool;
    }
}

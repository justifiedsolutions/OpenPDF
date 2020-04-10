/*
 * Copyright 2002 by Phillip Pan
 * 
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * https://github.com/LibrePDF/OpenPDF
 */
package com.justifiedsolutions.openpdf.text.pdf;

import com.justifiedsolutions.openpdf.text.Rectangle;
import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import java.awt.Color;

/**
 * Implements the pattern.
 */

public final class PdfPatternPainter extends PdfTemplate {
    
    float xstep, ystep;
    boolean stencil = false;
    Color defaultColor;
    
    /**
     *Creates a <CODE>PdfPattern</CODE>.
     */
    
    private PdfPatternPainter() {
        super();
        type = TYPE_PATTERN;
    }

    /**
     * Returns the horizontal interval when repeating the pattern.
     * @return a value
     */
    public float getXStep() {
        return this.xstep;
    }
    
    /**
     * Returns the vertical interval when repeating the pattern.
     * @return a value
     */
    public float getYStep() {
        return this.ystep;
    }
    
    /**
     * Tells you if this pattern is colored/uncolored (stencil = uncolored, you need to set a default color).
     * @return true if the pattern is an uncolored tiling pattern (stencil).
     */
    public boolean isStencil() {
        return stencil;
    }

    /**
     * Gets the stream representing this pattern
     * @param    compressionLevel    the compression level of the stream
     * @return the stream representing this pattern
     * @since    2.1.3
     */
    PdfPattern getPattern(int compressionLevel) {
        return new PdfPattern(this, compressionLevel);
    }
    
    /**
     * Gets a duplicate of this <CODE>PdfPatternPainter</CODE>. All
     * the members are copied by reference but the buffer stays different.
     * @return a copy of this <CODE>PdfPatternPainter</CODE>
     */
    
    public PdfContentByte getDuplicate() {
        PdfPatternPainter tpl = new PdfPatternPainter();
        tpl.writer = writer;
        tpl.pdf = pdf;
        tpl.thisReference = thisReference;
        tpl.pageResources = pageResources;
        tpl.bBox = new Rectangle(bBox);
        tpl.xstep = xstep;
        tpl.ystep = ystep;
        tpl.matrix = matrix;
        tpl.stencil = stencil;
        tpl.defaultColor = defaultColor;
        return tpl;
    }
    
    /**
     * Returns the default color of the pattern.
     * @return a Color
     */
    public Color getDefaultColor() {
        return defaultColor;
    }
    
    /**
     * @see PdfContentByte#setGrayFill(float)
     */
    public void setGrayFill(float gray) {
        checkNoColor();
        super.setGrayFill(gray);
    }
    
    /**
     * @see PdfContentByte#resetGrayFill()
     */
    public void resetGrayFill() {
        checkNoColor();
        super.resetGrayFill();
    }

    /**
     * @see PdfContentByte#resetGrayStroke()
     */
    public void resetGrayStroke() {
        checkNoColor();
        super.resetGrayStroke();
    }
    
    /**
     * @see PdfContentByte#setRGBColorFillF(float, float, float)
     */
    public void setRGBColorFillF(float red, float green, float blue) {
        checkNoColor();
        super.setRGBColorFillF(red, green, blue);
    }
    
    /**
     * @see PdfContentByte#resetRGBColorFill()
     */
    public void resetRGBColorFill() {
        checkNoColor();
        super.resetRGBColorFill();
    }
    
    /**
     * @see PdfContentByte#setRGBColorStrokeF(float, float, float)
     */
    public void setRGBColorStrokeF(float red, float green, float blue) {
        checkNoColor();
        super.setRGBColorStrokeF(red, green, blue);
    }
    
    /**
     * @see PdfContentByte#resetRGBColorStroke()
     */
    public void resetRGBColorStroke() {
        checkNoColor();
        super.resetRGBColorStroke();
    }

    /**
     * @see PdfContentByte#setRGBColorFill(int, int, int)
     */
    public void setRGBColorFill(int red, int green, int blue) {
        checkNoColor();
        super.setRGBColorFill(red, green, blue);
    }
    
    /**
     * @see PdfContentByte#setRGBColorStroke(int, int, int)
     */
    public void setRGBColorStroke(int red, int green, int blue) {
        checkNoColor();
        super.setRGBColorStroke(red, green, blue);
    }
    
    /**
     * @see PdfContentByte#setColorStroke(java.awt.Color)
     */
    public void setColorStroke(Color color) {
        checkNoColor();
        super.setColorStroke(color);
    }
    
    /**
     * @see PdfContentByte#setColorFill(java.awt.Color)
     */
    public void setColorFill(Color color) {
        checkNoColor();
        super.setColorFill(color);
    }
    
    /**
     * @see PdfContentByte#setColorFill(PdfSpotColor, float)
     */
    public void setColorFill(PdfSpotColor sp, float tint) {
        checkNoColor();
        super.setColorFill(sp, tint);
    }
    
    /**
     * @see PdfContentByte#setColorStroke(PdfSpotColor, float)
     */
    public void setColorStroke(PdfSpotColor sp, float tint) {
        checkNoColor();
        super.setColorStroke(sp, tint);
    }
    
    /**
     * @see PdfContentByte#setPatternFill(PdfPatternPainter)
     */
    public void setPatternFill(PdfPatternPainter p) {
        checkNoColor();
        super.setPatternFill(p);
    }
    
    /**
     * @see PdfContentByte#setPatternFill(PdfPatternPainter, java.awt.Color, float)
     */
    public void setPatternFill(PdfPatternPainter p, Color color, float tint) {
        checkNoColor();
        super.setPatternFill(p, color, tint);
    }
    
    /**
     * @see PdfContentByte#setPatternStroke(PdfPatternPainter, java.awt.Color, float)
     */
    public void setPatternStroke(PdfPatternPainter p, Color color, float tint) {
        checkNoColor();
        super.setPatternStroke(p, color, tint);
    }
    
    /**
     * @see PdfContentByte#setPatternStroke(PdfPatternPainter)
     */
    public void setPatternStroke(PdfPatternPainter p) {
        checkNoColor();
        super.setPatternStroke(p);
    }
    
    void checkNoColor() {
        if (stencil)
            throw new RuntimeException(MessageLocalization.getComposedMessage("colors.are.not.allowed.in.uncolored.tile.patterns"));
    }
}

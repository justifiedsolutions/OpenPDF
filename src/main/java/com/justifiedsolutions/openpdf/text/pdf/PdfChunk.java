/*
 * $Id: PdfChunk.java 4092 2009-11-11 17:58:16Z psoares33 $
 *
 * Copyright 1999, 2000, 2001, 2002 Bruno Lowagie
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

import com.justifiedsolutions.openpdf.text.Chunk;
import com.justifiedsolutions.openpdf.text.Font;
import com.justifiedsolutions.openpdf.text.Utilities;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * A <CODE>PdfChunk</CODE> is the PDF translation of a <CODE>Chunk</CODE>.
 * <P>
 * A <CODE>PdfChunk</CODE> is a <CODE>PdfString</CODE> in a certain
 * <CODE>PdfFont</CODE> and <CODE>Color</CODE>.
 */

class PdfChunk {

    private static final char[] singleSpace = {' '};
    private static final PdfChunk[] thisChunk = new PdfChunk[1];
    private static final float ITALIC_ANGLE = 0.21256f;
/** The allowed attributes in variable <CODE>attributes</CODE>. */
    private static final Map<String, Object> keysAttributes = new HashMap<>();
    
/** The allowed attributes in variable <CODE>noStroke</CODE>. */
    private static final Map<String, Object> keysNoStroke = new HashMap<>();
    
    static {
        keysAttributes.put(Chunk.UNDERLINE, null);
        keysAttributes.put(Chunk.GENERICTAG, null);
        keysAttributes.put(Chunk.NEWPAGE, null);
        keysAttributes.put(Chunk.BACKGROUND, null);
        keysAttributes.put(Chunk.SKEW, null);
        keysAttributes.put(Chunk.HSCALE, null);
        keysAttributes.put(Chunk.SEPARATOR, null);
        keysAttributes.put(Chunk.TAB, null);
        keysAttributes.put(Chunk.CHAR_SPACING, null);
        keysNoStroke.put(Chunk.SUBSUPSCRIPT, null);
        keysNoStroke.put(Chunk.SPLITCHARACTER, null);
        keysNoStroke.put(Chunk.HYPHENATION, null);
        keysNoStroke.put(Chunk.TEXTRENDERMODE, null);
    }
    
    // membervariables

    /** The value of this object. */
    protected String value;
    
    /** The encoding. */
    protected String encoding;
    
    
/** The font for this <CODE>PdfChunk</CODE>. */
    protected PdfFont font;
    
    protected BaseFont baseFont;
    
    protected SplitCharacter splitCharacter;
/**
 * Metric attributes.
 * <P>
 * This attributes require the measurement of characters widths when rendering
 * such as underline.
 */
    protected Map<String, Object> attributes = new HashMap<>();
    
/**
 * Non metric attributes.
 * <P>
 * This attributes do not require the measurement of characters widths when rendering
 * such as Color.
 */
    protected Map<String, Object> noStroke = new HashMap<>();
    
/** <CODE>true</CODE> if the chunk split was cause by a newline. */
    protected boolean newlineSplit;

    // constructors
    
/**
 * Constructs a <CODE>PdfChunk</CODE>-object.
 *
 * @param string the content of the <CODE>PdfChunk</CODE>-object
 * @param other Chunk with the same style you want for the new Chunk
 */
    
    PdfChunk(String string, PdfChunk other) {
        thisChunk[0] = this;
        value = string;
        this.font = other.font;
        this.attributes = other.attributes;
        this.noStroke = other.noStroke;
        this.baseFont = other.baseFont;
        encoding = font.getFont().getEncoding();
        splitCharacter = (SplitCharacter)noStroke.get(Chunk.SPLITCHARACTER);
        if (splitCharacter == null)
            splitCharacter = DefaultSplitCharacter.DEFAULT;
    }
    
/**
 * Constructs a <CODE>PdfChunk</CODE>-object.
 *  @param chunk the original <CODE>Chunk</CODE>-object
 *
 */
    
    PdfChunk(Chunk chunk) {
        thisChunk[0] = this;
        value = chunk.getContent();
        
        Font f = chunk.getFont();
        float size = f.getSize();
        if (size == Font.UNDEFINED)
            size = 12;
        baseFont = f.getBaseFont();
        int style = f.getStyle();
        if (style == Font.UNDEFINED) {
            style = Font.NORMAL;
        }
        if (baseFont == null) {
            // translation of the font-family to a PDF font-family
            baseFont = f.getCalculatedBaseFont(false);
        }
        else {
            // bold simulation
            if ((style & Font.BOLD) != 0)
                attributes.put(Chunk.TEXTRENDERMODE, new Object[]{PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE, size / 30f, null});
            // italic simulation
            if ((style & Font.ITALIC) != 0)
                attributes.put(Chunk.SKEW, new float[]{0, ITALIC_ANGLE});
        }
        font = new PdfFont(baseFont, size);
        // other style possibilities
        Map<String, Object> attr = chunk.getChunkAttributes();
        if (attr != null) {
            for (Map.Entry<String, Object> entry : attr.entrySet()) {
                String name = entry.getKey();
                if (keysAttributes.containsKey(name)) {
                    attributes.put(name, entry.getValue());
                } else if (keysNoStroke.containsKey(name)) {
                    noStroke.put(name, entry.getValue());
                }
            }
            if ("".equals(attr.get(Chunk.GENERICTAG))) {
                attributes.put(Chunk.GENERICTAG, chunk.getContent());
            }
        }
        if (f.isUnderlined()) {
            Object[] obj = {null, new float[]{0, 1f / 15, 0, -1f / 3, 0}};
            Object[][] unders = Utilities.addToArray((Object[][]) attributes.get(Chunk.UNDERLINE), obj);
            attributes.put(Chunk.UNDERLINE, unders);
        }
        if (f.isStrikethru()) {
            Object[] obj = {null, new float[]{0, 1f / 15, 0, 1f / 3, 0}};
            Object[][] unders = Utilities.addToArray((Object[][]) attributes.get(Chunk.UNDERLINE), obj);
            attributes.put(Chunk.UNDERLINE, unders);
        }
        // the color can't be stored in a PdfFont
        noStroke.put(Chunk.COLOR, f.getColor());
        noStroke.put(Chunk.ENCODING, font.getFont().getEncoding());
        Float hs = (Float)attributes.get(Chunk.HSCALE);
        if (hs != null)
            font.setHorizontalScaling(hs);
        encoding = font.getFont().getEncoding();
        splitCharacter = (SplitCharacter)noStroke.get(Chunk.SPLITCHARACTER);
        if (splitCharacter == null)
            splitCharacter = DefaultSplitCharacter.DEFAULT;
    }
    
    // methods
    
    /** Gets the Unicode equivalent to a CID.
     * The (inexistent) CID <FF00> is translated as '\n'. 
     * It has only meaning with CJK fonts with Identity encoding.
     * @param c the CID code
     * @return the Unicode equivalent
     */    
    int getUnicodeEquivalent(int c) {
        return baseFont.getUnicodeEquivalent(c);
    }

    protected int getWord(String text, int start) {
        int len = text.length();
        while (start < len) {
            if (!Character.isLetter(text.charAt(start)))
                break;
            ++start;
        }
        return start;
    }
    
/**
 * Splits this <CODE>PdfChunk</CODE> if it's too long for the given width.
 * <P>
 * Returns <VAR>null</VAR> if the <CODE>PdfChunk</CODE> wasn't truncated.
 *
 * @param        width        a given width
 * @return        the <CODE>PdfChunk</CODE> that doesn't fit into the width.
 */
    
    PdfChunk split(float width) {
        newlineSplit = false;
        HyphenationEvent hyphenationEvent = (HyphenationEvent)noStroke.get(Chunk.HYPHENATION);
        int currentPosition = 0;
        int splitPosition = -1;
        float currentWidth = 0;
        
        // loop over all the characters of a string
        // or until the totalWidth is reached
        int lastSpace = -1;
        float lastSpaceWidth = 0;
        int length = value.length();
        char[] valueArray = value.toCharArray();
        char character;
        BaseFont ft = font.getFont();
        boolean surrogate;
        if (ft.getFontType() == BaseFont.FONT_TYPE_CJK && ft.getUnicodeEquivalent(' ') != ' ') {
            while (currentPosition < length) {
                // the width of every character is added to the currentWidth
                char cidChar = valueArray[currentPosition];
                character = (char)ft.getUnicodeEquivalent(cidChar);
                // if a newLine or carriageReturn is encountered
                if (character == '\n') {
                    newlineSplit = true;
                    String returnValue = value.substring(currentPosition + 1);
                    value = value.substring(0, currentPosition);
                    if (value.length() < 1) {
                        value = "\u0001";
                    }
                    return new PdfChunk(returnValue, this);
                }
                currentWidth += getCharWidth(cidChar);
                if (character == ' ') {
                    lastSpace = currentPosition + 1;
                    lastSpaceWidth = currentWidth;
                }
                if (currentWidth > width)
                    break;
                // if a split-character is encountered, the splitPosition is altered
                if (splitCharacter.isSplitCharacter(currentPosition, valueArray, thisChunk))
                    splitPosition = currentPosition + 1;
                currentPosition++;
            }
        }
        else {
            while (currentPosition < length) {
                // the width of every character is added to the currentWidth
                character = valueArray[currentPosition];
                // if a newLine or carriageReturn is encountered
                if (character == '\r' || character == '\n') {
                    newlineSplit = true;
                    int inc = 1;
                    if (character == '\r' && currentPosition + 1 < length && valueArray[currentPosition + 1] == '\n')
                        inc = 2;
                    String returnValue = value.substring(currentPosition + inc);
                    value = value.substring(0, currentPosition);
                    if (value.length() < 1) {
                        value = " ";
                    }
                    return new PdfChunk(returnValue, this);
                }
                surrogate = Utilities.isSurrogatePair(valueArray, currentPosition);
                if (surrogate)
                    currentWidth += getCharWidth(Utilities.convertToUtf32(valueArray[currentPosition], valueArray[currentPosition + 1]));
                else
                    currentWidth += getCharWidth(character);
                if (character == ' ') {
                    lastSpace = currentPosition + 1;
                    lastSpaceWidth = currentWidth;
                }
                if (surrogate)
                    currentPosition++;
                if (currentWidth > width)
                    break;
                // if a split-character is encountered, the splitPosition is altered
                if (splitCharacter.isSplitCharacter(currentPosition, valueArray, null))
                    splitPosition = currentPosition + 1;
                currentPosition++;
            }
        }
        
        // if all the characters fit in the total width, null is returned (there is no overflow)
        if (currentPosition == length) {
            return null;
        }
        // otherwise, the string has to be truncated
        if (splitPosition < 0) {
            String returnValue = value;
            value = "";
            return new PdfChunk(returnValue, this);
        }
        if (lastSpace > splitPosition && splitCharacter.isSplitCharacter(0, singleSpace, null))
            splitPosition = lastSpace;
        if (hyphenationEvent != null && lastSpace >= 0 && lastSpace < currentPosition) {
            int wordIdx = getWord(value, lastSpace);
            if (wordIdx > lastSpace) {
                String pre = hyphenationEvent.getHyphenatedWordPre(value.substring(lastSpace, wordIdx), font.getFont(), font.size(), width - lastSpaceWidth);
                String post = hyphenationEvent.getHyphenatedWordPost();
                if (pre.length() > 0) {
                    String returnValue = post + value.substring(wordIdx);
                    value = trim(value.substring(0, lastSpace) + pre);
                    return new PdfChunk(returnValue, this);
                }
            }
        }
        String returnValue = value.substring(splitPosition);
        value = trim(value.substring(0, splitPosition));
        return new PdfChunk(returnValue, this);
    }
    
/**
 * Truncates this <CODE>PdfChunk</CODE> if it's too long for the given width.
 * <P>
 * Returns <VAR>null</VAR> if the <CODE>PdfChunk</CODE> wasn't truncated.
 *
 * @param        width        a given width
 * @return        the <CODE>PdfChunk</CODE> that doesn't fit into the width.
 */
    
    PdfChunk truncate(float width) {
        int currentPosition = 0;
        float currentWidth = 0;
        
        // it's no use trying to split if there isn't even enough place for a space
        if (width < font.width()) {
            String returnValue = value.substring(1);
            value = value.substring(0, 1);
            return new PdfChunk(returnValue, this);
        }
        
        // loop over all the characters of a string
        // or until the totalWidth is reached
        int length = value.length();
        boolean surrogate = false;
        while (currentPosition < length) {
            // the width of every character is added to the currentWidth
            surrogate = Utilities.isSurrogatePair(value, currentPosition);
            if (surrogate)
                currentWidth += getCharWidth(Utilities.convertToUtf32(value, currentPosition));
            else
                currentWidth += getCharWidth(value.charAt(currentPosition));
            if (currentWidth > width)
                break;
            if (surrogate)
                currentPosition++;
            currentPosition++;
        }
        
        // if all the characters fit in the total width, null is returned (there is no overflow)
        if (currentPosition == length) {
            return null;
        }
        
        // otherwise, the string has to be truncated
        //currentPosition -= 2;
        // we have to chop off minimum 1 character from the chunk
        if (currentPosition == 0) {
            currentPosition = 1;
            if (surrogate)
                ++currentPosition;
        }
        String returnValue = value.substring(currentPosition);
        value = value.substring(0, currentPosition);
        return new PdfChunk(returnValue, this);
    }
    
    // methods to retrieve the membervariables
    
/**
 * Returns the font of this <CODE>Chunk</CODE>.
 *
 * @return    a <CODE>PdfFont</CODE>
 */
    
    PdfFont font() {
        return font;
    }
    
/**
 * Returns the color of this <CODE>Chunk</CODE>.
 *
 * @return    a <CODE>Color</CODE>
 */
    
    Color color() {
        return (Color)noStroke.get(Chunk.COLOR);
    }
    
/**
 * Returns the width of this <CODE>PdfChunk</CODE>.
 *
 * @return    a width
 */
    
    float width() {
        if (isAttribute(Chunk.CHAR_SPACING)) {
            Float cs = (Float) getAttribute(Chunk.CHAR_SPACING);
            return font.width(value) + value.length() * cs;
        }
        return font.width(value);
    }
    
/**
 * Checks if the <CODE>PdfChunk</CODE> split was caused by a newline.
 * @return <CODE>true</CODE> if the <CODE>PdfChunk</CODE> split was caused by a newline.
 */
    
    boolean isNewlineSplit()
    {
        return newlineSplit;
    }
    
/**
 * Gets the width of the <CODE>PdfChunk</CODE> taking into account the
 * extra character and word spacing.
 * @param charSpacing the extra character spacing
 * @param wordSpacing the extra word spacing
 * @return the calculated width
 */
    
    float getWidthCorrected(float charSpacing, float wordSpacing)
    {
        int numberOfSpaces = 0;
        int idx = -1;
        while ((idx = value.indexOf(' ', idx + 1)) >= 0)
            ++numberOfSpaces;
        return width() + (value.length() * charSpacing + numberOfSpaces * wordSpacing);
    }
    
    /**
     * Gets the text displacement relative to the baseline.
     * @return a displacement in points
     */
    float getTextRise() {
        Float f = (Float) getAttribute(Chunk.SUBSUPSCRIPT);
        if (f != null) {
            return f;
        }
        return 0.0f;
    }
    
/**
 * Trims the last space.
 * @return the width of the space trimmed, otherwise 0
 */
    
    float trimLastSpace()
    {
        BaseFont ft = font.getFont();
        if (ft.getFontType() == BaseFont.FONT_TYPE_CJK && ft.getUnicodeEquivalent(' ') != ' ') {
            if (value.length() > 1 && value.endsWith("\u0001")) {
                value = value.substring(0, value.length() - 1);
                return font.width('\u0001');
            }
        }
        else {
            if (value.length() > 1 && value.endsWith(" ")) {
                value = value.substring(0, value.length() - 1);
                return font.width(' ');
            }
        }
        return 0;
    }    
    void trimFirstSpace()
    {
        BaseFont ft = font.getFont();
        if (ft.getFontType() == BaseFont.FONT_TYPE_CJK && ft.getUnicodeEquivalent(' ') != ' ') {
            if (value.length() > 1 && value.startsWith("\u0001")) {
                value = value.substring(1);
            }
        }
        else {
            if (value.length() > 1 && value.startsWith(" ")) {
                value = value.substring(1);
            }
        }
    }
    
/**
 * Gets an attribute. The search is made in <CODE>attributes</CODE>
 * and <CODE>noStroke</CODE>.
 * @param name the attribute key
 * @return the attribute value or null if not found
 */
    
    Object getAttribute(String name)
    {
        if (attributes.containsKey(name))
            return attributes.get(name);
        return noStroke.get(name);
    }
    
/**
 *Checks if the attribute exists.
 * @param name the attribute key
 * @return <CODE>true</CODE> if the attribute exists
 */
    
    boolean isAttribute(String name)
    {
        if (attributes.containsKey(name))
            return true;
        return noStroke.containsKey(name);
    }
    
/**
 * Checks if this <CODE>PdfChunk</CODE> needs some special metrics handling.
 * @return <CODE>true</CODE> if this <CODE>PdfChunk</CODE> needs some special metrics handling.
 */
    
    boolean isStroked()
    {
        return (!attributes.isEmpty());
    }
    
    /**
     * Checks if this <CODE>PdfChunk</CODE> is a Separator Chunk.
     * @return    true if this chunk is a separator.
     * @since    2.1.2
     */
    boolean isSeparator() {
        return isAttribute(Chunk.SEPARATOR);
    }
    
    /**
     * Checks if this <CODE>PdfChunk</CODE> is a horizontal Separator Chunk.
     * @return    true if this chunk is a horizontal separator.
     * @since    2.1.2
     */
    boolean isHorizontalSeparator() {
        if (isAttribute(Chunk.SEPARATOR)) {
            Object[] o = (Object[])getAttribute(Chunk.SEPARATOR);
            return !(Boolean) o[1];
        }
        return false;
    }
    
    /**
     * Checks if this <CODE>PdfChunk</CODE> is a tab Chunk.
     * @return    true if this chunk is a separator.
     * @since    2.1.2
     */
    boolean isTab() {
        return isAttribute(Chunk.TAB);
    }
    
    /**
     * Correction for the tab position based on the left starting position.
     * @param    newValue    the new value for the left X.
     * @since    2.1.2
     */
    void adjustLeft(float newValue) {
        Object[] o = (Object[])attributes.get(Chunk.TAB);
        if (o != null) {
            attributes.put(Chunk.TAB, new Object[]{o[0], o[1], o[2], newValue});
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return value;
    }

    /**
     * Tells you if this string is in Chinese, Japanese, Korean or Identity-H.
     * @return true if the Chunk has a special encoding
     */
    
    boolean isSpecialEncoding() {
        return encoding.equals(CJKFont.CJK_ENCODING) || encoding.equals(BaseFont.IDENTITY_H);
    }

    int length() {
        return value.length();
    }
    
    int lengthUtf32() {
        if (!BaseFont.IDENTITY_H.equals(encoding))
            return value.length();
        int total = 0;
        int len = value.length();
        for (int k = 0; k < len; ++k) {
            if (Utilities.isSurrogateHigh(value.charAt(k)))
                ++k;
            ++total;
        }
        return total;
    }
    
    boolean isExtSplitCharacter(int current, char[] cc, PdfChunk[] ck) {
        return splitCharacter.isSplitCharacter(current, cc, ck);
    }
    
/**
 * Removes all the <VAR>' '</VAR> and <VAR>'-'</VAR>-characters on the right of a <CODE>String</CODE>.
 * <P>
 * @param    string        the <CODE>String<CODE> that has to be trimmed.
 * @return    the trimmed <CODE>String</CODE>
 */    
    String trim(String string) {
        BaseFont ft = font.getFont();
        if (ft.getFontType() == BaseFont.FONT_TYPE_CJK && ft.getUnicodeEquivalent(' ') != ' ') {
            while (string.endsWith("\u0001")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        else {
            while (string.endsWith(" ") || string.endsWith("\t")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    float getCharWidth(int c) {
        if (noPrint(c))
            return 0;
        if (isAttribute(Chunk.CHAR_SPACING)) {
            Float cs = (Float) getAttribute(Chunk.CHAR_SPACING);
            return font.width(c) + cs;
        }
        return font.width(c);
    }
    
    static boolean noPrint(int c) {
        return ((c >= 0x200b && c <= 0x200f) || (c >= 0x202a && c <= 0x202e));
    }
    
}

/*
 * $Id: Paragraph.java 3668 2009-02-01 09:08:50Z blowagie $
 *
 * Copyright 1999, 2000, 2001, 2002 by Bruno Lowagie.
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

package com.justifiedsolutions.openpdf.text;

import static com.justifiedsolutions.openpdf.text.AlignmentConverter.convertHorizontalAlignment;

import com.justifiedsolutions.openpdf.pdf.content.Content;
import java.util.List;

/**
 * A <CODE>Paragraph</CODE> is a series of <CODE>Chunk</CODE>s and/or <CODE>Phrases</CODE>.
 * <p>
 * A <CODE>Paragraph</CODE> has the same qualities of a <CODE>Phrase</CODE>, but also some
 * additional layout-parameters:
 * <UL>
 * <LI>the indentation
 * <LI>the alignment of the text
 * </UL>
 * <p>
 * Example:
 * <BLOCKQUOTE><PRE>
 * <STRONG>Paragraph p = new Paragraph("This is a paragraph",
 * FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLDITALIC, new Color(0, 0, 255)));</STRONG>
 * </PRE></BLOCKQUOTE>
 *
 * @see Element
 * @see Phrase
 */

public class Paragraph extends Phrase {

    // constants
    private static final long serialVersionUID = 7852314969733375514L;

    // membervariables

    /**
     * The alignment of the text.
     */
    protected int alignment = Element.ALIGN_UNDEFINED;

    /**
     * The text leading that is multiplied by the biggest font size in the line.
     */
    protected float multipliedLeading = 0;

    /**
     * The indentation of this paragraph on the left side.
     */
    protected float indentationLeft;

    /**
     * The indentation of this paragraph on the right side.
     */
    protected float indentationRight;
    /**
     * The spacing before the paragraph.
     */
    protected float spacingBefore;
    /**
     * The spacing after the paragraph.
     */
    protected float spacingAfter;
    /**
     * Does the paragraph has to be kept together on 1 page.
     */
    protected boolean keeptogether = false;
    /**
     * Holds value of property firstLineIndent.
     */
    private float firstLineIndent = 0;
    /**
     * Holds value of property extraParagraphSpace.
     */
    private float extraParagraphSpace = 0;

    // constructors

    /**
     * Constructs a <CODE>Paragraph</CODE>.
     */
    public Paragraph() {
        super();
    }

    /**
     * Constructs a <CODE>Paragraph</CODE> with a certain <CODE>Chunk</CODE>.
     *
     * @param chunk a <CODE>Chunk</CODE>
     */
    public Paragraph(Chunk chunk) {
        super(chunk);
    }

    /**
     * Constructs a <CODE>Paragraph</CODE> with a certain <CODE>String</CODE>.
     *
     * @param string a <CODE>String</CODE>
     */
    public Paragraph(String string) {
        super(string);
    }

    /**
     * Constructs a <CODE>Paragraph</CODE> with a certain <CODE>Phrase</CODE>.
     *
     * @param phrase a <CODE>Phrase</CODE>
     */
    public Paragraph(Phrase phrase) {
        super(phrase);
        if (phrase instanceof Paragraph) {
            Paragraph p = (Paragraph) phrase;
            setAlignment(p.alignment);
            setLeading(phrase.getLeading(), p.multipliedLeading);
            setIndentationLeft(p.getIndentationLeft());
            setIndentationRight(p.getIndentationRight());
            setFirstLineIndent(p.getFirstLineIndent());
            setSpacingAfter(p.getSpacingAfter());
            setSpacingBefore(p.getSpacingBefore());
            setExtraParagraphSpace(p.getExtraParagraphSpace());
        }
    }

    // implementation of the Element-methods

    /**
     * Creates a new internal Paragraph from an API Paragraph.
     *
     * @param paragraph API paragraph
     * @return internal paragraph
     */
    public static Paragraph getInstance(
            com.justifiedsolutions.openpdf.pdf.content.Paragraph paragraph) {
        Paragraph result = new Paragraph();
        result.setLeading(paragraph.getLeading(), paragraph.getLineHeight());
        result.setFont(FontFactory.getFont(paragraph.getFont()));
        result.setIndentationLeft(paragraph.getLeftIndent());
        result.setIndentationRight(paragraph.getRightIndent());
        result.setFirstLineIndent(paragraph.getFirstLineIndent());
        result.setSpacingBefore(paragraph.getSpacingBefore());
        result.setSpacingAfter(paragraph.getSpacingAfter());
        result.setKeepTogether(paragraph.isKeepTogether());
        result.setAlignment(convertHorizontalAlignment(paragraph.getAlignment()));
        for (Content content : paragraph.getContent()) {
            if (content instanceof com.justifiedsolutions.openpdf.pdf.content.Phrase) {
                result.add(Phrase.getInstance(
                        (com.justifiedsolutions.openpdf.pdf.content.Phrase) content));
            } else if (content instanceof com.justifiedsolutions.openpdf.pdf.content.Chunk) {
                result.add(Chunk.getInstance(
                        (com.justifiedsolutions.openpdf.pdf.content.Chunk) content));
            }
        }
        return result;
    }

    // methods

    /**
     * Gets the type of the text element.
     *
     * @return a type
     */
    public int type() {
        return Element.PARAGRAPH;
    }

    // setting the membervariables

    /**
     * Adds an <CODE>Object</CODE> to the <CODE>Paragraph</CODE>.
     *
     * @param o object        the object to add.
     * @return true is adding the object succeeded
     */
    public boolean add(Element o) {
        if (o instanceof Paragraph) {
            super.add(o);
            List<Chunk> chunks = getChunks();
            if (!chunks.isEmpty()) {
                Chunk tmp = chunks.get(chunks.size() - 1);
                super.add(new Chunk("\n", tmp.getFont()));
            } else {
                super.add(Chunk.NEWLINE);
            }
            return true;
        }
        return super.add(o);
    }

    /**
     * @see Phrase#setLeading(float)
     */
    public void setLeading(float fixedLeading) {
        this.leading = fixedLeading;
        this.multipliedLeading = 0;
    }

    /**
     * Sets the leading fixed and variable. The resultant leading will be
     * fixedLeading+multipliedLeading*maxFontSize where maxFontSize is the size of the biggest font
     * in the line.
     *
     * @param fixedLeading      the fixed leading
     * @param multipliedLeading the variable leading
     */
    public void setLeading(float fixedLeading, float multipliedLeading) {
        this.leading = fixedLeading;
        this.multipliedLeading = multipliedLeading;
    }

    /**
     * Checks if this paragraph has to be kept together on one page.
     *
     * @return true if the paragraph may not be split over 2 pages.
     */
    public boolean getKeepTogether() {
        return keeptogether;
    }

    /**
     * Indicates that the paragraph has to be kept together on one page.
     *
     * @param keeptogether true of the paragraph may not be split over 2 pages
     */
    public void setKeepTogether(boolean keeptogether) {
        this.keeptogether = keeptogether;
    }

    /**
     * Gets the alignment of this paragraph.
     *
     * @return alignment
     */
    public int getAlignment() {
        return alignment;
    }

    /**
     * Sets the alignment of this paragraph.
     *
     * @param alignment the new alignment
     */
    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }


    /**
     * Gets the variable leading
     *
     * @return the leading
     */
    public float getMultipliedLeading() {
        return multipliedLeading;
    }

    /**
     * Gets the total leading. This method is based on the assumption that the font of the Paragraph
     * is the font of all the elements that make part of the paragraph. This isn't necessarily
     * true.
     *
     * @return the total leading (fixed and multiplied)
     */
    public float getTotalLeading() {
        float m = font == null ?
                Font.DEFAULTSIZE * multipliedLeading : font.getCalculatedLeading(multipliedLeading);
        if (m > 0 && !hasLeading()) {
            return m;
        }
        return getLeading() + m;
    }

    /**
     * Gets the indentation of this paragraph on the left side.
     *
     * @return the indentation
     */
    public float getIndentationLeft() {
        return indentationLeft;
    }

    // methods to retrieve information

    /**
     * Sets the indentation of this paragraph on the left side.
     *
     * @param indentation the new indentation
     */
    public void setIndentationLeft(float indentation) {
        this.indentationLeft = indentation;
    }

    /**
     * Gets the indentation of this paragraph on the right side.
     *
     * @return the indentation
     */
    public float getIndentationRight() {
        return indentationRight;
    }

    /**
     * Sets the indentation of this paragraph on the right side.
     *
     * @param indentation the new indentation
     */
    public void setIndentationRight(float indentation) {
        this.indentationRight = indentation;
    }

    /**
     * Getter for property firstLineIndent.
     *
     * @return Value of property firstLineIndent.
     */
    public float getFirstLineIndent() {
        return this.firstLineIndent;
    }

    /**
     * Setter for property firstLineIndent.
     *
     * @param firstLineIndent New value of property firstLineIndent.
     */
    public void setFirstLineIndent(float firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    /**
     * Gets the spacing before this paragraph.
     *
     * @return the spacing
     * @since 2.1.5
     */
    public float getSpacingBefore() {
        return spacingBefore;
    }

    /**
     * Sets the spacing before this paragraph.
     *
     * @param spacing the new spacing
     */
    public void setSpacingBefore(float spacing) {
        this.spacingBefore = spacing;
    }

    /**
     * Gets the spacing after this paragraph.
     *
     * @return the spacing
     * @since 2.1.5
     */
    public float getSpacingAfter() {
        return spacingAfter;
    }

    /**
     * Sets the spacing after this paragraph.
     *
     * @param spacing the new spacing
     */
    public void setSpacingAfter(float spacing) {
        this.spacingAfter = spacing;
    }

    /**
     * Getter for property extraParagraphSpace.
     *
     * @return Value of property extraParagraphSpace.
     */
    public float getExtraParagraphSpace() {
        return this.extraParagraphSpace;
    }

    /**
     * Setter for property extraParagraphSpace.
     *
     * @param extraParagraphSpace New value of property extraParagraphSpace.
     */
    public void setExtraParagraphSpace(float extraParagraphSpace) {
        this.extraParagraphSpace = extraParagraphSpace;
    }
}

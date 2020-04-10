/*
 * $Id: Chunk.java 4092 2009-11-11 17:58:16Z psoares33 $
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

import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import com.justifiedsolutions.openpdf.text.pdf.HyphenationEvent;
import com.justifiedsolutions.openpdf.text.pdf.draw.DrawInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This is the smallest significant part of text that can be added to a document.
 * <p>
 * Most elements can be divided in one or more <CODE>Chunk</CODE>s. A chunk is a <CODE>String</CODE>
 * with a certain <CODE>Font</CODE>. All other layout parameters should be defined in the object to
 * which this chunk of text is added.
 * <p>
 * Example: <BLOCKQUOTE>
 *
 * <PRE>
 *
 * <STRONG>Chunk chunk = new Chunk("Hello world",
 * FontFactory.getFont(FontFactory.COURIER, 20, Font.ITALIC, new Color(255, 0, 0))); </STRONG>
 * document.add(chunk);
 *
 * </PRE>
 *
 * </BLOCKQUOTE>
 */

public class Chunk implements Element {

    // public static membervariables

    /**
     * The character stand in for an image or a separator.
     */
    public static final String OBJECT_REPLACEMENT_CHARACTER = "\ufffc";

    /**
     * This is a Chunk containing a newline.
     */
    public static final Chunk NEWLINE = new Chunk("\n");

    /**
     * This is a Chunk containing a newpage.
     */
    public static final Chunk NEXTPAGE = new Chunk("");
    /**
     * Key for drawInterface of the Separator.
     *
     * @since 2.1.2
     */
    public static final String SEPARATOR = "SEPARATOR";

    // member variables
    /**
     * Key for drawInterface of the tab.
     *
     * @since 2.1.2
     */
    public static final String TAB = "TAB";
    /**
     * Key for text horizontal scaling.
     */
    public static final String HSCALE = "HSCALE";
    /**
     * Key for underline.
     */
    public static final String UNDERLINE = "UNDERLINE";

    // constructors
    /**
     * Key for sub/superscript.
     */
    public static final String SUBSUPSCRIPT = "SUBSUPSCRIPT";
    /**
     * Key for text skewing.
     */
    public static final String SKEW = "SKEW";
    /**
     * Key for background.
     */
    public static final String BACKGROUND = "BACKGROUND";
    /**
     * Key for text rendering mode.
     */
    public static final String TEXTRENDERMODE = "TEXTRENDERMODE";
    /**
     * Key for split character.
     */
    public static final String SPLITCHARACTER = "SPLITCHARACTER";
    /**
     * Key for hyphenation.
     */
    public static final String HYPHENATION = "HYPHENATION";
    /**
     * Key for generic tag.
     */
    public static final String GENERICTAG = "GENERICTAG";
    /**
     * Key for image.
     */
    public static final String IMAGE = "IMAGE";
    /**
     * Key for newpage.
     */
    public static final String NEWPAGE = "NEWPAGE";

    // implementation of the Element-methods
    /**
     * Key for color.
     */
    public static final String COLOR = "COLOR";
    /**
     * Key for encoding.
     */
    public static final String ENCODING = "ENCODING";
    /**
     * Key for character spacing.
     */
    public static final String CHAR_SPACING = "CHAR_SPACING";

    // methods that change the member variables

    static {
        NEXTPAGE.setNewPage();
    }

    /**
     * This is the content of this chunk of text.
     */
    protected StringBuffer content = null;

    // methods to retrieve information
    /**
     * This is the <CODE>Font</CODE> of this chunk of text.
     */
    protected Font font = null;
    /**
     * Contains some of the attributes for this Chunk.
     */
    protected Map<String, Object> attributes = null;

    /**
     * Constructs a chunk of text with a certain content and a certain <CODE> Font</CODE>.
     *
     * @param content the content
     * @param font    the font
     */
    public Chunk(String content, Font font) {
        this.content = new StringBuffer();
        if (content != null) {
            this.content.append(content);
        }
        this.font = font;
    }

    // attributes

    /**
     * Constructs a chunk of text with a certain content, without specifying a
     * <CODE>Font</CODE>.
     *
     * @param content the content
     */
    public Chunk(String content) {
        this(content, new Font());
    }

    // the attributes are ordered as they appear in the book 'iText in Action'

    /**
     * Creates a tab Chunk. Note that separator chunks can't be used in combination with tab
     * chunks!
     *
     * @param separator   the drawInterface to use to draw the tab.
     * @param tabPosition an X coordinate that will be used as start position for the next Chunk.
     * @param newline     if true, a newline will be added if the tabPosition has already been
     *                    reached.
     * @since 2.1.2
     */
    public Chunk(DrawInterface separator, float tabPosition, boolean newline) {
        this(OBJECT_REPLACEMENT_CHARACTER, new Font());
        if (tabPosition < 0) {
            throw new IllegalArgumentException(MessageLocalization
                    .getComposedMessage("a.tab.position.may.not.be.lower.than.0.yours.is.1",
                            String.valueOf(tabPosition)));
        }
        setAttribute(TAB, new Object[]{separator, tabPosition, newline, (float) 0});
    }

    /**
     * Creates a new internal Chunk from an API Chunk.
     *
     * @param chunk API chunk
     * @return internal chunk
     */
    public static Chunk getInstance(com.justifiedsolutions.openpdf.pdf.content.Chunk chunk) {
        Objects.requireNonNull(chunk);
        if (chunk.isPageBreak()) {
            return Chunk.NEXTPAGE;
        }
        Chunk result = new Chunk(chunk.getText());
        result.setFont(FontFactory.getFont(chunk.getFont()));
        return result;
    }

    /**
     * Processes the element by adding it (or the different parts) to an <CODE>
     * ElementListener</CODE>.
     *
     * @param listener an <CODE>ElementListener</CODE>
     * @return <CODE>true</CODE> if the element was processed successfully
     */
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        } catch (DocumentException de) {
            return false;
        }
    }

    /**
     * Gets the type of the text element.
     *
     * @return a type
     */
    public int type() {
        return Element.CHUNK;
    }

    /**
     * Gets all the chunks in this element.
     *
     * @return an <CODE>ArrayList</CODE>
     */
    public List<Chunk> getChunks() {
        List<Chunk> tmp = new ArrayList<>();
        tmp.add(this);
        return tmp;
    }

    /**
     * appends some text to this <CODE>Chunk</CODE>.
     *
     * @param string <CODE>String</CODE>
     * @return a <CODE>StringBuffer</CODE>
     */
    public StringBuffer append(String string) {
        return content.append(string);
    }

    /**
     * Gets the font of this <CODE>Chunk</CODE>.
     *
     * @return a <CODE>Font</CODE>
     */

    public Font getFont() {
        return font;
    }

    /**
     * Sets the font of this <CODE>Chunk</CODE>.
     *
     * @param font a <CODE>Font</CODE>
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Returns the content of this <CODE>Chunk</CODE>.
     *
     * @return a <CODE>String</CODE>
     */
    public String getContent() {
        return content.toString();
    }

    /**
     * Returns the content of this <CODE>Chunk</CODE>.
     *
     * @return a <CODE>String</CODE>
     */
    public String toString() {
        return getContent();
    }

    /**
     * Checks is this <CODE>Chunk</CODE> is empty.
     *
     * @return <CODE>false</CODE> if the Chunk contains other characters than
     * space.
     */
    public boolean isEmpty() {
        return (content.toString().trim().length() == 0)
                && (!content.toString().contains("\n"))
                && (attributes == null);
    }

    /**
     * Checks the attributes of this <CODE>Chunk</CODE>.
     *
     * @return false if there aren't any.
     */

    public boolean hasAttributes() {
        return attributes != null;
    }


    /**
     * Gets the attributes for this <CODE>Chunk</CODE>.
     * <p>
     * It may be null.
     *
     * @return the attributes for this <CODE>Chunk</CODE>
     */

    public Map<String, Object> getChunkAttributes() {
        return attributes;
    }

    /**
     * Sets the attributes all at once.
     *
     * @param attributes the attributes of a Chunk
     */
    public void setChunkAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * Sets an arbitrary attribute.
     *
     * @param name the key for the attribute
     * @param obj  the value of the attribute
     * @return this <CODE>Chunk</CODE>
     */

    private Chunk setAttribute(String name, Object obj) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, obj);
        return this;
    }

    /**
     * Sets a new page tag..
     *
     * @return this <CODE>Chunk</CODE>
     */

    public Chunk setNewPage() {
        return setAttribute(NEWPAGE, null);
    }

    // keys used in PdfChunk

    /**
     * @see Element#isContent()
     * @since iText 2.0.8
     */
    public boolean isContent() {
        return true;
    }

    /**
     * @see Element#isNestable()
     * @since iText 2.0.8
     */
    public boolean isNestable() {
        return true;
    }

    /**
     * Returns the hyphenation (if present).
     *
     * @since 2.1.2
     */
    public HyphenationEvent getHyphenation() {
        if (attributes == null) {
            return null;
        }
        return (HyphenationEvent) attributes.get(Chunk.HYPHENATION);
    }

    /**
     * sets the hyphenation engine to this <CODE>Chunk</CODE>.
     *
     * @param hyphenation the hyphenation engine
     * @return this <CODE>Chunk</CODE>
     */
    public Chunk setHyphenation(HyphenationEvent hyphenation) {
        return setAttribute(HYPHENATION, hyphenation);
    }

}

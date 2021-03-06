/*
 * $Id: Phrase.java 4065 2009-09-16 23:09:11Z psoares33 $
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

import com.justifiedsolutions.openpdf.text.pdf.HyphenationEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A <CODE>Phrase</CODE> is a series of <CODE>Chunk</CODE>s.
 * <P>
 * A <CODE>Phrase</CODE> has a main <CODE>Font</CODE>, but some chunks
 * within the phrase can have a <CODE>Font</CODE> that differs from the
 * main <CODE>Font</CODE>. All the <CODE>Chunk</CODE>s in a <CODE>Phrase</CODE>
 * have the same <CODE>leading</CODE>.
 * <P>
 * Example:
 * <BLOCKQUOTE><PRE>
 * // When no parameters are passed, the default leading = 16
 * <STRONG>Phrase phrase0 = new Phrase();</STRONG>
 * <STRONG>Phrase phrase1 = new Phrase("this is a phrase");</STRONG>
 * // In this example the leading is passed as a parameter
 * <STRONG>Phrase phrase2 = new Phrase(16, "this is a phrase with leading 16");</STRONG>
 * // When a Font is passed (explicitly or embedded in a chunk), the default leading = 1.5 * size of the font
 * <STRONG>Phrase phrase3 = new Phrase("this is a phrase with a red, normal font Courier, size 12", FontFactory.getFont(FontFactory.COURIER, 12, Font.NORMAL, new Color(255, 0, 0)));</STRONG>
 * <STRONG>Phrase phrase4 = new Phrase(new Chunk("this is a phrase"));</STRONG>
 * <STRONG>Phrase phrase5 = new Phrase(18, new Chunk("this is a phrase", FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, new Color(255, 0, 0)));</STRONG>
 * </PRE></BLOCKQUOTE>
 *
 * @see        Element
 * @see        Chunk
 * @see        Paragraph
 */

public class Phrase extends ArrayList<Element> implements Element {

    // constants
    private static final long serialVersionUID = 2643594602455068231L;

    // membervariables
    /** This is the leading of this phrase. */
    protected float leading = Float.NaN;

    /** This is the font of this phrase. */
    protected Font font;

    /** Null, unless the Phrase has to be hyphenated.
     * @since    2.1.2
     */
    protected HyphenationEvent hyphenation = null;

    // constructors

    /**
     * Constructs a <CODE>Phrase</CODE> without specifying a leading.
     */
    public Phrase() {
        this(16);
    }

    /**
     * Copy constructor for <CODE>Phrase</CODE>.
     */
    public Phrase(Phrase phrase) {
        super();
        this.addAll(phrase);
        leading = phrase.getLeading();
        font = phrase.getFont();
        setHyphenation(phrase.getHyphenation());
    }

    /**
     * Constructs a <CODE>Phrase</CODE> with a certain leading.
     *
     * @param    leading        the leading
     */
    public Phrase(float leading) {
        this.leading = leading;
        font = new Font();
    }

    /**
     * Constructs a <CODE>Phrase</CODE> with a certain <CODE>Chunk</CODE>.
     *
     * @param    chunk        a <CODE>Chunk</CODE>
     */
    public Phrase(Chunk chunk) {
        super.add(chunk);
        font = chunk.getFont();
        setHyphenation(chunk.getHyphenation());
    }

    /**
     * Constructs a <CODE>Phrase</CODE> with a certain <CODE>String</CODE>.
     *
     * @param    string        a <CODE>String</CODE>
     */
    public Phrase(String string) {
        this(Float.NaN, string, new Font());
    }

    /**
     * Constructs a <CODE>Phrase</CODE> with a certain leading, a certain <CODE>String</CODE>
     * and a certain <CODE>Font</CODE>.
     *
     * @param    leading    the leading
     * @param    string        a <CODE>String</CODE>
     * @param    font        a <CODE>Font</CODE>
     */
    public Phrase(float leading, String string, Font font) {
        this.leading = leading;
        this.font = font;
        /* bugfix by August Detlefsen */
        if (string != null && string.length() != 0) {
            super.add(new Chunk(string, font));
        }
    }

    // implementation of the Element-methods

    /**
     * Processes the element by adding it (or the different parts) to an
     * <CODE>ElementListener</CODE>.
     *
     * @param    listener    an <CODE>ElementListener</CODE>
     * @return    <CODE>true</CODE> if the element was processed successfully
     */
    public boolean process(ElementListener listener) {
        try {
            for (Object o : this) {
                listener.add((Element) o);
            }
            return true;
        }
        catch(DocumentException de) {
            return false;
        }
    }

    /**
     * Gets the type of the text element.
     *
     * @return    a type
     */
    public int type() {
        return Element.PHRASE;
    }

    /**
     * Gets all the chunks in this element.
     *
     * @return    an <CODE>ArrayList</CODE>
     */
    public java.util.List<Chunk> getChunks() {
        List<Chunk> tmp = new ArrayList<>();
        for (Element element : this) {
            tmp.addAll(element.getChunks());
        }
        return tmp;
    }

    /**
     * @see Element#isContent()
     * @since    iText 2.0.8
     */
    public boolean isContent() {
        return true;
    }

    /**
     * @see Element#isNestable()
     * @since    iText 2.0.8
     */
    public boolean isNestable() {
        return true;
    }

    // overriding some of the ArrayList-methods

    public void add(int index, Element element) {
        if (element == null) return;
        try {
            if (element.type() == Element.CHUNK) {
                Chunk chunk = (Chunk) element;
                if (!font.isStandardFont()) {
                    chunk.setFont(font.difference(chunk.getFont()));
                }
                if (hyphenation != null && chunk.getHyphenation() == null && !chunk.isEmpty()) {
                    chunk.setHyphenation(hyphenation);
                }
                super.add(index, chunk);
            }
            else if (element.type() == Element.PHRASE || element.type() == Element.TABLE) {
                super.add(index, element);
            }
            else {
                throw new ClassCastException(String.valueOf(element.type()));
            }
        }
        catch(ClassCastException cce) {
            throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", cce.getMessage()));
        }
    }

    public boolean add(Element element) {
        if (element == null) return false;
        try {
            switch(element.type()) {
                case Element.CHUNK:
                    return addChunk((Chunk) element);
                case Element.PHRASE:
                case Element.PARAGRAPH:
                    Phrase phrase = (Phrase) element;
                    boolean success = true;
                    Element e;
                    for (Object o1 : phrase) {
                        e = (Element) o1;
                        if (e instanceof Chunk) {
                            success &= addChunk((Chunk) e);
                        } else {
                            success &= this.add(e);
                        }
                    }
                    return success;
                case Element.PTABLE: // case added by mr. Karen Vardanyan
                    // This will only work for PDF!!! Not for RTF/HTML
                    return super.add(element);
                    default:
                        throw new ClassCastException(String.valueOf(element.type()));
            }
        }
        catch(ClassCastException cce) {
            throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", cce.getMessage()));
        }
    }

    /**
     * Adds a collection of <CODE>Chunk</CODE>s
     * to this <CODE>Phrase</CODE>.
     *
     * @param    collection    a collection of <CODE>Chunk</CODE>s, <CODE>Anchor</CODE>s and <CODE>Phrase</CODE>s.
     * @return    <CODE>true</CODE> if the action succeeded, <CODE>false</CODE> if not.
     * @throws    ClassCastException    when you try to add something that isn't a <CODE>Chunk</CODE>, <CODE>Anchor</CODE> or <CODE>Phrase</CODE>
     */
    public boolean addAll(Collection<? extends Element> collection) {
        for (Element o : collection) {
            this.add(o);
        }
        return true;
    }

    /**
     * Adds a Chunk.
     * <p>
     * This method is a hack to solve a problem I had with phrases that were split between chunks
     * in the wrong place.
     * @param chunk a Chunk to add to the Phrase
     * @return true if adding the Chunk succeeded
     */
    protected boolean addChunk(Chunk chunk) {
        Font f = chunk.getFont();
        String c = chunk.getContent();
        if (font != null && !font.isStandardFont()) {
            f = font.difference(chunk.getFont());
        }
        if (size() > 0 && !chunk.hasAttributes()) {
            try {
                Chunk previous = (Chunk) get(size() - 1);
                if (!previous.hasAttributes()
                        && (f == null
                        || f.compareTo(previous.getFont()) == 0)
                        && !"".equals(previous.getContent().trim())
                        && !"".equals(c.trim())) {
                    previous.append(c);
                    return true;
                }
            }
            catch(ClassCastException cce) {
            }
        }
        Chunk newChunk = new Chunk(c, f);
        newChunk.setChunkAttributes(chunk.getChunkAttributes());
        if (hyphenation != null && newChunk.getHyphenation() == null && !newChunk.isEmpty()) {
            newChunk.setHyphenation(hyphenation);
        }
        return super.add(newChunk);
    }

    // other methods that change the member variables

    /**
     * Sets the leading of this phrase.
     *
     * @param    leading        the new leading
     */

    public void setLeading(float leading) {
        this.leading = leading;
    }

    /**
     * Sets the main font of this phrase.
     * @param font    the new font
     */
    public void setFont(Font font) {
        this.font = font;
    }

    // methods to retrieve information

    /**
     * Gets the leading of this phrase.
     *
     * @return    the linespacing
     */
    public float getLeading() {
        if (Float.isNaN(leading) && font != null) {
            return font.getCalculatedLeading(1.5f);
        }
        return leading;
    }

    /**
     * Checks you if the leading of this phrase is defined.
     *
     * @return    true if the leading is defined
     */
    public boolean hasLeading() {
        return !Float.isNaN(leading);
    }

    /**
     * Gets the font of the first <CODE>Chunk</CODE> that appears in this <CODE>Phrase</CODE>.
     *
     * @return    a <CODE>Font</CODE>
     */
    public Font getFont() {
        return font;
    }

    /**
     * Checks is this <CODE>Phrase</CODE> contains no or 1 empty <CODE>Chunk</CODE>.
     *
     * @return    <CODE>false</CODE> if the <CODE>Phrase</CODE>
     * contains more than one or more non-empty<CODE>Chunk</CODE>s.
     */
    public boolean isEmpty() {
        switch(size()) {
            case 0:
                return true;
            case 1:
                Element element = get(0);
                return element.type() == Element.CHUNK && ((Chunk) element).isEmpty();
            default:
                    return false;
        }
    }

    /**
     * Getter for the hyphenation settings.
     * @return    a HyphenationEvent
     * @since    2.1.2
     */
    public HyphenationEvent getHyphenation() {
        return hyphenation;
    }

    /**
     * Setter for the hyphenation.
     * @param    hyphenation    a HyphenationEvent instance
     * @since    2.1.2
     */
    public void setHyphenation(HyphenationEvent hyphenation) {
        this.hyphenation = hyphenation;
    }

    /**
     * Creates a new internal Phrase from an API Phrase.
     *
     * @param phrase API phrase
     * @return internal phrase
     */
    public static Phrase getInstance(com.justifiedsolutions.openpdf.pdf.content.Phrase phrase) {
        Objects.requireNonNull(phrase);
        Phrase result = new Phrase();
        result.setLeading(phrase.getLeading());
        result.setFont(FontFactory.getFont(phrase.getFont()));
        for (com.justifiedsolutions.openpdf.pdf.content.Chunk chunk : phrase.getChunks()) {
            result.add(Chunk.getInstance(chunk));
        }
        return result;
    }
}

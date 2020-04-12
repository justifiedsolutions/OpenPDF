/*
 * $Id: Document.java 4106 2009-11-27 12:59:39Z blowagie $
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

import java.util.ArrayList;
import java.util.List;


/**
 * A generic Document class.
 * <p>
 * All kinds of Text-elements can be added to a <CODE>HTMLDocument</CODE>. The <CODE>Document</CODE>
 * signals all the listeners when an element has been added.
 * <p>
 * Remark:
 * <OL>
 * <LI>Once a document is created you can add some meta information.
 * <LI>You can also set the headers/footers.
 * <LI>You have to open the document before you can write content.
 * <LI>You can only write content (no more meta-formation!) once a document is
 * opened.
 * <LI>When you change the header/footer on a certain page, this will be
 * effective starting on the next page.
 * <LI>After closing the document, every listener (as well as its <CODE>
 * OutputStream</CODE>) is closed too.
 * </OL>
 * Example: <BLOCKQUOTE>
 *
 * <PRE>// creation of the document with a certain size and certain margins
 * <STRONG>Document document = new Document(PageSize.A4, 50, 50, 50, 50);
 * </STRONG> try {
 * PdfWriter.getInstance(<STRONG>document </STRONG>, new FileOutputStream("text.pdf")); // we add
 * some meta information to the document
 * <STRONG>document.addAuthor("Bruno Lowagie"); </STRONG>
 * <STRONG>document.addSubject("This is the result of a Test."); </STRONG>
 * // we open the document for writing
 * <STRONG>document.open(); </STRONG>
 * <STRONG>document.add(new Paragraph("Hello world"));</STRONG>
 * } catch(DocumentException de) { System.err.println(de.getMessage()); }
 * <STRONG>document.close();</STRONG>
 * </PRE>
 *
 * </BLOCKQUOTE>
 */

public class Document {

    /**
     * The DocListener.
     */
    private final List<DocListener> listeners = new ArrayList<>();

    /**
     * The size of the page.
     */
    private Rectangle pageSize;
    /**
     * margin in x direction starting from the getDocumentLeft
     */
    private float marginLeft;
    /**
     * margin in x direction starting from the getDocumentRight
     */
    private float marginRight;
    /**
     * margin in y direction starting from the getDocumentTop
     */
    private float marginTop;
    /**
     * margin in y direction starting from the getDocumentBottom
     */
    private float marginBottom;


    /**
     * Current pagenumber
     */
    private int pageNumber = 0;

    /**
     * Constructs a new <CODE>Document</CODE> -object.
     *
     * @param pageSize     the pageSize
     * @param marginLeft   the margin on the getDocumentLeft
     * @param marginRight  the margin on the getDocumentRight
     * @param marginTop    the margin on the getDocumentTop
     * @param marginBottom the margin on the getDocumentBottom
     */
    public Document(Rectangle pageSize, float marginLeft, float marginRight,
            float marginTop, float marginBottom) {
        this.pageSize = pageSize;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
    }

    /**
     * Adds a <CODE>DocListener</CODE> to the <CODE>Document</CODE>.
     *
     * @param listener the new DocListener.
     */
    public void addDocListener(DocListener listener) {
        listeners.add(listener);
    }

    /**
     * Adds an <CODE>Element</CODE> to the <CODE>Document</CODE>.
     *
     * @param element the <CODE>Element</CODE> to add
     * @return <CODE>true</CODE> if the element was added, <CODE>false
     * </CODE> if not
     * @throws DocumentException when a document isn't open yet, or has been closed
     */
    public boolean add(Element element) throws DocumentException {
        boolean success = false;
        for (DocListener listener : listeners) {
            success |= listener.add(element);
        }
        return success;
    }

    /**
     * Opens the document.
     * <p>
     * Once the document is opened, you can't write any Header- or Meta-information anymore. You
     * have to open the document before you can begin to add content to the body of the document.
     */
    public void open() {
        for (DocListener listener : listeners) {
            listener.open();
        }
    }

    /**
     * Returns the current page number.
     *
     * @return the current page number
     */
    public int getPageNumber() {
        return this.pageNumber;
    }

    /**
     * Closes the document.
     * <p>
     * Once all the content has been written in the body, you have to close the body. After that
     * nothing can be written to the body anymore.
     */
    public void close() {
        for (DocListener listener : listeners) {
            listener.close();
        }
    }

    /**
     * Returns the lower getDocumentLeft x-coordinate.
     *
     * @return the lower getDocumentLeft x-coordinate
     */
    public float getDocumentLeft() {
        return pageSize.getLeft(marginLeft);
    }

    /**
     * Returns the upper getDocumentRight x-coordinate.
     *
     * @return the upper getDocumentRight x-coordinate
     */
    public float getDocumentRight() {
        return pageSize.getRight(marginRight);
    }

    /**
     * Returns the upper getDocumentRight y-coordinate.
     *
     * @return the upper getDocumentRight y-coordinate
     */
    public float getDocumentTop() {
        return pageSize.getTop(marginTop);
    }

    /**
     * Returns the lower getDocumentLeft y-coordinate.
     *
     * @return the lower getDocumentLeft y-coordinate
     */
    public float getDocumentBottom() {
        return pageSize.getBottom(marginBottom);
    }

    /**
     * Returns the lower getDocumentLeft x-coordinate considering a given margin.
     *
     * @param margin a margin
     * @return the lower getDocumentLeft x-coordinate
     */
    protected float getDocumentLeft(float margin) {
        return pageSize.getLeft(marginLeft + margin);
    }

    /**
     * Returns the upper getDocumentRight x-coordinate, considering a given margin.
     *
     * @param margin a margin
     * @return the upper getDocumentRight x-coordinate
     */
    protected float getDocumentRight(float margin) {
        return pageSize.getRight(marginRight + margin);
    }

    /**
     * Returns the upper getDocumentRight y-coordinate, considering a given margin.
     *
     * @param margin a margin
     * @return the upper getDocumentRight y-coordinate
     */
    protected float getDocumentTop(float margin) {
        return pageSize.getTop(marginTop + margin);
    }

    /**
     * Returns the lower getDocumentLeft y-coordinate, considering a given margin.
     *
     * @param margin a margin
     * @return the lower getDocumentLeft y-coordinate
     */
    protected float getDocumentBottom(float margin) {
        return pageSize.getBottom(marginBottom + margin);
    }

    /**
     * Utilized by subclasses to increment the current page number.
     */
    protected void incrementPageNumber() {
        this.pageNumber++;
    }

    /**
     * Gets the pagesize.
     *
     * @return the page size
     */
    public Rectangle getPageSize() {
        return this.pageSize;
    }

    /**
     * Gets the left margin.
     *
     * @return left margin
     */
    public float getMarginLeft() {
        return marginLeft;
    }

    /**
     * Gets the right margin.
     *
     * @return right margin
     */
    public float getMarginRight() {
        return marginRight;
    }

    /**
     * Gets the top margin.
     *
     * @return top margin
     */
    public float getMarginTop() {
        return marginTop;
    }

    /**
     * Gets the bottom margin.
     *
     * @return bottom margin
     */
    public float getMarginBottom() {
        return marginBottom;
    }
}

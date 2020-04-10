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

import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


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

    // membervariables
    private static final String VERSION_PROPERTIES = "com/justifiedsolutions/openpdf/text/version.properties";
    private static final String OPEN_PDF = "JSOpenPDF";
    private static final String RELEASE;
    private static final String OPEN_PDF_VERSION;
    /**
     * Allows the pdf documents to be produced without compression for debugging purposes.
     */
    public static boolean compress = true;
    /**
     * When true the file access is not done through a memory mapped file. Use it if the file is too
     * big to be mapped in your address space.
     */
    public static boolean plainRandomAccess = false;

    static {
        RELEASE = getVersionNumber();
        OPEN_PDF_VERSION = OPEN_PDF + " " + RELEASE;
    }

    /**
     * The DocListener.
     */
    private final List<DocListener> listeners = new ArrayList<>();
    /**
     * Is the document open or not?
     */
    protected boolean open;
    /**
     * Has the document already been closed?
     */
    protected boolean close;
    /**
     * The size of the page.
     */
    protected Rectangle pageSize;

    // membervariables concerning the layout
    /**
     * margin in x direction starting from the left
     */
    protected float marginLeft;
    /**
     * margin in x direction starting from the right
     */
    protected float marginRight;
    /**
     * margin in y direction starting from the top
     */
    protected float marginTop;
    /**
     * margin in y direction starting from the bottom
     */
    protected float marginBottom;
    /**
     * Current pagenumber
     */
    protected int pageN = 0;

    /**
     * Constructs a new <CODE>Document</CODE> with a default page size of A4 with 36pt margins all around.
     */
    public Document() {
        this(new RectangleReadOnly(595,842), 36, 36, 36, 36);
    }

    /**
     * Constructs a new <CODE>Document</CODE> -object.
     *
     * @param pageSize     the pageSize
     * @param marginLeft   the margin on the left
     * @param marginRight  the margin on the right
     * @param marginTop    the margin on the top
     * @param marginBottom the margin on the bottom
     */
    public Document(Rectangle pageSize, float marginLeft, float marginRight,
            float marginTop, float marginBottom) {
        this.pageSize = pageSize;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
    }

    protected List<DocListener> getListeners() {
        return listeners;
    }

    private static String getVersionNumber() {
        String releaseVersion = "UNKNOWN";
        try (InputStream input = Document.class.getClassLoader()
                .getResourceAsStream(VERSION_PROPERTIES)) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                releaseVersion = prop.getProperty("bundleVersion", releaseVersion);
            }
        } catch (IOException ignored) {
            // ignore this and leave the default
        }
        return releaseVersion;
    }

    /**
     * Gets the release number.
     *
     * @return the product name
     * @since 2.1.6
     */
    public static String getRelease() {
        return RELEASE;
    }

    /**
     * Gets the iText version.
     *
     * @return iText version
     */
    public static String getVersion() {
        return OPEN_PDF_VERSION;
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
        if (close) {
            throw new DocumentException(MessageLocalization
                    .getComposedMessage("the.document.has.been.closed.you.can.t.add.any.elements"));
        }
        if (!open && element.isContent()) {
            throw new DocumentException(MessageLocalization.getComposedMessage(
                    "the.document.is.not.open.yet.you.can.only.add.meta.information"));
        }
        boolean success = false;
        for (DocListener listener : getListeners()) {
            success |= listener.add(element);
        }
        if (element instanceof LargeElement) {
            LargeElement e = (LargeElement) element;
            if (!e.isComplete()) {
                e.flushContent();
            }
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
        if (!close) {
            open = true;
        }
        for (DocListener listener : getListeners()) {
            listener.setPageSize(pageSize);
            listener.setMargins(marginLeft, marginRight, marginTop, marginBottom);
            listener.open();
        }
    }

    /**
     * Signals that an new page has to be started.
     */
    public void newPage() {
        if (!open || close) {
            return;
        }
        for (DocListener listener : getListeners()) {
            listener.newPage();
        }
    }

    /**
     * Returns the current page number.
     *
     * @return the current page number
     */
    public int getPageNumber() {
        return this.pageN;
    }

    /**
     * Closes the document.
     * <p>
     * Once all the content has been written in the body, you have to close the body. After that
     * nothing can be written to the body anymore.
     */
    public void close() {
        if (!close) {
            open = false;
            close = true;
        }
        for (DocListener listener : getListeners()) {
            listener.close();
        }
    }

    /**
     * Returns the lower left x-coordinate.
     *
     * @return the lower left x-coordinate
     */

    public float left() {
        return pageSize.getLeft(marginLeft);
    }

    /**
     * Returns the upper right x-coordinate.
     *
     * @return the upper right x-coordinate
     */

    public float right() {
        return pageSize.getRight(marginRight);
    }

    /**
     * Returns the upper right y-coordinate.
     *
     * @return the upper right y-coordinate
     */

    public float top() {
        return pageSize.getTop(marginTop);
    }

    /**
     * Returns the lower left y-coordinate.
     *
     * @return the lower left y-coordinate
     */

    public float bottom() {
        return pageSize.getBottom(marginBottom);
    }

    /**
     * Returns the lower left x-coordinate considering a given margin.
     *
     * @param margin a margin
     * @return the lower left x-coordinate
     */

    protected float left(float margin) {
        return pageSize.getLeft(marginLeft + margin);
    }

    /**
     * Returns the upper right x-coordinate, considering a given margin.
     *
     * @param margin a margin
     * @return the upper right x-coordinate
     */

    protected float right(float margin) {
        return pageSize.getRight(marginRight + margin);
    }

    /**
     * Returns the upper right y-coordinate, considering a given margin.
     *
     * @param margin a margin
     * @return the upper right y-coordinate
     */

    protected float top(float margin) {
        return pageSize.getTop(marginTop + margin);
    }

    /**
     * Returns the lower left y-coordinate, considering a given margin.
     *
     * @param margin a margin
     * @return the lower left y-coordinate
     */

    protected float bottom(float margin) {
        return pageSize.getBottom(marginBottom + margin);
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
     * Checks if the document is open.
     *
     * @return <CODE>true</CODE> if the document is open
     */
    public boolean isOpen() {
        return open;
    }

}

/*
 * $Id: PdfDocument.java 4098 2009-11-16 13:27:45Z blowagie $
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

package com.justifiedsolutions.openpdf.text.pdf;

import com.justifiedsolutions.openpdf.text.Chunk;
import com.justifiedsolutions.openpdf.text.DocListener;
import com.justifiedsolutions.openpdf.text.Document;
import com.justifiedsolutions.openpdf.text.DocumentException;
import com.justifiedsolutions.openpdf.text.Element;
import com.justifiedsolutions.openpdf.text.ExceptionConverter;
import com.justifiedsolutions.openpdf.text.Font;
import com.justifiedsolutions.openpdf.text.LargeElement;
import com.justifiedsolutions.openpdf.text.Meta;
import com.justifiedsolutions.openpdf.text.Paragraph;
import com.justifiedsolutions.openpdf.text.Phrase;
import com.justifiedsolutions.openpdf.text.Rectangle;
import com.justifiedsolutions.openpdf.text.Section;
import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import com.justifiedsolutions.openpdf.text.pdf.draw.DrawInterface;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <CODE>PdfDocument</CODE> is the class that is used by <CODE>PdfWriter</CODE>
 * to translate a <CODE>Document</CODE> into a PDF with different pages.
 * <p>
 * A <CODE>PdfDocument</CODE> always listens to a <CODE>Document</CODE> and adds the Pdf
 * representation of every <CODE>Element</CODE> that is added to the <CODE>Document</CODE>.
 *
 * @since 2.0.8 (class was package-private before)
 */

class PdfDocument extends Document implements DocListener {

    /**
     * The characters to be applied the hanging punctuation.
     */
    private static final String hangingPunctuation = ".,;:'";
    /**
     * Is the document open or not?
     */
    protected boolean open;
    /**
     * Has the document already been closed?
     */
    protected boolean close;
    /**
     * The <CODE>PdfWriter</CODE>.
     */
    private PdfWriter writer;
    /**
     * This is the PdfContentByte object, containing the text.
     */
    private PdfContentByte text;
    /**
     * This is the PdfContentByte object, containing the borders and other Graphics.
     */
    private PdfContentByte graphics;
    /**
     * This represents the leading of the lines.
     */
    private float leading = 0;
    /**
     * This represents the current alignment of the PDF Elements.
     */
    private int alignment = Element.ALIGN_LEFT;
    /**
     * This is the current height of the document.
     */
    private float currentHeight = 0;
    /**
     * Signals that onParagraph is valid (to avoid that a Chapter/Section title is treated as a
     * Paragraph).
     *
     * @since 2.1.2
     */
    private boolean isSectionTitle = false;
    private int textEmptySize;
    /**
     * Signals that OnOpenDocument should be called.
     */
    private boolean firstPageEvent = true;
    /**
     * The line that is currently being written.
     */
    private PdfLine line = null;
    /**
     * The lines that are written until now.
     */
    private java.util.List<PdfLine> lines = new ArrayList<>();
    /**
     * Holds the type of the last element, that has been added to the document.
     */
    private int lastElementType = -1;
    private Indentation indentation = new Indentation();

//    [L1] DocListener interface
    /**
     * some meta information about the Document.
     */
    private PdfInfo info = new PdfInfo();
    /**
     * This is the root outline of the document.
     */
    private PdfOutline rootOutline;
    /**
     * This is the current <CODE>PdfOutline</CODE> in the hierarchy of outlines.
     */
    private PdfOutline currentOutline;
    /**
     * This is the size of the several boxes of the current Page.
     */
    private HashMap<String, PdfRectangle> thisBoxSize = new HashMap<>();
    /**
     * This is the size of the several boxes that will be used in the next page.
     */
    private HashMap<String, PdfRectangle> boxSize = new HashMap<>();
    /**
     * This checks if the page is empty.
     */
    private boolean pageEmpty = true;
    /**
     * The duration of the page
     */
    private int duration = -1; // negative values will indicate no duration
    /**
     * The page transition
     */
    private PdfTransition transition = null;
    private PdfDictionary pageAA = null;
    private PdfIndirectReference thumb;
    /**
     * This are the page resources of the current Page.
     */
    private PageResources pageResources;
    /**
     * This is the position where the image ends.
     */
    private float imageEnd = -1;

    public PdfDocument(Rectangle pageSize, float marginLeft, float marginRight, float marginTop,
            float marginBottom) {
        super(pageSize, marginLeft, marginRight, marginTop, marginBottom);
    }

    static PdfPTable createInOneCell(Iterator<Element> elements) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(0);
        while (elements.hasNext()) {
            cell.addElement(elements.next());
        }
        table.addCell(cell);
        return table;
    }

    /**
     * Adds a <CODE>PdfWriter</CODE> to the <CODE>PdfDocument</CODE>.
     *
     * @param writer the <CODE>PdfWriter</CODE> that writes everything what is added to this
     *               document to an outputstream.
     * @throws DocumentException on error
     */
    void addWriter(PdfWriter writer) throws DocumentException {
        if (this.writer == null) {
            this.writer = writer;
            return;
        }
        throw new DocumentException(MessageLocalization
                .getComposedMessage("you.can.only.add.a.writer.to.a.pdfdocument.once"));
    }

    /**
     * Signals that an <CODE>Element</CODE> was added to the <CODE>Document</CODE>.
     *
     * @param element the element to add
     * @return <CODE>true</CODE> if the element was added, <CODE>false</CODE> if not.
     * @throws DocumentException when a document isn't open yet, or has been closed
     */
    @Override
    public boolean add(Element element) throws DocumentException {
        if (close) {
            throw new DocumentException(MessageLocalization
                    .getComposedMessage("the.document.has.been.closed.you.can.t.add.any.elements"));
        }
        if (!open && element.isContent()) {
            throw new DocumentException(MessageLocalization.getComposedMessage(
                    "the.document.is.not.open.yet.you.can.only.add.meta.information"));
        }
        try {
            switch (element.type()) {
                // Information (headers)
                case Element.TITLE:
                    info.addTitle(((Meta) element).getContent());
                    break;
                case Element.SUBJECT:
                    info.addSubject(((Meta) element).getContent());
                    break;
                case Element.KEYWORDS:
                    info.addKeywords(((Meta) element).getContent());
                    break;
                case Element.AUTHOR:
                    info.addAuthor(((Meta) element).getContent());
                    break;
                case Element.CREATOR:
                    info.addCreator(((Meta) element).getContent());
                    break;
                case Element.PRODUCER:
                    info.addProducer(((Meta) element).getContent());
                    break;
                case Element.CREATIONDATE:
                    // you can not set the creation date, only reset it
                    info.addCreationDate();
                    break;

                // content (text)
                case Element.CHUNK: {
                    add(new PdfChunk((Chunk) element));
                    break;
                }
                case Element.PHRASE: {
                    // we cast the element to a phrase and set the leading of the document
                    leading = ((Phrase) element).getLeading();
                    // we process the element
                    element.process(this);
                    break;
                }
                case Element.PARAGRAPH: {
                    // we cast the element to a paragraph
                    add((Paragraph) element);
                    break;
                }
                case Element.SECTION:
                case Element.CHAPTER: {
                    // Chapters and Sections only differ in their constructor
                    // so we cast both to a Section
                    add((Section) element);
                    break;
                }
                case Element.RECTANGLE: {
                    graphics.rectangle((Rectangle) element);
                    pageEmpty = false;
                    break;
                }
                case Element.PTABLE: {
                    add((PdfPTable) element);
                    break;
                }
                case Element.MULTI_COLUMN_TEXT: {
                    add((MultiColumnText) element);
                    break;
                }
                default:
                    return false;
            }
            lastElementType = element.type();

            if (element instanceof LargeElement) {
                LargeElement e = (LargeElement) element;
                if (!e.isComplete()) {
                    e.flushContent();
                }
            }

            return true;
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    private void add(MultiColumnText multiText) {
        ensureNewLine();
        flushLines();
        float height = multiText
                .write(writer.getDirectContent(), this, indentTop() - currentHeight);
        currentHeight += height;
        text.moveText(0, -1f * height);
        pageEmpty = false;
    }

    private void add(PdfPTable ptable) {
        if (ptable.size() <= ptable.getHeaderRows()) {
            return;
        }

        // before every table, we add a new line and flush all lines
        ensureNewLine();
        flushLines();

        addPTable(ptable);
        pageEmpty = false;
        newLine();
    }

    private void add(Section section) {
        PdfPageEvent pageEvent = writer.getPageEvent();

        boolean hasTitle = section.isNotAddedYet()
                && section.getTitle() != null;

        // if the section is a chapter, we begin a new page
        if (section.isTriggerNewPage()) {
            newPage();
        }

        if (hasTitle) {
            float fith = indentTop() - currentHeight;
            int rotation = getPageSize().getRotation();
            if (rotation == 90 || rotation == 180) {
                fith = getPageSize().getHeight() - fith;
            }
            PdfDestination destination = new PdfDestination(PdfDestination.FITH, fith);
            while (currentOutline.level() >= section.getDepth()) {
                currentOutline = currentOutline.parent();
            }
            currentOutline = new PdfOutline(currentOutline, destination, section.getBookmarkTitle(),
                    section.isBookmarkOpen());
        }

        // some values are set
        carriageReturn();
        indentation.sectionIndentLeft += section.getIndentationLeft();
        indentation.sectionIndentRight += section.getIndentationRight();

        if (section.isNotAddedYet() && pageEvent != null) {
            if (section.type() == Element.CHAPTER) {
                pageEvent.onChapter(writer, this, indentTop() - currentHeight, section.getTitle());
            } else {
                pageEvent.onSection(writer, this, indentTop() - currentHeight, section.getDepth(),
                        section.getTitle());
            }
        }

        // the title of the section (if any has to be printed)
        if (hasTitle) {
            isSectionTitle = true;
            add((Element) section.getTitle());
            isSectionTitle = false;
        }
        indentation.sectionIndentLeft += section.getIndentation();
        // we process the section
        section.process(this);
        flushLines();
        // some parameters are set back to normal again
        indentation.sectionIndentLeft -= (section.getIndentationLeft() + section.getIndentation());
        indentation.sectionIndentRight -= section.getIndentationRight();

        if (section.isComplete() && pageEvent != null) {
            if (section.type() == Element.CHAPTER) {
                pageEvent.onChapterEnd(writer, this, indentTop() - currentHeight);
            } else {
                pageEvent.onSectionEnd(writer, this, indentTop() - currentHeight);
            }
        }
    }

    private void add(Paragraph paragraph) {
        addSpacing(paragraph.getSpacingBefore(), leading, paragraph.getFont());

        // we adjust the parameters of the document
        alignment = paragraph.getAlignment();
        leading = paragraph.getTotalLeading();
        carriageReturn();

        // we don't want to make orphans/widows
        if (currentHeight + line.height() + leading > indentTop() - indentBottom()) {
            newPage();
        }
        indentation.indentLeft += paragraph.getIndentationLeft();
        indentation.indentRight += paragraph.getIndentationRight();
        carriageReturn();

        PdfPageEvent pageEvent = writer.getPageEvent();
        if (pageEvent != null && !isSectionTitle) {
            pageEvent.onParagraph(writer, this, indentTop() - currentHeight);
        }

        // if a paragraph has to be kept together, we wrap it in a table object
        if (paragraph.getKeepTogether()) {
            carriageReturn();
            // fixes bug with nested tables not shown
            // Paragraph#getChunks() doesn't contain the nested table element
            PdfPTable table = createInOneCell(paragraph.iterator());
            indentation.indentLeft -= paragraph.getIndentationLeft();
            indentation.indentRight -= paragraph.getIndentationRight();
            this.add((Element) table);
            indentation.indentLeft += paragraph.getIndentationLeft();
            indentation.indentRight += paragraph.getIndentationRight();
        } else {
            line.setExtraIndent(paragraph.getFirstLineIndent());
            paragraph.process(this);
            carriageReturn();
            addSpacing(paragraph.getSpacingAfter(), paragraph.getTotalLeading(),
                    paragraph.getFont());
        }

        if (pageEvent != null && !isSectionTitle) {
            pageEvent.onParagraphEnd(writer, this, indentTop() - currentHeight);
        }

        alignment = Element.ALIGN_LEFT;
        indentation.indentLeft -= paragraph.getIndentationLeft();
        indentation.indentRight -= paragraph.getIndentationRight();
        carriageReturn();
    }

    private void add(PdfChunk chunk) {
        // if there isn't a current line available, we make one
        if (line == null) {
            carriageReturn();
        }

        // we cast the element to a chunk
        // we try to add the chunk to the line, until we succeed
        {
            PdfChunk overflow;
            while ((overflow = line.add(chunk)) != null) {
                carriageReturn();
                chunk = overflow;
                chunk.trimFirstSpace();
            }
        }
        pageEmpty = false;
        if (chunk.isAttribute(Chunk.NEWPAGE)) {
            newPage();
        }
    }

    /**
     * Opens the document.
     * <p>
     * You have to open the document before you can begin to add content to the body of the
     * document.
     */
    @Override
    public void open() {
        if (!open) {
            open = true;
            writer.open();
            rootOutline = new PdfOutline(writer);
            currentOutline = rootOutline;
            try {
                initPage();
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }
    }

    /**
     * Closes the document.
     * <B>
     * Once all the content has been written in the body, you have to close the body. After that
     * nothing can be written to the body anymore.
     */
    @Override
    public void close() {
        if (close) {
            return;
        }
        try {
            newPage();
            PdfPageEvent pageEvent = writer.getPageEvent();
            if (pageEvent != null) {
                pageEvent.onCloseDocument(writer, this);
            }
            open = false;
            close = true;
            calculateOutlineCount();
            writeOutlines();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }

        writer.close();
    }

    /**
     * Makes a new page and sends it to the <CODE>PdfWriter</CODE>.
     */
    public void newPage() {
        lastElementType = -1;
        if (isPageEmpty()) {
            return;
        }
        if (!open || close) {
            throw new RuntimeException(
                    MessageLocalization.getComposedMessage("the.document.is.not.open"));
        }
        PdfPageEvent pageEvent = writer.getPageEvent();
        if (pageEvent != null) {
            pageEvent.onEndPage(writer, this);
        }

        // the following 2 lines were added by Pelikan Stephan
        indentation.imageIndentLeft = 0;
        indentation.imageIndentRight = 0;

        try {
            // we flush the arraylist with recently written lines
            flushLines();

            // we prepare the elements of the page dictionary

            // [U1] page size and rotation
            int rotation = getPageSize().getRotation();

            // [C10]
            if (writer.isPdfX()) {
                if (thisBoxSize.containsKey("art") && thisBoxSize.containsKey("trim")) {
                    throw new PdfXConformanceException(MessageLocalization.getComposedMessage(
                            "only.one.of.artbox.or.trimbox.can.exist.in.the.page"));
                }
                if (!thisBoxSize.containsKey("art") && !thisBoxSize.containsKey("trim")) {
                    if (thisBoxSize.containsKey("crop")) {
                        thisBoxSize.put("trim", thisBoxSize.get("crop"));
                    } else {
                        thisBoxSize.put("trim", new PdfRectangle(getPageSize(), getPageSize().getRotation()));
                    }
                }
            }

            // [M1]
            pageResources.addDefaultColorDiff(writer.getDefaultColorspace());
            PdfDictionary resources = pageResources.getResources();

            // we create the page dictionary

            PdfPage page = new PdfPage(new PdfRectangle(getPageSize(), rotation), thisBoxSize, resources,
                    rotation);
            page.put(PdfName.TABS, writer.getTabs());

            // we complete the page dictionary

            // [U3] page actions: transition, duration, additional actions
            if (this.transition != null) {
                page.put(PdfName.TRANS, this.transition.getTransitionDictionary());
                transition = null;
            }
            if (this.duration > 0) {
                page.put(PdfName.DUR, new PdfNumber(this.duration));
                duration = 0;
            }
            if (pageAA != null) {
                page.put(PdfName.AA, writer.addToBody(pageAA).getIndirectReference());
                pageAA = null;
            }

            // [U4] we add the thumbs
            if (thumb != null) {
                page.put(PdfName.THUMB, thumb);
                thumb = null;
            }

            // [U8] we check if the userunit is defined
            if (writer.getUserunit() > 0f) {
                page.put(PdfName.USERUNIT, new PdfNumber(writer.getUserunit()));
            }

            // [C5] and [C8] we add the annotations

            if (text.size() > textEmptySize) {
                text.endText();
            } else {
                text = null;
            }
            writer.add(page, new PdfContents(writer.getDirectContentUnder(), graphics, text,
                    writer.getDirectContent(), getPageSize()));
            // we initialize the new page
            initPage();
        } catch (DocumentException | IOException de) {
            // maybe this never happens, but it's better to check.
            throw new ExceptionConverter(de);
        }
    }

    /**
     * Initializes a page.
     * <p>
     * If the footer/header is set, it is printed.
     *
     * @throws DocumentException on error
     */
    private void initPage() throws DocumentException {
        // the pagenumber is incremented
        incrementPageNumber();

        // initialization of some page objects
        pageResources = new PageResources();

        writer.resetContent();
        graphics = new PdfContentByte(writer);
        text = new PdfContentByte(writer);
        text.reset();
        text.beginText();
        textEmptySize = text.size();

        imageEnd = -1;
        indentation.imageIndentRight = 0;
        indentation.imageIndentLeft = 0;
        indentation.indentBottom = 0;
        indentation.indentTop = 0;
        currentHeight = 0;

        // backgroundcolors, etc...
        thisBoxSize = new HashMap<>(boxSize);
        if (getPageSize().getBackgroundColor() != null
                || getPageSize().hasBorders()
                || getPageSize().getBorderColor() != null) {
            add(getPageSize());
        }

        float oldleading = leading;
        int oldAlignment = alignment;
        // we move to the left/top position of the page
        text.moveText(getDocumentLeft(), getDocumentTop());
        pageEmpty = true;
        leading = oldleading;
        alignment = oldAlignment;
        carriageReturn();

        PdfPageEvent pageEvent = writer.getPageEvent();
        if (pageEvent != null) {
            if (firstPageEvent) {
                pageEvent.onOpenDocument(writer, this);
            }
            pageEvent.onStartPage(writer, this);
        }
        firstPageEvent = false;
    }

    /**
     * Adds the current line to the list of lines and also adds an empty line.
     *
     * @throws DocumentException on error
     */
    private void newLine() throws DocumentException {
        lastElementType = -1;
        carriageReturn();
        if (lines != null && !lines.isEmpty()) {
            lines.add(line);
            currentHeight += line.height();
        }
        line = new PdfLine(indentLeft(), indentRight(), alignment, leading);
    }

//    Info Dictionary and Catalog

    /**
     * If the current line is not empty or null, it is added to the arraylist of lines and a new
     * empty line is added.
     */
    private void carriageReturn() {
        // the arraylist with lines may not be null
        if (lines == null) {
            lines = new ArrayList<>();
        }
        // If the current line is not null
        if (line != null) {
            // we check if the end of the page is reached (bugfix by Francois Gravel)
            if (currentHeight + line.height() + leading < indentTop() - indentBottom()) {
                // if so nonempty lines are added and the height is augmented
                if (line.size() > 0) {
                    currentHeight += line.height();
                    lines.add(line);
                    pageEmpty = false;
                }
            }
            // if the end of the line is reached, we start a new page
            else {
                newPage();
            }
        }
        if (imageEnd > -1 && currentHeight > imageEnd) {
            imageEnd = -1;
            indentation.imageIndentRight = 0;
            indentation.imageIndentLeft = 0;
        }
        // a new current line is constructed
        line = new PdfLine(indentLeft(), indentRight(), alignment, leading);
    }

    /**
     * Gets the current vertical page position.
     *
     * @param ensureNewLine Tells whether a new line shall be enforced. This may cause side effects
     *                      for elements that do not terminate the lines they've started because
     *                      those lines will get terminated.
     * @return The current vertical page position.
     */
    float getVerticalPosition(boolean ensureNewLine) {
        // ensuring that a new line has been started.
        if (ensureNewLine) {
            ensureNewLine();
        }
        return getDocumentTop() - currentHeight - indentation.indentTop;
    }

    /**
     * Ensures that a new line has been started.
     */
    private void ensureNewLine() {
        try {
            if ((lastElementType == Element.PHRASE) ||
                    (lastElementType == Element.CHUNK)) {
                newLine();
                flushLines();
            }
        } catch (DocumentException ex) {
            throw new ExceptionConverter(ex);
        }
    }

//    [C1] outlines

    /**
     * Writes all the lines to the text-object.
     *
     * @return the displacement that was caused
     * @throws DocumentException on error
     */
    private float flushLines() throws DocumentException {
        // checks if the ArrayList with the lines is not null
        if (lines == null) {
            return 0;
        }
        // checks if a new Line has to be made.
        if (line != null && line.size() > 0) {
            lines.add(line);
            line = new PdfLine(indentLeft(), indentRight(), alignment, leading);
        }

        // checks if the ArrayList with the lines is empty
        if (lines.isEmpty()) {
            return 0;
        }

        // initialization of some parameters
        Object[] currentValues = new Object[2];
        PdfFont currentFont = null;
        float displacement = 0;
        PdfLine l;
        currentValues[1] = 0.0F;
        // looping over all the lines
        for (PdfLine line1 : lines) {

            // this is a line in the loop
            l = line1;

            float moveTextX = l.indentLeft() - indentLeft() + indentation.indentLeft
                    + indentation.listIndentLeft + indentation.sectionIndentLeft;
            text.moveText(moveTextX, -l.height());
            // is the line preceded by a symbol?
            if (l.listSymbol() != null) {
                ColumnText.showTextAligned(graphics, Element.ALIGN_LEFT, new Phrase(l.listSymbol()),
                        text.getXTLM(), text.getYTLM(), 0);
            }

            currentValues[0] = currentFont;

            writeLineToContent(l, text, graphics, currentValues, writer.getSpaceCharRatio());

            currentFont = (PdfFont) currentValues[0];
            displacement += l.height();
            text.moveText(-moveTextX, 0);

        }
        lines = new ArrayList<>();
        return displacement;
    }

    /**
     * Writes a text line to the document. It takes care of all the attributes.
     * <p>
     * Before entering the line position must have been established and the
     * <CODE>text</CODE> argument must be in text object scope (<CODE>beginText()</CODE>).
     *
     * @param line          the line to be written
     * @param text          the <CODE>PdfContentByte</CODE> where the text will be written to
     * @param graphics      the <CODE>PdfContentByte</CODE> where the graphics will be written to
     * @param currentValues the current font and extra spacing values
     * @param ratio
     * @throws DocumentException on error
     */
    void writeLineToContent(PdfLine line, PdfContentByte text, PdfContentByte graphics,
            Object[] currentValues, float ratio) throws DocumentException {
        PdfFont currentFont = (PdfFont) (currentValues[0]);
        float lastBaseFactor = (Float) (currentValues[1]);
        PdfChunk chunk;
        int numberOfSpaces;
        int lineLen;
        boolean isJustified;
        float hangingCorrection = 0;
        float hScale = 1;
        float lastHScale = Float.NaN;
        float baseWordSpacing = 0;
        float baseCharacterSpacing = 0;
        float glueWidth = 0;

        numberOfSpaces = line.numberOfSpaces();
        lineLen = line.GetLineLengthUtf32();
        // does the line need to be justified?
        isJustified = line.hasToBeJustified() && (numberOfSpaces != 0 || lineLen > 1);
        int separatorCount = line.getSeparatorCount();
        if (separatorCount > 0) {
            glueWidth = line.widthLeft() / separatorCount;
        } else if (isJustified) {
            final float calc = lastBaseFactor * (ratio * numberOfSpaces + lineLen - 1);
            if (line.isNewlineSplit() && line.widthLeft() >= calc) {
                if (line.isRTL()) {
                    text.moveText(line.widthLeft() - calc, 0);
                }
                baseWordSpacing = ratio * lastBaseFactor;
                baseCharacterSpacing = lastBaseFactor;
            } else {
                float width = line.widthLeft();
                PdfChunk last = line.getChunk(line.size() - 1);
                if (last != null) {
                    String s = last.toString();
                    char c;
                    if (s.length() > 0
                            && hangingPunctuation.indexOf((c = s.charAt(s.length() - 1))) >= 0) {
                        float oldWidth = width;
                        width += last.font().width(c) * 0.4f;
                        hangingCorrection = width - oldWidth;
                    }
                }
                // if there's a single word on the line and we are using NO_SPACE_CHAR_RATIO,
                // we don't want any character spacing
                float baseFactor = (numberOfSpaces == 0 && ratio == PdfWriter.NO_SPACE_CHAR_RATIO)
                        ? 0f : width / (ratio * numberOfSpaces + lineLen - 1);
                baseWordSpacing = ratio * baseFactor;
                baseCharacterSpacing = baseFactor;
                lastBaseFactor = baseFactor;
            }
        }

        int lastChunkStroke = line.getLastStrokeChunk();
        int chunkStrokeIdx = 0;
        float xMarker = text.getXTLM();
        float baseXMarker = xMarker;
        float yMarker = text.getYTLM();
        boolean adjustMatrix = false;
        float tabPosition = 0;

        // looping over all the chunks in 1 line
        for (Iterator j = line.iterator(); j.hasNext(); ) {
            chunk = (PdfChunk) j.next();
            Color color = chunk.color();
            hScale = 1;

            if (chunkStrokeIdx <= lastChunkStroke) {
                float width;
                if (isJustified) {
                    width = chunk.getWidthCorrected(baseCharacterSpacing, baseWordSpacing);
                } else {
                    width = chunk.width();
                }
                if (chunk.isStroked()) {
                    PdfChunk nextChunk = line.getChunk(chunkStrokeIdx + 1);
                    if (chunk.isSeparator()) {
                        width = glueWidth;
                        Object[] sep = (Object[]) chunk.getAttribute(Chunk.SEPARATOR);
                        DrawInterface di = (DrawInterface) sep[0];
                        Boolean vertical = (Boolean) sep[1];
                        float fontSize = chunk.font().size();
                        float ascender = chunk.font().getFont()
                                .getFontDescriptor(BaseFont.ASCENT, fontSize);
                        float descender = chunk.font().getFont()
                                .getFontDescriptor(BaseFont.DESCENT, fontSize);
                        if (vertical) {
                            di.draw(graphics, baseXMarker, yMarker + descender,
                                    baseXMarker + line.getOriginalWidth(), ascender - descender,
                                    yMarker);
                        } else {
                            di.draw(graphics, xMarker, yMarker + descender, xMarker + width,
                                    ascender - descender, yMarker);
                        }
                    }
                    if (chunk.isTab()) {
                        Object[] tab = (Object[]) chunk.getAttribute(Chunk.TAB);
                        DrawInterface di = (DrawInterface) tab[0];
                        tabPosition = (Float) tab[1] + (Float) tab[3];
                        float fontSize = chunk.font().size();
                        float ascender = chunk.font().getFont()
                                .getFontDescriptor(BaseFont.ASCENT, fontSize);
                        float descender = chunk.font().getFont()
                                .getFontDescriptor(BaseFont.DESCENT, fontSize);
                        if (tabPosition > xMarker) {
                            di.draw(graphics, xMarker, yMarker + descender, tabPosition,
                                    ascender - descender, yMarker);
                        }
                        float tmp = xMarker;
                        xMarker = tabPosition;
                        tabPosition = tmp;
                    }
                    if (chunk.isAttribute(Chunk.BACKGROUND)) {
                        graphics.saveState();

                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.BACKGROUND)) {
                            subtract = 0;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        float fontSize = chunk.font().size();
                        float ascender = chunk.font().getFont()
                                .getFontDescriptor(BaseFont.ASCENT, fontSize);
                        float descender = chunk.font().getFont()
                                .getFontDescriptor(BaseFont.DESCENT, fontSize);
                        Object[] bgr = (Object[]) chunk.getAttribute(Chunk.BACKGROUND);

                        graphics.setColorFill((Color) bgr[0]);

                        float[] extra = (float[]) bgr[1];
                        graphics.rectangle(xMarker - extra[0],
                                yMarker + descender - extra[1] + chunk.getTextRise(),
                                width - subtract + extra[0] + extra[2],
                                ascender - descender + extra[1] + extra[3]);
                        graphics.fill();
                        graphics.setGrayFill(0);

                        graphics.restoreState();
                    }
                    if (chunk.isAttribute(Chunk.UNDERLINE)) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.UNDERLINE)) {
                            subtract = 0;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        Object[][] unders = (Object[][]) chunk.getAttribute(Chunk.UNDERLINE);
                        Color scolor = null;
                        for (Object[] obj : unders) {
                            scolor = (Color) obj[0];
                            float[] ps = (float[]) obj[1];
                            if (scolor == null) {
                                scolor = color;
                            }
                            if (scolor != null) {
                                graphics.setColorStroke(scolor);
                            }
                            float fsize = chunk.font().size();
                            graphics.setLineWidth(ps[0] + fsize * ps[1]);
                            float shift = ps[2] + fsize * ps[3];
                            int cap2 = (int) ps[4];
                            if (cap2 != 0) {
                                graphics.setLineCap(cap2);
                            }
                            graphics.moveTo(xMarker, yMarker + shift);
                            graphics.lineTo(xMarker + width - subtract, yMarker + shift);
                            graphics.stroke();
                            if (scolor != null) {
                                graphics.resetGrayStroke();
                            }
                            if (cap2 != 0) {
                                graphics.setLineCap(0);
                            }
                        }
                        graphics.setLineWidth(1);
                    }
                    if (chunk.isAttribute(Chunk.GENERICTAG)) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute(Chunk.GENERICTAG)) {
                            subtract = 0;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        Rectangle rect = new Rectangle(xMarker, yMarker, xMarker + width - subtract,
                                yMarker + chunk.font().size());
                        PdfPageEvent pev = writer.getPageEvent();
                        if (pev != null) {
                            pev.onGenericTag(writer, this, rect,
                                    (String) chunk.getAttribute(Chunk.GENERICTAG));
                        }
                    }
                    float[] params = (float[]) chunk.getAttribute(Chunk.SKEW);
                    Float hs = (Float) chunk.getAttribute(Chunk.HSCALE);
                    if (params != null || hs != null) {
                        float b = 0, c = 0;
                        if (params != null) {
                            b = params[0];
                            c = params[1];
                        }
                        if (hs != null) {
                            hScale = hs;
                        }
                        text.setTextMatrix(hScale, b, c, 1, xMarker, yMarker);
                    }
                    if (chunk.isAttribute(Chunk.CHAR_SPACING)) {
                        Float cs = (Float) chunk.getAttribute(Chunk.CHAR_SPACING);
                        text.setCharacterSpacing(cs);
                    }
                }
                xMarker += width;
                ++chunkStrokeIdx;
            }

            if (chunk.font().compareTo(currentFont) != 0) {
                currentFont = chunk.font();
                text.setFontAndSize(currentFont.getFont(), currentFont.size());
            }
            float rise = 0;
            Object[] textRender = (Object[]) chunk.getAttribute(Chunk.TEXTRENDERMODE);
            int tr = 0;
            float strokeWidth = 1;
            Color strokeColor = null;
            Float fr = (Float) chunk.getAttribute(Chunk.SUBSUPSCRIPT);
            if (textRender != null) {
                tr = (Integer) textRender[0] & 3;
                if (tr != PdfContentByte.TEXT_RENDER_MODE_FILL) {
                    text.setTextRenderingMode(tr);
                }
                if (tr == PdfContentByte.TEXT_RENDER_MODE_STROKE
                        || tr == PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE) {
                    strokeWidth = (Float) textRender[1];
                    if (strokeWidth != 1) {
                        text.setLineWidth(strokeWidth);
                    }
                    strokeColor = (Color) textRender[2];
                    if (strokeColor == null) {
                        strokeColor = color;
                    }
                    if (strokeColor != null) {
                        text.setColorStroke(strokeColor);
                    }
                }
            }
            if (fr != null) {
                rise = fr;
            }
            if (color != null) {
                text.setColorFill(color);
            }
            if (rise != 0) {
                text.setTextRise(rise);
            } else if (chunk.isHorizontalSeparator()) {
                PdfTextArray array = new PdfTextArray();
                array.add(-glueWidth * 1000f / chunk.font.size() / hScale);
                text.showText(array);
            } else if (chunk.isTab()) {
                PdfTextArray array = new PdfTextArray();
                array.add((tabPosition - xMarker) * 1000f / chunk.font.size() / hScale);
                text.showText(array);
            }
            // If it is a CJK chunk or Unicode TTF we will have to simulate the
            // space adjustment.
            else if (isJustified && numberOfSpaces > 0 && chunk.isSpecialEncoding()) {
                if (hScale != lastHScale) {
                    lastHScale = hScale;
                    text.setWordSpacing(baseWordSpacing / hScale);
                    text.setCharacterSpacing(
                            baseCharacterSpacing / hScale + text.getCharacterSpacing());
                }
                String s = chunk.toString();
                int idx = s.indexOf(' ');
                if (idx < 0) {
                    text.showText(s);
                } else {
                    float spaceCorrection = -baseWordSpacing * 1000f / chunk.font.size() / hScale;
                    PdfTextArray textArray = new PdfTextArray(s.substring(0, idx));
                    int lastIdx = idx;
                    while ((idx = s.indexOf(' ', lastIdx + 1)) >= 0) {
                        textArray.add(spaceCorrection);
                        textArray.add(s.substring(lastIdx, idx));
                        lastIdx = idx;
                    }
                    textArray.add(spaceCorrection);
                    textArray.add(s.substring(lastIdx));
                    text.showText(textArray);
                }
            } else {
                if (isJustified && hScale != lastHScale) {
                    lastHScale = hScale;
                    text.setWordSpacing(baseWordSpacing / hScale);
                    text.setCharacterSpacing(
                            baseCharacterSpacing / hScale + text.getCharacterSpacing());
                }
                text.showText(chunk.toString());
            }

            if (rise != 0) {
                text.setTextRise(0);
            }
            if (color != null) {
                text.resetRGBColorFill();
            }
            if (tr != PdfContentByte.TEXT_RENDER_MODE_FILL) {
                text.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
            }
            if (strokeColor != null) {
                text.resetRGBColorStroke();
            }
            if (strokeWidth != 1) {
                text.setLineWidth(1);
            }
            if (chunk.isAttribute(Chunk.SKEW) || chunk.isAttribute(Chunk.HSCALE)) {
                adjustMatrix = true;
                text.setTextMatrix(xMarker, yMarker);
            }
            if (chunk.isAttribute(Chunk.CHAR_SPACING)) {
                text.setCharacterSpacing(baseCharacterSpacing);
            }
        }
        if (isJustified) {
            text.setWordSpacing(0);
            text.setCharacterSpacing(0);
            if (line.isNewlineSplit()) {
                lastBaseFactor = 0;
            }
        }
        if (adjustMatrix) {
            text.moveText(baseXMarker - text.getXTLM(), 0);
        }
        currentValues[0] = currentFont;
        currentValues[1] = lastBaseFactor;
    }

    /**
     * Gets the indentation on the left side.
     *
     * @return a margin
     */

    private float indentLeft() {
        return getDocumentLeft(
                indentation.indentLeft + indentation.listIndentLeft + indentation.imageIndentLeft
                        + indentation.sectionIndentLeft);
    }

    /**
     * Gets the indentation on the right side.
     *
     * @return a margin
     */

    private float indentRight() {
        return getDocumentRight(indentation.indentRight + indentation.sectionIndentRight
                + indentation.imageIndentRight);
    }

    /**
     * Gets the indentation on the top side.
     *
     * @return a margin
     */

    private float indentTop() {
        return getDocumentTop(indentation.indentTop);
    }

    /**
     * Gets the indentation on the bottom side.
     *
     * @return a margin
     */

    private float indentBottom() {
        return getDocumentBottom(indentation.indentBottom);
    }

    /**
     * Adds extra space. This method should probably be rewritten.
     */
    private void addSpacing(float extraspace, float oldleading, Font f) {
        if (extraspace == 0) {
            return;
        }
        if (pageEmpty) {
            return;
        }
        if (currentHeight + line.height() + leading > indentTop() - indentBottom()) {
            return;
        }
        leading = extraspace;
        carriageReturn();
        if (f.isUnderlined() || f.isStrikethru()) {
            f = new Font(f);
            int style = f.getStyle();
            style &= ~Font.UNDERLINE;
            style &= ~Font.STRIKETHRU;
            f.setStyle(style);
        }
        Chunk space = new Chunk(" ", f);
        space.process(this);
        carriageReturn();
        leading = oldleading;
    }

    /**
     * Gets the <CODE>PdfInfo</CODE>-object.
     *
     * @return <CODE>PdfInfo</COPE>
     */

    PdfInfo getInfo() {
        return info;
    }

    /**
     * Gets the <CODE>PdfCatalog</CODE>-object.
     *
     * @param pages an indirect reference to this document pages
     * @return <CODE>PdfCatalog</CODE>
     */

    PdfCatalog getCatalog(PdfIndirectReference pages) {
        PdfCatalog catalog = new PdfCatalog(pages);

        // [C1] outlines
        if (rootOutline.getKids().size() > 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
            catalog.put(PdfName.OUTLINES, rootOutline.indirectReference());
        }

        // [C2] version
        writer.getPdfVersion().addToCatalog(catalog);

        return catalog;
    }

    /**
     * Updates the count in the outlines.
     */
    private void calculateOutlineCount() {
        if (rootOutline.getKids().size() == 0) {
            return;
        }
        traverseOutlineCount(rootOutline);
    }

    //    [U2] empty pages

    /**
     * Recursive method to update the count in the outlines.
     */
    private void traverseOutlineCount(PdfOutline outline) {
        java.util.List<PdfOutline> kids = outline.getKids();
        PdfOutline parent = outline.parent();
        if (kids.isEmpty()) {
            if (parent != null) {
                parent.setCount(parent.getCount() + 1);
            }
        } else {
            for (PdfOutline kid : kids) {
                traverseOutlineCount(kid);
            }
            if (parent != null) {
                if (outline.isOpen()) {
                    parent.setCount(outline.getCount() + parent.getCount() + 1);
                } else {
                    parent.setCount(parent.getCount() + 1);
                    outline.setCount(-outline.getCount());
                }
            }
        }
    }

    /**
     * Writes the outline tree to the body of the PDF document.
     */
    private void writeOutlines() throws IOException {
        if (rootOutline.getKids().size() == 0) {
            return;
        }
        outlineTree(rootOutline);
        writer.addToBody(rootOutline, rootOutline.indirectReference());
    }

    /**
     * Recursive method used to write outlines.
     */
    private void outlineTree(PdfOutline outline) throws IOException {
        outline.setIndirectReference(writer.getPdfIndirectReference());
        if (outline.parent() != null) {
            outline.put(PdfName.PARENT, outline.parent().indirectReference());
        }
        java.util.List<PdfOutline> kids = outline.getKids();
        int size = kids.size();
        for (PdfOutline kid1 : kids) {
            outlineTree(kid1);
        }
        for (int k = 0; k < size; ++k) {
            if (k > 0) {
                kids.get(k).put(PdfName.PREV, kids.get(k - 1).indirectReference());
            }
            if (k < size - 1) {
                kids.get(k).put(PdfName.NEXT, kids.get(k + 1).indirectReference());
            }
        }
        if (size > 0) {
            outline.put(PdfName.FIRST, kids.get(0).indirectReference());
            outline.put(PdfName.LAST, kids.get(size - 1).indirectReference());
        }
        for (PdfOutline kid : kids) {
            writer.addToBody(kid, kid.indirectReference());
        }
    }

    private boolean isPageEmpty() {
        return writer == null || (writer.getDirectContent().size() == 0
                && writer.getDirectContentUnder().size() == 0 && pageEmpty);
    }

    PageResources getPageResources() {
        return pageResources;
    }

    /**
     * Adds a <CODE>PdfPTable</CODE> to the document.
     *
     * @param ptable the <CODE>PdfPTable</CODE> to be added to the document.
     * @throws DocumentException on error
     */
    private void addPTable(PdfPTable ptable) throws DocumentException {
        ColumnText ct = new ColumnText(writer.getDirectContent());
        // if the table prefers to be on a single page, and it wouldn't
        //fit on the current page, start a new page.
        if (ptable.getKeepTogether() && !fitsPage(ptable, 0f) && currentHeight > 0) {
            newPage();
        }
        // add dummy paragraph if we aren't at the top of a page, so that
        // spacingBefore will be taken into account by ColumnText
        if (currentHeight > 0) {
            Paragraph p = new Paragraph();
            p.setLeading(0);
            ct.addElement(p);
        }
        ct.addElement(ptable);
        boolean he = ptable.isHeadersInEvent();
        ptable.setHeadersInEvent(true);
        int loop = 0;
        while (true) {
            ct.setSimpleColumn(indentLeft(), indentBottom(), indentRight(),
                    indentTop() - currentHeight);
            int status = ct.go();
            if ((status & ColumnText.NO_MORE_TEXT) != 0) {
                text.moveText(0, ct.getYLine() - indentTop() + currentHeight);
                currentHeight = indentTop() - ct.getYLine();
                break;
            }
            if (indentTop() - currentHeight == ct.getYLine()) {
                ++loop;
            } else {
                loop = 0;
            }
            if (loop == 3) {
                add((Element) new Paragraph("ERROR: Infinite table loop"));
                break;
            }
            newPage();
        }
        ptable.setHeadersInEvent(he);
    }

    /**
     * Checks if a <CODE>PdfPTable</CODE> fits the current page of the <CODE>PdfDocument</CODE>.
     *
     * @param table  the table that has to be checked
     * @param margin a certain margin
     * @return <CODE>true</CODE> if the <CODE>PdfPTable</CODE> fits the page, <CODE>false</CODE>
     * otherwise.
     */

    private boolean fitsPage(PdfPTable table, float margin) {
        if (!table.isLockedWidth()) {
            float totalWidth = (indentRight() - indentLeft()) * table.getWidthPercentage() / 100;
            table.setTotalWidth(totalWidth);
        }
        // ensuring that a new line has been started.
        ensureNewLine();
        return table.getTotalHeight() + ((currentHeight > 0) ? table.spacingBefore() : 0f)
                <= indentTop() - currentHeight - indentBottom() - margin;
    }

    /**
     * Checks if the document is open.
     *
     * @return <CODE>true</CODE> if the document is open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * <CODE>PdfInfo</CODE> is the PDF InfoDictionary.
     * <p>
     * A document's trailer may contain a reference to an Info dictionary that provides information
     * about the document. This optional dictionary may contain one or more keys, whose values
     * should be strings.<BR> This object is described in the 'Portable Document Format Reference
     * Manual version 1.3' section 6.10 (page 120-121)
     *
     * @since 2.0.8 (PdfDocument was package-private before)
     */

    static class PdfInfo extends PdfDictionary {

        /**
         * Construct a <CODE>PdfInfo</CODE>-object.
         */

        PdfInfo() {
            super();
        }

        /**
         * Adds the title of the document.
         *
         * @param title the title of the document
         */

        void addTitle(String title) {
            put(PdfName.TITLE, new PdfString(title, PdfObject.TEXT_UNICODE));
        }

        /**
         * Adds the subject to the document.
         *
         * @param subject the subject of the document
         */

        void addSubject(String subject) {
            put(PdfName.SUBJECT, new PdfString(subject, PdfObject.TEXT_UNICODE));
        }

        /**
         * Adds some keywords to the document.
         *
         * @param keywords the keywords of the document
         */

        void addKeywords(String keywords) {
            put(PdfName.KEYWORDS, new PdfString(keywords, PdfObject.TEXT_UNICODE));
        }

        /**
         * Adds the name of the author to the document.
         *
         * @param author the name of the author
         */

        void addAuthor(String author) {
            put(PdfName.AUTHOR, new PdfString(author, PdfObject.TEXT_UNICODE));
        }

        /**
         * Adds the name of the creator to the document.
         *
         * @param creator the name of the creator
         */

        void addCreator(String creator) {
            put(PdfName.CREATOR, new PdfString(creator, PdfObject.TEXT_UNICODE));
        }

        /**
         * Adds the name of the producer to the document.
         */
        void addProducer(final String producer) {
            put(PdfName.PRODUCER, new PdfString(producer, PdfObject.TEXT_UNICODE));
        }

        /**
         * Adds the date of creation to the document.
         */

        void addCreationDate() {
            PdfString date = new PdfDate();
            put(PdfName.CREATIONDATE, date);
            put(PdfName.MODDATE, date);
        }

    }

    /**
     * <CODE>PdfCatalog</CODE> is the PDF Catalog-object.
     * <p>
     * The Catalog is a dictionary that is the root node of the document. It contains a reference to
     * the tree of pages contained in the document, a reference to the tree of objects representing
     * the document's outline, a reference to the document's article threads, and the list of named
     * destinations. In addition, the Catalog indicates whether the document's outline or thumbnail
     * page images should be displayed automatically when the document is viewed and whether some
     * location other than the first page should be shown when the document is opened.<BR> In this
     * class however, only the reference to the tree of pages is implemented.<BR> This object is
     * described in the 'Portable Document Format Reference Manual version 1.3' section 6.2 (page
     * 67-71)
     */

    static class PdfCatalog extends PdfDictionary {

        /**
         * Constructs a <CODE>PdfCatalog</CODE>.
         *
         * @param pages an indirect reference to the root of the document's Pages tree.
         */
        PdfCatalog(PdfIndirectReference pages) {
            super(CATALOG);
            put(PdfName.PAGES, pages);
        }

    }

    /**
     * @since 2.0.8 (PdfDocument was package-private before)
     */
    private static class Indentation {

        /**
         * This represents the current indentation of the PDF Elements on the left side.
         */
        private float indentLeft = 0;

        /**
         * Indentation to the left caused by a section.
         */
        private float sectionIndentLeft = 0;

        /**
         * This represents the current indentation of the PDF Elements on the left side.
         */
        private float listIndentLeft = 0;

        /**
         * This is the indentation caused by an image on the left.
         */
        private float imageIndentLeft = 0;

        /**
         * This represents the current indentation of the PDF Elements on the right side.
         */
        private float indentRight = 0;

        /**
         * Indentation to the right caused by a section.
         */
        private float sectionIndentRight = 0;

        /**
         * This is the indentation caused by an image on the right.
         */
        private float imageIndentRight = 0;

        /**
         * This represents the current indentation of the PDF Elements on the top side.
         */
        private float indentTop = 0;

        /**
         * This represents the current indentation of the PDF Elements on the bottom side.
         */
        private float indentBottom = 0;
    }

}

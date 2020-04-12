/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text.pdf;

import com.justifiedsolutions.openpdf.text.Document;
import com.justifiedsolutions.openpdf.text.Paragraph;
import com.justifiedsolutions.openpdf.text.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * If you want to add more than one page event to a PdfWriter, you have to construct a
 * PdfPageEventForwarder, add the different events to this object and add the forwarder to the
 * PdfWriter.
 */
public class PdfPageEventForwarder implements PdfPageEvent {

    /**
     * ArrayList containing all the PageEvents that have to be executed.
     */
    private final List<PdfPageEvent> events = new ArrayList<>();

    /**
     * Add a page event to the forwarder.
     *
     * @param event an event that has to be added to the forwarder.
     */
    public void addPageEvent(PdfPageEvent event) {
        events.add(event);
    }

    /**
     * Called when the document is opened.
     *
     * @param writer   the <CODE>PdfWriter</CODE> for this document
     * @param document the document
     */
    public void onOpenDocument(PdfWriter writer, Document document) {
        events.forEach(event -> onOpenDocument(writer, document));
    }

    /**
     * Called when a page is initialized.
     * <p>
     * Note that if even if a page is not written this method is still called. It is preferable to
     * use <CODE>onEndPage</CODE> to avoid infinite loops.
     *
     * @param writer   the <CODE>PdfWriter</CODE> for this document
     * @param document the document
     */
    public void onStartPage(PdfWriter writer, Document document) {
        events.forEach(event -> onStartPage(writer, document));
    }

    /**
     * Called when a page is finished, just before being written to the document.
     *
     * @param writer   the <CODE>PdfWriter</CODE> for this document
     * @param document the document
     */
    public void onEndPage(PdfWriter writer, Document document) {
        events.forEach(event -> onEndPage(writer, document));
    }

    /**
     * Called when the document is closed.
     * <p>
     * Note that this method is called with the page number equal to the last page plus one.
     *
     * @param writer   the <CODE>PdfWriter</CODE> for this document
     * @param document the document
     */
    public void onCloseDocument(PdfWriter writer, Document document) {
        events.forEach(event -> onCloseDocument(writer, document));
    }

    /**
     * Called when a Paragraph is written.
     * <p>
     * <CODE>paragraphPosition</CODE> will hold the height at which the
     * paragraph will be written to. This is useful to insert bookmarks with more control.
     *
     * @param writer            the <CODE>PdfWriter</CODE> for this document
     * @param document          the document
     * @param paragraphPosition the position the paragraph will be written to
     */
    public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
        events.forEach(event -> onParagraph(writer, document, paragraphPosition));
    }

    /**
     * Called when a Paragraph is written.
     * <p>
     * <CODE>paragraphPosition</CODE> will hold the height of the end of the
     * paragraph.
     *
     * @param writer            the <CODE>PdfWriter</CODE> for this document
     * @param document          the document
     * @param paragraphPosition the position of the end of the paragraph
     */
    public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
        events.forEach(event -> onParagraphEnd(writer, document, paragraphPosition));
    }

    /**
     * Called when a Chapter is written.
     * <p>
     * <CODE>position</CODE> will hold the height at which the chapter will be
     * written to.
     *
     * @param writer            the <CODE>PdfWriter</CODE> for this document
     * @param document          the document
     * @param paragraphPosition the position the chapter will be written to
     * @param title             the title of the Chapter
     */
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition,
            Paragraph title) {
        events.forEach(event -> onChapter(writer, document, paragraphPosition, title));
    }

    /**
     * Called when the end of a Chapter is reached.
     * <p>
     * <CODE>position</CODE> will hold the height of the end of the chapter.
     *
     * @param writer   the <CODE>PdfWriter</CODE> for this document
     * @param document the document
     * @param position the position of the end of the chapter.
     */
    public void onChapterEnd(PdfWriter writer, Document document, float position) {
        events.forEach(event -> onChapterEnd(writer, document, position));
    }

    /**
     * Called when a Section is written.
     * <p>
     * <CODE>position</CODE> will hold the height at which the section will be
     * written to.
     *
     * @param writer            the <CODE>PdfWriter</CODE> for this document
     * @param document          the document
     * @param paragraphPosition the position the section will be written to
     * @param depth             the number depth of the Section
     * @param title             the title of the section
     */
    public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth,
            Paragraph title) {
        events.forEach(event -> onSection(writer, document, paragraphPosition, depth, title));
    }

    /**
     * Called when the end of a Section is reached.
     * <p>
     * <CODE>position</CODE> will hold the height of the section end.
     *
     * @param writer   the <CODE>PdfWriter</CODE> for this document
     * @param document the document
     * @param position the position of the end of the section
     */
    public void onSectionEnd(PdfWriter writer, Document document, float position) {
        events.forEach(event -> onSectionEnd(writer, document, position));
    }

    /**
     * Called when a <CODE>Chunk</CODE> with a generic tag is written.
     * <p>
     * It is useful to pinpoint the <CODE>Chunk</CODE> location to generate bookmarks, for example.
     *
     * @param writer   the <CODE>PdfWriter</CODE> for this document
     * @param document the document
     * @param rect     the <CODE>Rectangle</CODE> containing the <CODE>Chunk
     *                 </CODE>
     * @param text     the text of the tag
     */
    public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
        events.forEach(event -> onGenericTag(writer, document, rect, text));
    }
}
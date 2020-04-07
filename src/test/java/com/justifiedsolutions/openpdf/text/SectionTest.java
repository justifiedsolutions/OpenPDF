/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.justifiedsolutions.openpdf.pdf.Chapter;
import com.justifiedsolutions.openpdf.pdf.Document;
import com.justifiedsolutions.openpdf.pdf.DocumentException;
import com.justifiedsolutions.openpdf.pdf.Margin;
import com.justifiedsolutions.openpdf.pdf.PageSize;
import com.justifiedsolutions.openpdf.pdf.content.Chunk;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import com.justifiedsolutions.openpdf.pdf.content.Phrase;
import com.justifiedsolutions.openpdf.pdf.content.Table;
import com.justifiedsolutions.openpdf.text.pdf.PdfPRow;
import com.justifiedsolutions.openpdf.text.pdf.PdfPTable;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    public void addSectionNullParent() throws DocumentException {
        Document document = new Document(PageSize.LETTER, new Margin(10, 10, 10, 10));
        Chapter chapter = document.createChapter(new Paragraph());
        com.justifiedsolutions.openpdf.pdf.Section child = chapter
                .addSection(new Paragraph(new Chunk("title")));

        assertThrows(NullPointerException.class, () -> Section.addSection(null, child));
    }

    @Test
    public void addSectionNullChild() {
        com.justifiedsolutions.openpdf.text.Chapter parent = new com.justifiedsolutions.openpdf.text.Chapter(
                1);

        assertThrows(NullPointerException.class, () -> Section.addSection(parent, null));
    }

    @Test
    public void addSectionWithNumbers() throws DocumentException {
        com.justifiedsolutions.openpdf.text.Chapter parent = new com.justifiedsolutions.openpdf.text.Chapter(
                1);

        Document document = new Document(PageSize.LETTER, new Margin(10, 10, 10, 10));
        Chapter chapter = document.createChapter(new Paragraph());
        com.justifiedsolutions.openpdf.pdf.Section child = chapter
                .addSection(new Paragraph(new Chunk("title")));
        child.setDisplaySectionNumber(true);
        child.setStartsNewPage(true);

        Section.addSection(parent, child);

        Section actualChild = (Section) parent.get(0);
        assertEquals(2, actualChild.getTitle().getChunks().size());
        assertEquals("1.1 ", actualChild.getTitle().getChunks().get(0).getContent());
        assertEquals("title", actualChild.getTitle().getChunks().get(1).getContent());
        assertTrue(actualChild.isTriggerNewPage());
    }

    @Test
    public void addSectionNoNumbers() throws DocumentException {
        com.justifiedsolutions.openpdf.text.Chapter parent = new com.justifiedsolutions.openpdf.text.Chapter(
                1);

        Document document = new Document(PageSize.LETTER, new Margin(10, 10, 10, 10));
        Chapter chapter = document.createChapter(new Paragraph());
        com.justifiedsolutions.openpdf.pdf.Section child = chapter
                .addSection(new Paragraph(new Chunk("title")));
        child.setDisplaySectionNumber(false);
        child.setStartsNewPage(true);

        Section.addSection(parent, child);

        Section actualChild = (Section) parent.get(0);
        assertEquals(1, actualChild.getTitle().getChunks().size());
        assertEquals("title", actualChild.getTitle().getChunks().get(0).getContent());
        assertTrue(actualChild.isTriggerNewPage());
    }

    @Test
    public void addContentChunk() {
        Section section = new Section();
        Chunk content = new Chunk("text");
        Section.addContent(section, content);
        assertNotNull(section.getChunks());
        assertEquals(1, section.getChunks().size());
        assertEquals("text", section.getChunks().get(0).getContent());
    }

    @Test
    public void addContentTable() {
        Section section = new Section();
        Phrase phrase = new Phrase("text");
        Table content = new Table(1);
        content.createCell(phrase);
        Section.addContent(section, content);
        assertEquals(1, section.size());
        Element element = section.get(0);
        assertTrue(element instanceof PdfPTable);
        PdfPTable pdfTable = (PdfPTable) element;
        assertEquals(1, pdfTable.getRows().size());
        PdfPRow row = pdfTable.getRow(0);
        assertEquals(1, row.getCells().length);
    }

    @Test
    public void addContentParagraph() {
        Section section = new Section();
        Chunk chunk = new Chunk("text");
        Paragraph content = new Paragraph(chunk);
        Section.addContent(section, content);
        assertNotNull(section.getChunks());
        assertEquals(1, section.getChunks().size());
        assertEquals("text", section.getChunks().get(0).getContent());
    }

    @Test
    public void addContentPhrase() {
        Section section = new Section();
        Phrase content = new Phrase("text");
        Section.addContent(section, content);
        assertNotNull(section.getChunks());
        assertEquals(1, section.getChunks().size());
        assertEquals("text", section.getChunks().get(0).getContent());
    }
}
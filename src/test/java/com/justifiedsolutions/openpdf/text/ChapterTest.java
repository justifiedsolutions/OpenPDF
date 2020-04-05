/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.justifiedsolutions.openpdf.pdf.Document;
import com.justifiedsolutions.openpdf.pdf.DocumentException;
import com.justifiedsolutions.openpdf.pdf.Margin;
import com.justifiedsolutions.openpdf.pdf.PageSize;
import com.justifiedsolutions.openpdf.pdf.content.Chunk;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import org.junit.jupiter.api.Test;

public class ChapterTest {

    @Test
    public void getInstanceNull() {
        assertThrows(NullPointerException.class, () -> Chapter.getInstance(null));
    }

    @Test
    public void getInstanceWithNumbers() throws DocumentException {
        Document document = new Document(PageSize.LETTER, new Margin(10, 10, 10, 10));
        com.justifiedsolutions.openpdf.pdf.Chapter chapter = document
                .createChapter(new Paragraph(new Chunk("title")));
        chapter.setDisplaySectionNumber(true);

        Chapter actual = Chapter.getInstance(chapter);

        assertEquals(2, actual.getTitle().getChunks().size());
        assertEquals("1 ", actual.getTitle().getChunks().get(0).getContent());
        assertEquals("title", actual.getTitle().getChunks().get(1).getContent());
        assertTrue(actual.isTriggerNewPage());
    }

    @Test
    public void addSectionNoNumbers() throws DocumentException {
        Document document = new Document(PageSize.LETTER, new Margin(10, 10, 10, 10));
        com.justifiedsolutions.openpdf.pdf.Chapter chapter = document
                .createChapter(new Paragraph(new Chunk("title")));
        chapter.setDisplaySectionNumber(false);

        Chapter actual = Chapter.getInstance(chapter);

        assertEquals(1, actual.getTitle().getChunks().size());
        assertEquals("title", actual.getTitle().getChunks().get(0).getContent());
        assertTrue(actual.isTriggerNewPage());
    }
}
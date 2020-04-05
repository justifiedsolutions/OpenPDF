/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.justifiedsolutions.openpdf.pdf.content.Chunk;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DocumentTest {

    private Document document;

    @BeforeEach
    public void setup() {
        document = new Document(PageSize.LETTER, new Margin(10, 10, 10, 10));
    }

    @Test
    public void setMetadataAddAndRemove() {
        String expected = "text";
        Metadata metadata = Metadata.TITLE;
        document.setMetadata(metadata, expected);
        assertEquals(expected, document.getMetadata(Metadata.TITLE));
        document.setMetadata(metadata, null);
        assertNull(document.getMetadata(metadata));
    }

    @Test
    public void setMetadataNPE() {
        assertThrows(NullPointerException.class, () -> document.setMetadata(null, ""));
    }

    @Test
    public void getMetadataNPE() {
        assertThrows(NullPointerException.class, () -> document.getMetadata(null));
    }

    @Test
    public void addChapterAlreadyContent() throws DocumentException {
        document.addContent(new Chunk());
        Paragraph title = new Paragraph(new Chunk());
        assertThrows(DocumentException.class, () -> document.addChapter(title));
    }

    @Test
    public void addContentAlreadyChapter() throws DocumentException {
        document.addChapter(new Paragraph(new Chunk()));
        assertThrows(DocumentException.class, () -> document.addContent(new Chunk()));
    }
}
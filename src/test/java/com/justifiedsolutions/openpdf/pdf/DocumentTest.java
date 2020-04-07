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
import com.justifiedsolutions.openpdf.pdf.content.Table;
import java.io.FileOutputStream;
import java.io.IOException;
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
        document.add(new Chunk());
        Paragraph title = new Paragraph(new Chunk());
        assertThrows(DocumentException.class, () -> document.createChapter(title));
    }

    @Test
    public void addContentAlreadyChapter() throws DocumentException {
        document.createChapter(new Paragraph(new Chunk()));
        assertThrows(DocumentException.class, () -> document.add(new Chunk()));
    }

    public void output() throws DocumentException, IOException {
        Table table = new Table(1);
        table.setWidthPercentage(60);
        Paragraph paragraph = new Paragraph(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        paragraph.setLeading(20f);
        table.createCell(paragraph);
        document.add(table);
        FileOutputStream outputStream = new FileOutputStream("/Users/jason/Downloads/foo.pdf");
        document.write(outputStream);
    }


}
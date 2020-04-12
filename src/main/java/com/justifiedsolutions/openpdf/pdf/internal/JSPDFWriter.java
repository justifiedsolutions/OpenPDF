/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.internal;

import com.justifiedsolutions.openpdf.pdf.Chapter;
import com.justifiedsolutions.openpdf.pdf.Margin;
import com.justifiedsolutions.openpdf.pdf.Metadata;
import com.justifiedsolutions.openpdf.pdf.PageSize;
import com.justifiedsolutions.openpdf.pdf.content.Chunk;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import com.justifiedsolutions.openpdf.pdf.content.Phrase;
import com.justifiedsolutions.openpdf.pdf.content.*;
import com.justifiedsolutions.openpdf.text.*;
import com.justifiedsolutions.openpdf.text.pdf.PdfPTable;
import com.justifiedsolutions.openpdf.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;

public class JSPDFWriter {

    private final com.justifiedsolutions.openpdf.pdf.Document model;
    private final OutputStream outputStream;

    /**
     * Creates a new writer.
     *
     * @param model        the document model
     * @param outputStream the output stream to write to
     * @throws NullPointerException if either argument is <code>null</code>
     */
    public JSPDFWriter(com.justifiedsolutions.openpdf.pdf.Document model, OutputStream outputStream) {
        this.model = Objects.requireNonNull(model);
        this.outputStream = Objects.requireNonNull(outputStream);
    }

    /**
     * Writes the {@link com.justifiedsolutions.openpdf.pdf.Document} to the {@link OutputStream}.
     */
    public void write() {
        Document document = createDocument();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        pdfWriter.setPageEvent(new HeaderFooterHelper(model.getHeader(), model.getFooter()));
        addMetadata(document);
        document.open();
        if (model.hasChapters()) {
            for (Chapter chapter : model.getChapters()) {
                document.add(com.justifiedsolutions.openpdf.text.Chapter.getInstance(chapter));
            }
        } else if (model.hasContent()) {
            for (Content content : model.getContent()) {
                document.add(processContent(content));
            }
        }
        document.close();
    }

    private Document createDocument() {
        Rectangle pageSize = convertPageSize(model.getPageSize());
        Margin margin = model.getMargin();
        return new Document(pageSize, margin.getLeft(), margin.getRight(), margin.getTop(), margin.getBottom());
    }

    private Rectangle convertPageSize(PageSize pageSize) {
        return new RectangleReadOnly(pageSize.width(), pageSize.height());
    }

    private void addMetadata(Document document) {
        Map<Metadata, String> metadata = model.getMetadata();
        for (Metadata key : metadata.keySet()) {
            Meta meta = new Meta(key, metadata.get(key));
            document.add(meta);
        }
    }

    private Element processContent(Content content) {
        Element result = null;
        if (content instanceof Paragraph) {
            result = com.justifiedsolutions.openpdf.text.Paragraph.getInstance((Paragraph) content);
        } else if (content instanceof Phrase) {
            result = com.justifiedsolutions.openpdf.text.Phrase.getInstance((Phrase) content);
        } else if (content instanceof Chunk) {
            result = com.justifiedsolutions.openpdf.text.Chunk.getInstance((Chunk) content);
        } else if (content instanceof Table) {
            result = PdfPTable.getInstance((Table) content);
        }
        return result;
    }
}

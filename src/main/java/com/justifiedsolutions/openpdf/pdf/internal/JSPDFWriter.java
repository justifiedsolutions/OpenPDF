/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.internal;

import com.justifiedsolutions.openpdf.pdf.Chapter;
import com.justifiedsolutions.openpdf.pdf.Document;
import com.justifiedsolutions.openpdf.pdf.Footer;
import com.justifiedsolutions.openpdf.pdf.Header;
import com.justifiedsolutions.openpdf.pdf.Margin;
import com.justifiedsolutions.openpdf.pdf.Metadata;
import com.justifiedsolutions.openpdf.pdf.PageSize;
import com.justifiedsolutions.openpdf.pdf.content.Chunk;
import com.justifiedsolutions.openpdf.pdf.content.Content;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import com.justifiedsolutions.openpdf.pdf.content.Phrase;
import com.justifiedsolutions.openpdf.pdf.content.Table;
import com.justifiedsolutions.openpdf.text.Element;
import com.justifiedsolutions.openpdf.text.Meta;
import com.justifiedsolutions.openpdf.text.Rectangle;
import com.justifiedsolutions.openpdf.text.RectangleReadOnly;
import com.justifiedsolutions.openpdf.text.pdf.ColumnText;
import com.justifiedsolutions.openpdf.text.pdf.PdfContentByte;
import com.justifiedsolutions.openpdf.text.pdf.PdfPTable;
import com.justifiedsolutions.openpdf.text.pdf.PdfPageEventHelper;
import com.justifiedsolutions.openpdf.text.pdf.PdfWriter;
import java.io.OutputStream;
import java.util.Map;

public class JSPDFWriter {

    private final Document model;
    private final OutputStream outputStream;

    /**
     * Creates a new writer.
     *
     * @param model        the document model
     * @param outputStream the output stream to write to
     */
    public JSPDFWriter(Document model, OutputStream outputStream) {
        this.model = model;
        this.outputStream = outputStream;
    }

    /**
     * Writes the {@link Document} to the {@link OutputStream}.
     */
    public void write() {
        com.justifiedsolutions.openpdf.text.Document document = createDocument();
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

    private com.justifiedsolutions.openpdf.text.Document createDocument() {
        Rectangle pageSize = convertPageSize(model.getPageSize());
        Margin margin = model.getMargin();
        return new com.justifiedsolutions.openpdf.text.Document(pageSize, margin.getLeft(),
                margin.getRight(), margin.getTop(), margin.getBottom());
    }

    private Rectangle convertPageSize(PageSize pageSize) {
        com.justifiedsolutions.openpdf.pdf.Rectangle size = pageSize.size();
        return new RectangleReadOnly(size.getWidth(), size.getHeight());
    }

    private void addMetadata(com.justifiedsolutions.openpdf.text.Document document) {
        Map<Metadata, String> metadata = model.getMetadata();
        for (Metadata key : metadata.keySet()) {
            String keyString = key.value();
            Meta meta = new Meta(keyString, metadata.get(key));
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

    private static class HeaderFooterHelper extends PdfPageEventHelper {

        private final Header header;
        private final Footer footer;

        public HeaderFooterHelper(Header header, Footer footer) {
            this.header = header;
            this.footer = footer;
        }

        @Override
        public void onStartPage(PdfWriter writer,
                com.justifiedsolutions.openpdf.text.Document document) {
            if (header != null && header.isValidForPageNumber(document.getPageNumber())) {
                writeHeaderFooter(writer, document, header.getParagraph(document.getPageNumber()),
                        document.top() + 27);
            }
        }

        @Override
        public void onEndPage(PdfWriter writer,
                com.justifiedsolutions.openpdf.text.Document document) {
            if (footer != null && footer.isValidForPageNumber(document.getPageNumber())) {
                writeHeaderFooter(writer, document, footer.getParagraph(document.getPageNumber()),
                        document.bottom() - 27);
            }
        }

        private void writeHeaderFooter(PdfWriter writer, com.justifiedsolutions.openpdf.text.Document document,
                Paragraph modelParagraph, float y) {
            com.justifiedsolutions.openpdf.text.Paragraph paragraph = com.justifiedsolutions.openpdf.text.Paragraph
                    .getInstance(modelParagraph);
            float x;
            int alignment = paragraph.getAlignment();
            if (alignment == Element.ALIGN_LEFT) {
                x = document.left();
            } else if (alignment == Element.ALIGN_RIGHT) {
                x = document.right();
            } else {
                x = document.getPageSize().getRight() / 2.0f;
                alignment = Element.ALIGN_CENTER;
            }
            final PdfContentByte cb = writer.getDirectContent();
            ColumnText.showTextAligned(cb, alignment, paragraph, x, y, 0);
        }
    }
}

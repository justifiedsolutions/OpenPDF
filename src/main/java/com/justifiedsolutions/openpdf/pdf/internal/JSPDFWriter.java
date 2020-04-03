/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.internal;

import com.justifiedsolutions.openpdf.pdf.Chapter;
import com.justifiedsolutions.openpdf.pdf.Document;
import com.justifiedsolutions.openpdf.pdf.HorizontalAlignment;
import com.justifiedsolutions.openpdf.pdf.Margin;
import com.justifiedsolutions.openpdf.pdf.Metadata;
import com.justifiedsolutions.openpdf.pdf.PageSize;
import com.justifiedsolutions.openpdf.pdf.Section;
import com.justifiedsolutions.openpdf.pdf.content.Cell;
import com.justifiedsolutions.openpdf.pdf.content.Chunk;
import com.justifiedsolutions.openpdf.pdf.content.Content;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import com.justifiedsolutions.openpdf.pdf.content.Phrase;
import com.justifiedsolutions.openpdf.pdf.content.Table;
import com.justifiedsolutions.openpdf.pdf.font.Font;
import com.justifiedsolutions.openpdf.pdf.font.PDFFont;
import com.justifiedsolutions.openpdf.text.Element;
import com.justifiedsolutions.openpdf.text.FontFactory;
import com.justifiedsolutions.openpdf.text.Meta;
import com.justifiedsolutions.openpdf.text.Rectangle;
import com.justifiedsolutions.openpdf.text.RectangleReadOnly;
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
        PdfWriter.getInstance(document, outputStream);
        addMetadata(document);
        document.open();
        if (model.hasChapters()) {
            for (Chapter chapter : model.getChapters()) {
                document.add(processChapter(chapter));
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
        if (content instanceof Paragraph) {
            return processParagraph((Paragraph) content);
        }
        if (content instanceof Phrase) {
            return processPhrase((Phrase) content);
        }
        if (content instanceof Chunk) {
            return processChunk((Chunk) content);
        }
        if (content instanceof Table) {
            return processTable((Table) content);
        }
        throw new IllegalArgumentException("Invalid content type: " + content.getClass());
    }

    private com.justifiedsolutions.openpdf.text.Chapter processChapter(Chapter chapter) {
        com.justifiedsolutions.openpdf.text.Paragraph title = processParagraph(chapter.getTitle());
        com.justifiedsolutions.openpdf.text.Chapter result = new com.justifiedsolutions.openpdf.text.Chapter(
                title, chapter.getSectionNumber());
        result.setTriggerNewPage(true);
        result.setNumberStyle(
                com.justifiedsolutions.openpdf.text.Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
        for (Content content : chapter.getContent()) {
            result.add(processContent(content));
        }
        for (Section section : chapter.getSections()) {
            processSection(section, result);
        }
        return result;
    }

    private void processSection(Section section,
            com.justifiedsolutions.openpdf.text.Section parent) {
        com.justifiedsolutions.openpdf.text.Paragraph title = processParagraph(section.getTitle());
        com.justifiedsolutions.openpdf.text.Section result = parent.addSection(title);
        result.setTriggerNewPage(section.isStartsNewPage());
        result.setNumberStyle(
                com.justifiedsolutions.openpdf.text.Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
        for (Content content : section.getContent()) {
            result.add(processContent(content));
        }
        for (Section child : section.getSections()) {
            processSection(child, result);
        }
    }

    private com.justifiedsolutions.openpdf.text.Paragraph processParagraph(Paragraph paragraph) {
        com.justifiedsolutions.openpdf.text.Paragraph result = new com.justifiedsolutions.openpdf.text.Paragraph();
        result.setLeading(paragraph.getLeading());
        result.setFont(processFont(paragraph.getFont()));
        result.setIndentationLeft(paragraph.getLeftIndent());
        result.setIndentationRight(paragraph.getRightIndent());
        result.setFirstLineIndent(paragraph.getFirstLineIndent());
        result.setSpacingBefore(paragraph.getSpacingBefore());
        result.setSpacingAfter(paragraph.getSpacingAfter());
        result.setKeepTogether(paragraph.isKeepTogether());
        result.setAlignment(processAlignment(paragraph.getAlignment()));
        for (Content content : paragraph.getContent()) {
            result.add(processContent(content));
        }
        return result;
    }

    private com.justifiedsolutions.openpdf.text.Phrase processPhrase(Phrase phrase) {
        com.justifiedsolutions.openpdf.text.Phrase result = new com.justifiedsolutions.openpdf.text.Phrase();
        result.setLeading(phrase.getLeading());
        result.setFont(processFont(phrase.getFont()));
        for (Chunk chunk : phrase.getChunks()) {
            result.add(processChunk(chunk));
        }
        return result;
    }

    private com.justifiedsolutions.openpdf.text.Chunk processChunk(Chunk chunk) {
        com.justifiedsolutions.openpdf.text.Chunk result = new com.justifiedsolutions.openpdf.text.Chunk(
                chunk.getText());
        result.setFont(processFont(chunk.getFont()));
        return result;
    }

    private com.justifiedsolutions.openpdf.text.Table processTable(Table table) {
        return null;
    }

    private com.justifiedsolutions.openpdf.text.Cell processCell(Cell cell) {
        return null;
    }

    private com.justifiedsolutions.openpdf.text.Font processFont(Font font) {
        if (font instanceof PDFFont) {
            PDFFont pdfFont = (PDFFont) font;
            return FontFactory
                    .getFont(pdfFont.getName().getName(), pdfFont.getSize(), pdfFont.getColor());
        }
        return new com.justifiedsolutions.openpdf.text.Font();
    }

    private int processAlignment(HorizontalAlignment alignment) {
        int result = -1;
        switch (alignment) {
            case LEFT:
                result = 0;
                break;
            case CENTER:
                result = 1;
                break;
            case RIGHT:
                result = 2;
                break;
            case JUSTIFIED:
                result = 3;
                break;
        }
        return result;
    }

}

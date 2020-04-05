/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

import com.justifiedsolutions.openpdf.pdf.content.Content;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import com.justifiedsolutions.openpdf.pdf.internal.JSPDFWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a PDF document. A document can contain both {@link Metadata} and {@link Content}. It
 * initialized with both a {@link PageSize} and a {@link Margin}. A Document can contain either a
 * list of {@link Chapter}s or other {@link Content}, such as {@link Paragraph}s, but not both.
 */
public class Document {

    private final PageSize pageSize;
    private final Margin margin;
    private final Map<Metadata, String> metadata = new HashMap<>();
    private final List<Chapter> chapters = new ArrayList<>();
    private final List<Content> content = new ArrayList<>();
    private Header header;
    private Footer footer;

    public Document(PageSize pageSize, Margin margin) {
        this.pageSize = pageSize;
        this.margin = margin;
    }

    /**
     * Returns the {@link PageSize} of the document.
     *
     * @return the PageSize.
     */
    public PageSize getPageSize() {
        return pageSize;
    }

    /**
     * Returns the document {@link Margin}
     *
     * @return the margin
     */
    public Margin getMargin() {
        return margin;
    }

    /**
     * Sets the specific piece of metadata. A <code>null</code> value will remove the metadata from
     * the document.
     *
     * @param metadata the metadata to set
     * @param value    the value of the metadata. <code>null</code> removes the metadata
     * @throws NullPointerException if metadata is <code>null</code>
     */
    public void setMetadata(Metadata metadata, String value) {
        Objects.requireNonNull(metadata);
        if (value != null) {
            this.metadata.put(metadata, value);
        } else {
            this.metadata.remove(metadata);
        }
    }

    /**
     * Gets the metadata value.
     *
     * @param metadata the metadata to get
     * @return the value of the metadata. <code>null</code> means there is no value for that @{@link
     * Metadata}
     * @throws NullPointerException if metadata is <code>null</code>
     */
    public String getMetadata(Metadata metadata) {
        Objects.requireNonNull(metadata);
        return this.metadata.get(metadata);
    }

    /**
     * Gets a {@link Collections#unmodifiableMap(Map)} of the metadata.
     *
     * @return the metadata
     */
    public Map<Metadata, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    /**
     * Gets the {@link Header} for the document.
     *
     * @return the document header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Sets the {@link Header} for the document.
     *
     * @param header the header, <code>null</code> will remove the header
     */
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * Gets the {@link Footer} for the document.
     *
     * @return the document footer
     */
    public Footer getFooter() {
        return footer;
    }

    /**
     * Sets the {@link Footer} for the document.
     *
     * @param footer the footer, <code>null</code> will remove the footer
     */
    public void setFooter(Footer footer) {
        this.footer = footer;
    }

    /**
     * Specifies if the Document has any {@link Chapter}s.
     *
     * @return true if there is a Chapter in the Document
     */
    public boolean hasChapters() {
        return !chapters.isEmpty();
    }

    /**
     * Gets a {@link Collections#unmodifiableList(List)} of the chapters.
     *
     * @return the chapters
     */
    public List<Chapter> getChapters() {
        return Collections.unmodifiableList(chapters);
    }

    /**
     * Creates a {@link Chapter} and adds it to the Document.
     *
     * @param title the title for the Chapter
     * @throws DocumentException    if other {@link Content} has already been added to the Document
     * @throws NullPointerException if title is <code>null</code>
     */
    public Chapter createChapter(Paragraph title) throws DocumentException {
        if (hasContent()) {
            throw new DocumentException(
                    "Unable to create Chapter with Content already added to Document.");
        }
        Objects.requireNonNull(title);
        int num = chapters.size() + 1;
        Chapter chapter = new Chapter(num, title);
        chapters.add(chapter);
        return chapter;
    }

    /**
     * Specifies if the Document has any {@link Content}.
     *
     * @return true if there is Content in the Document
     */
    public boolean hasContent() {
        return !content.isEmpty();
    }

    /**
     * Gets a {@link Collections#unmodifiableList(List)} of the content.
     *
     * @return the content.
     */
    public List<Content> getContent() {
        return Collections.unmodifiableList(content);
    }

    /**
     * Adds {@link Content} to the document.
     *
     * @param content the content to add
     * @throws DocumentException    if {@link Chapter}s have already been added to the Document
     * @throws NullPointerException if content is <code>null</code>
     */
    public void add(Content content) throws DocumentException {
        if (hasChapters()) {
            throw new DocumentException(
                    "Unable to add Content with Chapters already added to Document.");
        }
        Objects.requireNonNull(content);
        this.content.add(content);
    }

    /**
     * Writes the contents of the Document to the specified {@link OutputStream}.
     *
     * @param out the OutputStream to write the PDF to
     * @throws IOException if there is an issue writing the stream
     */
    public void write(OutputStream out) throws IOException {
        JSPDFWriter writer = new JSPDFWriter(this, out);
        writer.write();
    }
}

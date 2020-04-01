/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

import com.justifiedsolutions.openpdf.pdf.content.Chapter;
import com.justifiedsolutions.openpdf.pdf.content.Content;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a PDF document. A document can contain both {@link Metadata} and {@link Content}. It
 * initialized with both a {@link PageSize} and a {@link Margin}. A Document can contain either a
 * list of {@link Chapter}s or other {@link Content}, such as {@link Paragraph}s, but not both.
 */
public interface Document {

    /**
     * Returns the {@link PageSize} of the document.
     *
     * @return the PageSize.
     */
    PageSize getPageSize();

    /**
     * Returns the document {@link Margin}
     *
     * @return the margin
     */
    Margin getMargin();

    /**
     * Sets the specific piece of metadata. A <code>null</code> value will remove the metadata from
     * the document.
     *
     * @param metadata the metadata to set
     * @param value    the value of the metadata. <code>null</code> removes the metadata
     */
    void setMetadata(Metadata metadata, String value);

    /**
     * Gets the metadata value.
     *
     * @param metadata the metadata to get
     * @return the value of the metadata. <code>null</code> means there is no value for that @{@link
     * Metadata}
     */
    String getMetadata(Metadata metadata);

    /**
     * Gets the {@link Header} for the document.
     *
     * @return the document header
     */
    Header getHeader();

    /**
     * Sets the {@link Header} for the document.
     *
     * @param header the header, <code>null</code> will remove the header
     */
    void setHeader(Header header);

    /**
     * Gets the {@link Footer} for the document.
     *
     * @return the document footer
     */
    Footer getFooter();

    /**
     * Sets the {@link Footer} for the document.
     *
     * @param footer the footer, <code>null</code> will remove the footer
     */
    void setFooter(Footer footer);

    /**
     * Creates a {@link Chapter} and adds it to the Document.
     *
     * @param title the title for the Chapter
     * @throws DocumentException if other {@link Content} has already been added to the Document
     */
    void addChapter(Paragraph title) throws DocumentException;

    /**
     * Adds {@link Content} to the document.
     *
     * @param content the content to add
     * @throws DocumentException if {@link Chapter}s have already been added to the Document
     */
    void addContent(Content content) throws DocumentException;

    /**
     * Writes the contents of the Document to the specified {@link OutputStream}.
     *
     * @param out the OutputStream to write the PDF to
     * @throws IOException if there is an issue writing the stream
     */
    void write(OutputStream out) throws IOException;
}

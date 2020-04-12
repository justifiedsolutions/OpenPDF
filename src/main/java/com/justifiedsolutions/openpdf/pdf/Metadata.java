/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

/**
 * Enumerates the supported metadata fields in a PDF document.
 *
 * @see "PDF 32000-1:2008, Table 317"
 */
public enum Metadata {
    /**
     * Title of the document.
     */
    TITLE,
    /**
     * The subject of the document.
     */
    SUBJECT,
    /**
     * Keywords associated with the document.
     */
    KEYWORDS,
    /**
     * The name of the person who created the document.
     */
    AUTHOR,
    /**
     * If the document was converted to PDF from another format, the name of the conforming product that created the
     * original document from which it was converted.
     */
    CREATOR,
    /**
     * If the document was converted to PDF from another format, the name of the conforming product that converted it to
     * PDF.
     */
    PRODUCER,
    /**
     * The date and time the document was created, in human-readable form. The value of this field is ignored and
     * replaced with a date generated at the time the file is being written.
     */
    CREATE_DATE
}

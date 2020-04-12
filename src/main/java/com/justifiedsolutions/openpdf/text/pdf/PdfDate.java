/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */
package com.justifiedsolutions.openpdf.text.pdf;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A PdfDate represents the current date/time in the local time zone at the time of object creation.
 * The PdfDate is stored as a string formatted in accordance with the PDF specification.
 *
 * @see "PDF 32000-1:2008, Section 7.9.4"
 */
public class PdfDate extends PdfString {

    public PdfDate() {
        super(createPdfDateString(ZonedDateTime.now()));
    }

    /**
     * Creates a string in the format <code>D:YYYYMMDDHHmmSSOHH'mm</code>.
     *
     * @param dateTime the time to convert
     * @return the formatted string
     */
    static String createPdfDateString(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'D:'yyyyMMddHHmmssXX");
        String result = dateTime.format(formatter);
        if (!result.endsWith("Z")) {
            int index = result.length() - 2;
            result = result.substring(0, index) + "'" + result.substring(index);
        }
        return result;
    }

}
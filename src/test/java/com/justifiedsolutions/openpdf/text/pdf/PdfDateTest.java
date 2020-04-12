/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

public class PdfDateTest {

    @Test
    public void createPdfDateStringEpochUTC() {
        String expected = "D:19700101000000Z";
        ZonedDateTime input = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
        assertEquals(expected, PdfDate.createPdfDateString(input));
    }

    @Test
    public void createPdfDateStringZMinus6() {
        String expected = "D:19770124203800-06'00";
        ZonedDateTime input = ZonedDateTime.of(1977, 1, 24, 20, 38, 0, 0, ZoneOffset.of("-6"));
        assertEquals(expected, PdfDate.createPdfDateString(input));
    }

    @Test
    public void createPdfDateStringZPlus0630() {
        String expected = "D:19770124203800+06'30";
        ZonedDateTime input = ZonedDateTime.of(1977, 1, 24, 20, 38, 0, 0, ZoneOffset.of("+0630"));
        assertEquals(expected, PdfDate.createPdfDateString(input));
    }
}
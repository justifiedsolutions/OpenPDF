/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.font;

import java.awt.Color;
import java.util.Objects;

/**
 * Represents the 14 fonts native to a PDF document.
 */
public class PDFFont implements Font {

    /**
     * The default font name.
     */
    public static final FontName DEFAULT_NAME = FontName.HELVETICA;
    /**
     * The default font size.
     */
    public static final float DEFAULT_SIZE = 12f;
    /**
     * The default font color.
     */
    public static final Color DEFAULT_COLOR = Color.BLACK;

    private final FontName name;
    private final float size;
    private final Color color;

    /**
     * Creates the default PDFFont.
     */
    public PDFFont() {
        this(DEFAULT_NAME, DEFAULT_SIZE, DEFAULT_COLOR);
    }

    /**
     * Creates a PDFFont with the specified name with the default size and color.
     *
     * @param name the font name
     */
    public PDFFont(FontName name) {
        this(name, DEFAULT_SIZE, DEFAULT_COLOR);
    }

    /**
     * Creates a PDFFont with the specified name and size with the default color.
     *
     * @param name the font name
     * @param size the font size
     */
    public PDFFont(FontName name, float size) {
        this(name, size, DEFAULT_COLOR);
    }

    /**
     * Creates a PDFFont with the specified name, size, and color.
     *
     * @param name  the font name
     * @param size  the font size
     * @param color the font color
     */
    public PDFFont(FontName name, float size, Color color) {
        this.name = Objects.requireNonNull(name);
        this.size = size;
        this.color = Objects.requireNonNull(color);
    }

    /**
     * Gets the {@link FontName} of the font.
     *
     * @return the font name
     */
    public FontName getName() {
        return name;
    }

    /**
     * Gets the size of the Font.
     *
     * @return the font size
     */
    public float getSize() {
        return size;
    }

    /**
     * Gets the {@link Color} of the Font.
     *
     * @return the font color
     */
    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PDFFont pdfFont = (PDFFont) o;
        return Float.compare(pdfFont.size, size) == 0 &&
                name == pdfFont.name &&
                color.equals(pdfFont.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size, color);
    }

    /**
     * Represents the 14 font names native to a PDF document.
     */
    public enum FontName {
        COURIER("Courier"),
        COURIER_BOLD("Courier-Bold"),
        COURIER_OBLIQUE("Courier-Oblique"),
        COURIER_BOLD_OBLIQUE("Courier-BoldOblique"),
        HELVETICA("Helvetica"),
        HELVETICA_BOLD("Helvetica-Bold"),
        HELVETICA_OBLIQUE("Helvetica-Oblique"),
        HELVETICA_BOLD_OBLIQUE("Helvetica-BoldOblique"),
        TIMES_ROMAN("Times-Roman"),
        TIMES_BOLD("Times-Bold"),
        TIMES_ITALIC("Times-Italic"),
        TIMES_BOLD_ITALIC("Times-BoldItalic"),
        SYMBOL("Symbol"),
        ZAPFDINGBATS("ZapfDingbats");

        private final String name;

        FontName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

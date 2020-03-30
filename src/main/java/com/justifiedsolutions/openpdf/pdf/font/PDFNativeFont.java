package com.justifiedsolutions.openpdf.pdf.font;

import java.awt.Color;
import java.util.Objects;

/**
 * Represents the fonts native to a PDF document.
 */
public class PDFNativeFont implements Font {

    /**
     * The default font family is Helvetica.
     */
    public static final FontFamily DEFAULT_FONT_FAMILY = FontFamily.HELVETICA;
    /**
     * The default font style is normal (none).
     */
    public static final FontStyle DEFAULT_FONT_STYLE = FontStyle.NORMAL;
    /**
     * The default font size is 12.0f.
     */
    public static final float DEFAULT_FONT_SIZE = 12.0f;
    /**
     * The default font decoration is none.
     */
    public static final FontDecoration DEFAULT_FONT_DECORATION = FontDecoration.NONE;
    /**
     * The default font color is black.
     */
    public static final Color DEFAULT_COLOR = Color.BLACK;
    /**
     * The default font.
     */
    public static final Font DEFAULT_FONT = new PDFNativeFont();

    private final FontFamily family;
    private final FontStyle style;
    private final float size;
    private final FontDecoration decoration;
    private final Color color;

    private PDFNativeFont() {
        this(DEFAULT_FONT_FAMILY, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE, DEFAULT_FONT_DECORATION,
                DEFAULT_COLOR);
    }

    /**
     * Creates a new instances of a native PDF font.
     *
     * @param family     the font family
     * @param style      the font style
     * @param size       the font size
     * @param decoration the font decoration
     * @param color      the font color
     */
    public PDFNativeFont(FontFamily family, FontStyle style, float size, FontDecoration decoration,
            Color color) {
        this.family = Objects.requireNonNull(family);
        this.style = Objects.requireNonNull(style);
        this.size = size;
        this.decoration = Objects.requireNonNull(decoration);
        this.color = Objects.requireNonNull(color);
    }

    /**
     * Gets the {@link FontFamily} of the Font.
     *
     * @return the FontFamily
     */
    public FontFamily getFamily() {
        return family;
    }

    /**
     * Gets the {@link FontStyle} of the Font.
     *
     * @return the FontStyle
     */
    public FontStyle getStyle() {
        return style;
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
     * Gets the {@link FontDecoration} of the Font.
     *
     * @return the FontDecoration
     */
    public FontDecoration getDecoration() {
        return decoration;
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
        PDFNativeFont that = (PDFNativeFont) o;
        return Float.compare(that.size, size) == 0 &&
                family == that.family &&
                style == that.style &&
                decoration == that.decoration &&
                color.equals(that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(family, style, size, decoration, color);
    }
}

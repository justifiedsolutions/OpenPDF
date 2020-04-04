/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.content;

import com.justifiedsolutions.openpdf.pdf.HorizontalAlignment;
import com.justifiedsolutions.openpdf.pdf.VerticalAlignment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A Cell represents a cell in a {@link Table}. A Cell is created by methods on the Table and must
 * be instantiated with the content embedded in the Cell.
 */
public class Cell {

    private final List<Border> borders = new ArrayList<>();
    private Content content;
    private int rowSpan = 1;
    private int columnSpan = 1;
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    private VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
    private float minimumHeight = 0;
    private float paddingTop = 2;
    private float paddingBottom = 2;
    private float paddingLeft = 2;
    private float paddingRight = 2;
    private float greyFill = 1;

    /**
     * Creates a cell devoid of content.
     */
    Cell() {
        borders.add(Border.ALL);
    }

    /**
     * Creates a new Cell with the specified content. The {@link Content} must be a {@link Phrase}
     * or a {@link Paragraph}.
     *
     * @param content the cell content
     * @throws IllegalArgumentException if the content isn't the correct type.
     */
    Cell(Content content) {
        this();
        setContent(content);
    }

    /**
     * Gets the content of the Cell.
     *
     * @return the content
     */
    public Content getContent() {
        return content;
    }

    /**
     * Sets the content of the Cell. The {@link Content} must be a {@link Phrase}
     * or a {@link Paragraph}.
     *
     * @param content the cell content
     * @throws IllegalArgumentException if the content isn't the correct type.
     */
    public void setContent(Content content) {
        if ((content instanceof Phrase) || (content instanceof Paragraph)) {
            this.content = content;
        } else {
            throw new IllegalArgumentException("Invalid content type: " + content.getClass());
        }
    }

    /**
     * Gets the number of rows high this cell is. The default is 1.
     *
     * @return the row span
     */
    public int getRowSpan() {
        return rowSpan;
    }

    /**
     * Sets the number of rows high the cell is.
     *
     * @param rowSpan the row span
     */
    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    /**
     * Gets the number of columns wide the cell is. The default is 1.
     *
     * @return the column span
     */
    public int getColumnSpan() {
        return columnSpan;
    }

    /**
     * Sets the number of columns wide the cell is.
     *
     * @param columnSpan the column span
     */
    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
    }

    /**
     * Gets the {@link HorizontalAlignment} of the contents of the cell. The default is {@link
     * HorizontalAlignment#LEFT}.
     *
     * @return the horizontal alignment
     */
    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Sets the {@link HorizontalAlignment} of the contents of the cell.
     *
     * @param horizontalAlignment the horizontal alignment
     */
    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    /**
     * Gets the {@link VerticalAlignment} of the contents of the cell. The default is {@link
     * VerticalAlignment#TOP}.
     *
     * @return the vertical alignment
     */
    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the {@link VerticalAlignment} of the contents of the cell.
     *
     * @param verticalAlignment the vertical alignment
     */
    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    /**
     * Gets the minimum height of the cell. The default value is 0.
     *
     * @return the minimum height
     */
    public float getMinimumHeight() {
        return minimumHeight;
    }

    /**
     * Sets the minimum height of the cell.
     *
     * @param minimumHeight the minimum height
     */
    public void setMinimumHeight(float minimumHeight) {
        this.minimumHeight = minimumHeight;
    }

    /**
     * Sets the padding for all four sides of the cell. This is a convenience method for setting all
     * four <code>set*Padding</code> methods individually.
     *
     * @param padding the amount of padding
     */
    public void setPadding(float padding) {
        setPaddingTop(padding);
        setPaddingBottom(padding);
        setPaddingLeft(padding);
        setPaddingRight(padding);
    }

    /**
     * Gets the padding for the top of the cell. The default value is 2.
     *
     * @return the top padding
     */
    public float getPaddingTop() {
        return paddingTop;
    }

    /**
     * Sets the padding for the top of the cell.
     *
     * @param paddingTop top padding
     */
    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }

    /**
     * Gets the padding for the bottom of the cell. The default value is 2.
     *
     * @return the bottom padding
     */
    public float getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * Sets the padding for the bottom of the cell.
     *
     * @param paddingBottom bottom padding
     */
    public void setPaddingBottom(float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    /**
     * Gets the padding for the left side of the cell. The default value is 2.
     *
     * @return the left padding
     */
    public float getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Sets the padding for the left side of the cell.
     *
     * @param paddingLeft left padding
     */
    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    /**
     * Gets the padding for the right side of the cell. The default value is 2.
     *
     * @return the right padding
     */
    public float getPaddingRight() {
        return paddingRight;
    }

    /**
     * Sets the padding for the right side of the cell.
     *
     * @param paddingRight right padding
     */
    public void setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
    }

    /**
     * Gets the {@link Collections#unmodifiableList(List)} of {@link Border}s configured for the
     * cell. The default is {@link Border#ALL}.
     *
     * @return the cell borders
     */
    public List<Border> getBorders() {
        return Collections.unmodifiableList(borders);
    }

    /**
     * Sets the {@link Border}s of the Cell.
     *
     * @param borders the borders
     */
    public void setBorders(Border... borders) {
        this.borders.clear();
        if (borders != null) {
            this.borders.addAll(Arrays.asList(borders));
        }
    }

    /**
     * Gets the grey fill for the cell. The values range from 0.0 (Black) to 1.0 (White). The
     * default value is 1.0 (White).
     *
     * @return the grey fill
     */
    public float getGreyFill() {
        return greyFill;
    }

    /**
     * Sets the grey fill for the cell. The values range from 0.0 (Black) to 1.0 (White).
     *
     * @param greyFill the grey fill
     * @throws IllegalArgumentException if the value is less than 0.0 or greater than 1.0.
     */
    public void setGreyFill(float greyFill) {
        this.greyFill = greyFill;
    }


    /**
     * A Border represents the sides of the {@link Cell} that should have a border.
     */
    public enum Border {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        ALL
    }
}

/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.content;

import com.justifiedsolutions.openpdf.pdf.HorizontalAlignment;
import com.justifiedsolutions.openpdf.pdf.VerticalAlignment;
import java.util.List;

/**
 * A Cell represents a cell in a {@link Table}. A Cell is created by methods on the Table and must
 * be instantiated with the content embedded in the Cell.
 */
public interface Cell {

    /**
     * Gets the content of the Cell.
     *
     * @return the content
     */
    Phrase getContent();

    /**
     * Gets the number of rows high this cell is. The default is 1.
     *
     * @return the row span
     */
    int getRowSpan();

    /**
     * Sets the number of rows high the cell is.
     *
     * @param rowSpan the row span
     */
    void setRowSpan(int rowSpan);

    /**
     * Gets the number of columns wide the cell is. The default is 1.
     *
     * @return the column span
     */
    int getColumnSpan();

    /**
     * Sets the number of columns wide the cell is.
     *
     * @param columnSpan the column span
     */
    void setColumnSpan(int columnSpan);

    /**
     * Gets the {@link HorizontalAlignment} of the contents of the cell. The default is {@link
     * HorizontalAlignment#LEFT}.
     *
     * @return the horizontal alignment
     */
    HorizontalAlignment getAlignmentHorizontal();

    /**
     * Sets the {@link HorizontalAlignment} of the contents of the cell.
     *
     * @param alignment the horizontal alignment
     */
    void setAlignmentHorizontal(HorizontalAlignment alignment);

    /**
     * Gets the {@link VerticalAlignment} of the contents of the cell. The default is {@link
     * VerticalAlignment#TOP}.
     *
     * @return the vertical alignment
     */
    VerticalAlignment getAlignmentVertical();

    /**
     * Sets the {@link VerticalAlignment} of the contents of the cell.
     *
     * @param alignment the vertical alignment
     */
    void setAlignmentVertical(VerticalAlignment alignment);

    /**
     * Gets the minimum height of the cell. The default value is 0.
     *
     * @return the minimum height
     */
    float getMinimumHeight();

    /**
     * Sets the minimum height of the cell.
     *
     * @param minimumHeight the minimum height
     */
    void setMinimumHeight(float minimumHeight);

    /**
     * Sets the padding for all four sides of the cell. This is a convenience method for setting all
     * four <code>set*Padding</code> methods individually.
     *
     * @param padding the amount of padding
     */
    void setPadding(float padding);

    /**
     * Gets the padding for the top of the cell. The default value is 2.
     *
     * @return the top padding
     */
    float getPaddingTop();

    /**
     * Sets the padding for the top of the cell.
     *
     * @param padding top padding
     */
    void setPaddingTop(float padding);

    /**
     * Gets the padding for the bottom of the cell. The default value is 2.
     *
     * @return the bottom padding
     */
    float getPaddingBottom();

    /**
     * Sets the padding for the bottom of the cell.
     *
     * @param padding bottom padding
     */
    void setPaddingBottom(float padding);

    /**
     * Gets the padding for the left side of the cell. The default value is 2.
     *
     * @return the left padding
     */
    float getPaddingLeft();

    /**
     * Sets the padding for the left side of the cell.
     *
     * @param padding left padding
     */
    void setPaddingLeft(float padding);

    /**
     * Gets the padding for the right side of the cell. The default value is 2.
     *
     * @return the right padding
     */
    float getPaddingRight();

    /**
     * Sets the padding for the right side of the cell.
     *
     * @param padding right padding
     */
    void setPaddingRight(float padding);

    /**
     * Gets the {@link List} of {@link CellBorder}s configured for the cell. The default is {@link
     * CellBorder#ALL}.
     *
     * @return the cell borders
     */
    List<CellBorder> getBorders();

    /**
     * Sets the {@link CellBorder}s of the Cell.
     *
     * @param borders the borders
     */
    void setBorders(CellBorder... borders);

    /**
     * Gets the grey fill for the cell. The values range from 0.0 (Black) to 1.0 (White). The
     * default value is 1.0 (White).
     *
     * @return the grey fill
     */
    float getGreyFill();

    /**
     * Sets the grey fill for the cell. The values range from 0.0 (Black) to 1.0 (White).
     *
     * @param greyFill the grey fill
     * @throws IllegalArgumentException if the value is less than 0.0 or greater than 1.0.
     */
    void setGreyFill(float greyFill);
}

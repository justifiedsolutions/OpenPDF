/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A Table is a type of {@link Content} that can be added to a PDF {@link
 * com.justifiedsolutions.openpdf.pdf.Document}.
 */
public class Table implements Content {

    private final float[] relativeColumnWidths;
    private final List<Cell> cells = new ArrayList<>();
    private boolean keepTogether = false;
    private float widthPercentage = 80;
    private float spacingBefore = 0;
    private float spacingAfter = 0;

    public Table(float[] relativeColumnWidths) {
        this.relativeColumnWidths = relativeColumnWidths;
    }

    public Table(int numberOfColumns) {
        relativeColumnWidths = new float[numberOfColumns];
        Arrays.fill(relativeColumnWidths, 1);
    }

    /**
     * Gets the number of columns in the Table.
     *
     * @return the number of columns
     */
    public int getNumberOfColumns() {
        return this.relativeColumnWidths.length;
    }

    /**
     * Gets the array of relative column widths.
     *
     * @return column widths
     */
    public float[] getRelativeColumnWidths() {
        return Arrays.copyOf(this.relativeColumnWidths, this.relativeColumnWidths.length);
    }

    /**
     * Specifies whether the entire Table should be kept together on the same page. The default
     * value is <code>false</code>.
     *
     * @return true if the table should be kept together on the same page
     */
    public boolean isKeepTogether() {
        return keepTogether;
    }

    /**
     * Specifies whether the entire Table should be kept together on the same page.
     *
     * @param keepTogether true if the table should be kept together
     */
    public void setKeepTogether(boolean keepTogether) {
        this.keepTogether = keepTogether;
    }

    /**
     * Gets the percentage of the page width that the table should occupy. A value of 100 would go
     * from left margin to right margin. The default value is <code>80</code>.
     *
     * @return the width percentage
     */
    public float getWidthPercentage() {
        return widthPercentage;
    }

    /**
     * Sets the percentage of the page width that the table should occupy. A value of 100 would go
     * from left margin to right margin.
     *
     * @param widthPercentage the width percentage
     */
    public void setWidthPercentage(float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }

    /**
     * Gets the amount of empty space above the table. The default value is <code>0</code>.
     *
     * @return the spacing before the table
     */
    public float getSpacingBefore() {
        return spacingBefore;
    }

    /**
     * Sets the amount of empty space above the table.
     *
     * @param spacingBefore the spacing before the table
     */
    public void setSpacingBefore(float spacingBefore) {
        this.spacingBefore = spacingBefore;
    }

    /**
     * Gets the amount of empty space below the table. The default value is <code>0</code>.
     *
     * @return the spacing after the table
     */
    public float getSpacingAfter() {
        return spacingAfter;
    }

    /**
     * Sets the amount of empty space below the table.
     *
     * @param spacingAfter the spacing after the table
     */
    public void setSpacingAfter(float spacingAfter) {
        this.spacingAfter = spacingAfter;
    }

    /**
     * Creates a new empty {@link Cell} and adds it to the Table.
     *
     * @return a new cell
     */
    public Cell createCell() {
        Cell cell = new Cell();
        cells.add(cell);
        return cell;
    }

    /**
     * Creates a new {@link Cell} with the specified {@link Content} and adds it to the Table. The
     * Content must be either a {@link Phrase} or a {@link Paragraph}.
     *
     * @param content the content for the cell
     * @return a new cell
     * @throws IllegalArgumentException if content is not the correct type
     */
    public Cell createCell(Content content) {
        Cell cell = new Cell(content);
        cells.add(cell);
        return cell;
    }

    /**
     * Gets a {@link Collections#unmodifiableList(List)} of the {@link Cell}s in this Table.
     *
     * @return the cells
     */
    public List<Cell> getCells() {
        return Collections.unmodifiableList(cells);
    }
}

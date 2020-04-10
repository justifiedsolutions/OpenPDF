/*
 * $Id: PdfPCell.java 4065 2009-09-16 23:09:11Z psoares33 $
 *
 * Copyright 2001, 2002 Paulo Soares
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * https://github.com/LibrePDF/OpenPDF
 */

package com.justifiedsolutions.openpdf.text.pdf;

import static com.justifiedsolutions.openpdf.text.AlignmentConverter.convertHorizontalAlignment;
import static com.justifiedsolutions.openpdf.text.AlignmentConverter.convertVerticalAlignment;

import com.justifiedsolutions.openpdf.pdf.content.Cell.Border;
import com.justifiedsolutions.openpdf.text.DocumentException;
import com.justifiedsolutions.openpdf.text.Element;
import com.justifiedsolutions.openpdf.text.ExceptionConverter;
import com.justifiedsolutions.openpdf.text.Paragraph;
import com.justifiedsolutions.openpdf.text.Phrase;
import com.justifiedsolutions.openpdf.text.Rectangle;
import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import java.util.List;
import java.util.Objects;

/**
 * A cell in a PdfPTable.
 */

public class PdfPCell extends Rectangle {

    private ColumnText column = new ColumnText(null);

    /**
     * Vertical alignment of the cell.
     */
    private int verticalAlignment = Element.ALIGN_TOP;

    /**
     * Left padding of the cell.
     */
    private float paddingLeft = 2;

    /**
     * Right padding of the cell.
     */
    private float paddingRight = 2;

    /**
     * Top padding of the cell.
     */
    private float paddingTop = 2;

    /**
     * Bottom padding of the cell.
     */
    private float paddingBottom = 2;

    /**
     * Fixed height of the cell.
     */
    private float fixedHeight = 0;

    /**
     * Minimum height of the cell.
     */
    private float minimumHeight;

    /**
     * Holds value of property noWrap.
     */
    private boolean noWrap = false;

    /**
     * Holds value of property table.
     */
    private PdfPTable table;

    /**
     * Holds value of property colspan.
     */
    private int colspan = 1;

    /**
     * Holds value of property rowspan.
     *
     * @since 2.1.6
     */
    private int rowspan = 1;

    /**
     * Holds value of property cellEvent.
     */
    private PdfPCellEvent cellEvent;

    /**
     * Holds value of property useDescender.
     */
    private boolean useDescender;

    /**
     * Increases padding to include border if true
     */
    private boolean useBorderPadding = false;

    /**
     * The text in the cell.
     */
    protected Phrase phrase;

    /**
     * The rotation of the cell. Possible values are 0, 90, 180 and 270.
     */
    private int rotation;

    /**
     * Constructs an empty <CODE>PdfPCell</CODE>. The default padding is 2.
     */
    public PdfPCell() {
        super(0, 0, 0, 0);
        borderWidth = 0.5f;
        border = BOX;
        column.setLeading(0, 1);
    }

    /**
     * Constructs a <CODE>PdfPCell</CODE> with a <CODE>Phrase</CODE>. The default padding is 2.
     *
     * @param phrase the text
     */
    public PdfPCell(Phrase phrase) {
        super(0, 0, 0, 0);
        borderWidth = 0.5f;
        border = BOX;
        column.addText(this.phrase = phrase);
        column.setLeading(0, 1);
    }

    /**
     * Constructs a deep copy of a <CODE>PdfPCell</CODE>.
     *
     * @param cell the <CODE>PdfPCell</CODE> to duplicate
     */
    public PdfPCell(PdfPCell cell) {
        super(cell.llx, cell.lly, cell.urx, cell.ury);
        cloneNonPositionParameters(cell);
        verticalAlignment = cell.verticalAlignment;
        paddingLeft = cell.paddingLeft;
        paddingRight = cell.paddingRight;
        paddingTop = cell.paddingTop;
        paddingBottom = cell.paddingBottom;
        phrase = cell.phrase;
        fixedHeight = cell.fixedHeight;
        minimumHeight = cell.minimumHeight;
        noWrap = cell.noWrap;
        colspan = cell.colspan;
        rowspan = cell.rowspan;
        if (cell.table != null) {
            table = new PdfPTable(cell.table);
        }
        cellEvent = cell.cellEvent;
        useDescender = cell.useDescender;
        column = ColumnText.duplicate(cell.column);
        useBorderPadding = cell.useBorderPadding;
        rotation = cell.rotation;
    }

    /**
     * Adds an iText element to the cell.
     *
     * @param element
     */
    public void addElement(Element element) {
        if (table != null) {
            table = null;
            column.setText(null);
        }
        column.addElement(element);
    }

    /**
     * Gets the <CODE>Phrase</CODE> from this cell.
     *
     * @return the <CODE>Phrase</CODE>
     */
    public Phrase getPhrase() {
        return phrase;
    }

    /**
     * Sets the <CODE>Phrase</CODE> for this cell.
     *
     * @param phrase the <CODE>Phrase</CODE>
     */
    public void setPhrase(Phrase phrase) {
        table = null;
        column.setText(this.phrase = phrase);
    }

    /**
     * Gets the horizontal alignment for the cell.
     *
     * @return the horizontal alignment for the cell
     */
    public int getHorizontalAlignment() {
        return column.getAlignment();
    }

    /**
     * Sets the horizontal alignment for the cell. It could be
     * <CODE>Element.ALIGN_CENTER</CODE> for example.
     *
     * @param horizontalAlignment The horizontal alignment
     */
    public void setHorizontalAlignment(int horizontalAlignment) {
        column.setAlignment(horizontalAlignment);
    }

    /**
     * Gets the vertical alignment for the cell.
     *
     * @return the vertical alignment for the cell
     */
    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the vertical alignment for the cell. It could be
     * <CODE>Element.ALIGN_MIDDLE</CODE> for example.
     *
     * @param verticalAlignment The vertical alignment
     */
    public void setVerticalAlignment(int verticalAlignment) {
        if (table != null) {
            table.setExtendLastRow(verticalAlignment == Element.ALIGN_TOP);
        }
        this.verticalAlignment = verticalAlignment;
    }

    /**
     * Gets the effective left padding. This will include the left border width if {@link
     * #isUseBorderPadding()} is true.
     *
     * @return effective value of property paddingLeft.
     */
    public float getEffectivePaddingLeft() {
        if (isUseBorderPadding()) {
            float border = getBorderWidthLeft() / (isUseVariableBorders() ? 1f : 2f);
            return paddingLeft + border;
        }
        return paddingLeft;
    }

    /**
     * @return Value of property paddingLeft.
     */
    public float getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Setter for property paddingLeft.
     *
     * @param paddingLeft New value of property paddingLeft.
     */
    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    /**
     * Gets the effective right padding.  This will include the right border width if {@link
     * #isUseBorderPadding()} is true.
     *
     * @return effective value of property paddingRight.
     */
    public float getEffectivePaddingRight() {
        if (isUseBorderPadding()) {
            float border = getBorderWidthRight() / (isUseVariableBorders() ? 1f : 2f);
            return paddingRight + border;
        }
        return paddingRight;
    }

    /**
     * Getter for property paddingRight.
     *
     * @return Value of property paddingRight.
     */
    public float getPaddingRight() {
        return paddingRight;
    }

    /**
     * Setter for property paddingRight.
     *
     * @param paddingRight New value of property paddingRight.
     */
    public void setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
    }

    /**
     * Gets the effective top padding.  This will include the top border width if {@link
     * #isUseBorderPadding()} is true.
     *
     * @return effective value of property paddingTop.
     */
    public float getEffectivePaddingTop() {
        if (isUseBorderPadding()) {
            float border = getBorderWidthTop() / (isUseVariableBorders() ? 1f : 2f);
            return paddingTop + border;
        }
        return paddingTop;
    }

    /**
     * Getter for property paddingTop.
     *
     * @return Value of property paddingTop.
     */
    public float getPaddingTop() {
        return paddingTop;
    }

    /**
     * Setter for property paddingTop.
     *
     * @param paddingTop New value of property paddingTop.
     */
    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }

    /**
     * Gets the effective bottom padding. This will include  the bottom border width if {@link
     * #isUseBorderPadding()} is true.
     *
     * @return effective value of property paddingBottom.
     */
    public float getEffectivePaddingBottom() {
        if (isUseBorderPadding()) {
            float border = getBorderWidthBottom() / (isUseVariableBorders() ? 1f : 2f);
            return paddingBottom + border;
        }
        return paddingBottom;
    }

    /**
     * Getter for property paddingBottom.
     *
     * @return Value of property paddingBottom.
     */
    public float getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * Setter for property paddingBottom.
     *
     * @param paddingBottom New value of property paddingBottom.
     */
    public void setPaddingBottom(float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    /**
     * Sets the padding of the contents in the cell (space between content and border).
     *
     * @param padding
     */
    public void setPadding(float padding) {
        paddingBottom = padding;
        paddingTop = padding;
        paddingLeft = padding;
        paddingRight = padding;
    }

    /**
     * If true, then effective padding will include border widths
     *
     * @return true if effective padding includes border widths
     */
    public boolean isUseBorderPadding() {
        return useBorderPadding;
    }

    /**
     * Set a fixed height for the cell. This will automatically unset minimumHeight, if set.
     *
     * @param fixedHeight New value of property fixedHeight.
     */
    public void setFixedHeight(float fixedHeight) {
        this.fixedHeight = fixedHeight;
        minimumHeight = 0;
    }

    /**
     * Get the fixed height of the cell.
     *
     * @return Value of property fixedHeight.
     */
    public float getFixedHeight() {
        return fixedHeight;
    }

    /**
     * Tells you whether the cell has a fixed height.
     *
     * @return true is a fixed height was set.
     * @since 2.1.5
     */
    public boolean hasFixedHeight() {
        return getFixedHeight() > 0;
    }

    /**
     * Set a minimum height for the cell. This will automatically unset fixedHeight, if set.
     *
     * @param minimumHeight New value of property minimumHeight.
     */
    public void setMinimumHeight(float minimumHeight) {
        this.minimumHeight = minimumHeight;
        fixedHeight = 0;
    }

    /**
     * Get the minimum height of the cell.
     *
     * @return Value of property minimumHeight.
     */
    public float getMinimumHeight() {
        return minimumHeight;
    }

    /**
     * Getter for property noWrap.
     *
     * @return Value of property noWrap.
     */
    public boolean isNoWrap() {
        return noWrap;
    }

    /**
     * Getter for property colspan.
     *
     * @return Value of property colspan.
     */
    public int getColspan() {
        return colspan;
    }

    /**
     * Setter for property colspan.
     *
     * @param colspan New value of property colspan.
     */
    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    /**
     * Getter for property rowspan.
     *
     * @return Value of property rowspan.
     * @since 2.1.6
     */
    public int getRowspan() {
        return rowspan;
    }

    /**
     * Setter for property rowspan.
     *
     * @param rowspan New value of property rowspan.
     * @since 2.1.6
     */
    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    /**
     * Sets the run direction of the text content in the cell. May be either of:
     * PdfWriter.RUN_DIRECTION_DEFAULT, PdfWriter.RUN_DIRECTION_NO_BIDI, PdfWriter.RUN_DIRECTION_LTR
     * or PdfWriter.RUN_DIRECTION_RTL.
     *
     * @param runDirection
     */
    public void setRunDirection(int runDirection) {
        column.setRunDirection(runDirection);
    }

    /**
     * Gets the run direction of the text content in the cell
     *
     * @return One of the following values: PdfWriter.RUN_DIRECTION_DEFAULT,
     * PdfWriter.RUN_DIRECTION_NO_BIDI, PdfWriter.RUN_DIRECTION_LTR or PdfWriter.RUN_DIRECTION_RTL.
     */
    public int getRunDirection() {
        return column.getRunDirection();
    }


    /**
     * Gets the cell event for this cell.
     *
     * @return the cell event
     */
    public PdfPCellEvent getCellEvent() {
        return cellEvent;
    }


    /**
     * Getter for property useDescender.
     *
     * @return Value of property useDescender.
     */
    public boolean isUseDescender() {
        return useDescender;
    }

    /**
     * Gets the ColumnText with the content of the cell.
     *
     * @return a columntext object
     */
    public ColumnText getColumn() {
        return column;
    }

    /**
     * Sets the columntext in the cell.
     *
     * @param column
     */
    public void setColumn(ColumnText column) {
        this.column = column;
    }

    /**
     * Gets the rotation of the cell.
     *
     * @return the rotation of the cell.
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation of the cell. Possible values are 0, 90, 180 and 270.
     *
     * @param rotation the rotation of the cell
     */
    public void setRotation(int rotation) {
        rotation %= 360;
        if (rotation < 0) {
            rotation += 360;
        }
        if ((rotation % 90) != 0) {
            throw new IllegalArgumentException(
                    MessageLocalization.getComposedMessage("rotation.must.be.a.multiple.of.90"));
        }
        this.rotation = rotation;
    }

    /**
     * Consumes part of the content of the cell.
     *
     * @param height the hight of the part that has to be consumed
     * @since 2.1.6
     */
    void consumeHeight(float height) {
        float rightLimit = getRight() - getEffectivePaddingRight();
        float leftLimit = getLeft() + getEffectivePaddingLeft();
        float bry = height - getEffectivePaddingTop() - getEffectivePaddingBottom();
        if (getRotation() != 90 && getRotation() != 270) {
            column.setSimpleColumn(leftLimit, bry + 0.001f, rightLimit, 0);
        } else {
            column.setSimpleColumn(0, leftLimit, bry + 0.001f, rightLimit);
        }
        try {
            column.go(true);
        } catch (DocumentException e) {
            // do nothing
        }
    }

    /**
     * Returns the height of the cell.
     *
     * @return the height of the cell
     * @since 3.0.0
     */
    public float getMaxHeight() {
        boolean pivoted = (getRotation() == 90 || getRotation() == 270);
            if ((pivoted && hasFixedHeight()) || getColumn() == null) {
                setBottom(getTop() - getFixedHeight());
            } else {
                ColumnText ct = ColumnText.duplicate(getColumn());
                float right, top, left, bottom;
                if (pivoted) {
                    right = PdfPRow.RIGHT_LIMIT;
                    top = getRight() - getEffectivePaddingRight();
                    left = 0;
                    bottom = getLeft() + getEffectivePaddingLeft();
                } else {
                    right = isNoWrap() ? PdfPRow.RIGHT_LIMIT
                            : getRight() - getEffectivePaddingRight();
                    top = getTop() - getEffectivePaddingTop();
                    left = getLeft() + getEffectivePaddingLeft();
                    bottom = hasFixedHeight() ? getTop() + getEffectivePaddingBottom()
                            - getFixedHeight() : PdfPRow.BOTTOM_LIMIT;
                }
                PdfPRow.setColumn(ct, left, bottom, right, top);
                try {
                    ct.go(true);
                } catch (DocumentException e) {
                    throw new ExceptionConverter(e);
                }
                if (pivoted) {
                    setBottom(getTop() - getEffectivePaddingTop() - getEffectivePaddingBottom() - ct
                            .getFilledWidth());
                } else {
                    float yLine = ct.getYLine();
                    if (isUseDescender()) {
                        yLine += ct.getDescender();
                    }
                    setBottom(yLine - getEffectivePaddingBottom());
                }
            }

        float height = getHeight();
        if (hasFixedHeight()) {
            height = getFixedHeight();
        } else if (height < getMinimumHeight()) {
            height = getMinimumHeight();
        }
        return height;
    }

    public static PdfPCell getInstance(com.justifiedsolutions.openpdf.pdf.content.Cell cell) {
        Objects.requireNonNull(cell);
        PdfPCell result = new PdfPCell();
        result.setBorder(convertBorder(cell.getBorders()));
        result.setRowspan(cell.getRowSpan());
        result.setColspan(cell.getColumnSpan());
        result.setHorizontalAlignment(convertHorizontalAlignment(cell.getHorizontalAlignment()));
        result.setVerticalAlignment(convertVerticalAlignment(cell.getVerticalAlignment()));
        result.setMinimumHeight(cell.getMinimumHeight());
        result.setPaddingTop(cell.getPaddingTop());
        result.setPaddingBottom(cell.getPaddingBottom());
        result.setPaddingLeft(cell.getPaddingLeft());
        result.setPaddingRight(cell.getPaddingRight());
        result.setGrayFill(cell.getGreyFill());
        if (cell.getContent() instanceof com.justifiedsolutions.openpdf.pdf.content.Paragraph) {
            Paragraph paragraph = Paragraph.getInstance((com.justifiedsolutions.openpdf.pdf.content.Paragraph) cell.getContent());
            if (paragraph.getAlignment() == ALIGN_UNDEFINED) {
                paragraph.setAlignment(result.getHorizontalAlignment());
            }
            result.addElement(paragraph);
        } else if (cell.getContent() instanceof com.justifiedsolutions.openpdf.pdf.content.Phrase) {
            Phrase phrase = Phrase.getInstance((com.justifiedsolutions.openpdf.pdf.content.Phrase) cell.getContent());
            Paragraph paragraph = new Paragraph(phrase);
            paragraph.setAlignment(result.getHorizontalAlignment());
            paragraph.setLeading(0, 1);
            result.addElement(paragraph);
        }

        return result;
    }

    private static int convertBorder(List<Border> borders) {
        int result = NO_BORDER;
        if (borders != null) {
            for (Border border : borders) {
                switch (border) {
                    case TOP:
                        result |= TOP;
                        break;
                    case BOTTOM:
                        result |= BOTTOM;
                        break;
                    case LEFT:
                        result |= LEFT;
                        break;
                    case RIGHT:
                        result |= RIGHT;
                        break;
                    case ALL:
                        result = BOX;
                        break;
                }
                if (result == BOX) {
                    break;
                }
            }
        }
        return result;
    }
}

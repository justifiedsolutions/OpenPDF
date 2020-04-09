/*
 * $Id: PdfContentByte.java 4065 2009-09-16 23:09:11Z psoares33 $
 *
 * Copyright 1999, 2000, 2001, 2002 Bruno Lowagie
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

import static com.justifiedsolutions.openpdf.text.pdf.ExtendedColor.MAX_COLOR_VALUE;
import static com.justifiedsolutions.openpdf.text.pdf.ExtendedColor.MAX_FLOAT_COLOR_VALUE;
import static com.justifiedsolutions.openpdf.text.pdf.ExtendedColor.MAX_INT_COLOR_VALUE;

import com.justifiedsolutions.openpdf.text.Rectangle;
import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import com.justifiedsolutions.openpdf.text.exceptions.IllegalPdfSyntaxException;
import com.justifiedsolutions.openpdf.text.pdf.internal.PdfXConformanceImp;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <CODE>PdfContentByte</CODE> is an object containing the user positioned
 * text and graphic contents of a page. It knows how to apply the proper
 * font encoding.
 */

public class PdfContentByte {

    /**
     * This class keeps the graphic state of the current page
     */

    static class GraphicState {

        /** This is the font in use */
        FontDetails fontDetails;

        /** This is the color in use */
        ColorDetails colorDetails;

        /** This is the font size in use */
        float size;

        /** The x position of the text line matrix. */
        protected float xTLM = 0;
        /** The y position of the text line matrix. */
        protected float yTLM = 0;

        /** The current text leading. */
        protected float leading = 0;

        /** The current horizontal scaling */
        protected float scale = 100;

        /** The current character spacing */
        protected float charSpace = 0;

        /** The current word spacing */
        protected float wordSpace = 0;

        protected ExtendedColor colorFill = GrayColor.GRAYBLACK;

        protected ExtendedColor colorStroke = GrayColor.GRAYBLACK;

        protected PdfObject extGState = null;

        GraphicState() {
        }

        GraphicState(GraphicState cp) {
            copyParameters(cp);
        }

        void copyParameters(final GraphicState cp) {
            fontDetails = cp.fontDetails;
            colorDetails = cp.colorDetails;
            size = cp.size;
            xTLM = cp.xTLM;
            yTLM = cp.yTLM;
            leading = cp.leading;
            scale = cp.scale;
            charSpace = cp.charSpace;
            wordSpace = cp.wordSpace;
            colorFill = cp.colorFill;
            colorStroke = cp.colorStroke;
            extGState = cp.extGState;
        }

        void restore(final GraphicState restore) {
            copyParameters(restore);
        }
    }

    /** A possible line cap value */
    public static final int LINE_CAP_BUTT = 0;

    /** A possible line join value */
    public static final int LINE_JOIN_MITER = 0;

    /** A possible text rendering value */
    public static final int TEXT_RENDER_MODE_FILL = 0;
    /** A possible text rendering value */
    public static final int TEXT_RENDER_MODE_STROKE = 1;
    /** A possible text rendering value */
    public static final int TEXT_RENDER_MODE_FILL_STROKE = 2;

    static final float MIN_FONT_SIZE = 0.0001f;

    // membervariables

    /** This is the actual content */
    protected ByteBuffer content = new ByteBuffer();

    /** This is the writer */
    protected PdfWriter writer;

    /** This is the PdfDocument */
    protected PdfDocument pdf;

    /** This is the GraphicState in use */
    protected GraphicState state = new GraphicState();

    private static final Map<PdfName, String> abrev = new HashMap<>();
    /** The list were we save/restore the state */
    protected List<GraphicState> stateList = new ArrayList<>();

    /** The separator between commands.
     */
    protected int separator = '\n';

    private boolean inText = false;
    /** The list were we save/restore the layer depth */
    protected List<Integer> layerDepth;

    static {
        abrev.put(PdfName.BITSPERCOMPONENT, "/BPC ");
        abrev.put(PdfName.COLORSPACE, "/CS ");
        abrev.put(PdfName.DECODE, "/D ");
        abrev.put(PdfName.DECODEPARMS, "/DP ");
        abrev.put(PdfName.FILTER, "/F ");
        abrev.put(PdfName.HEIGHT, "/H ");
        abrev.put(PdfName.IMAGEMASK, "/IM ");
        abrev.put(PdfName.INTENT, "/Intent ");
        abrev.put(PdfName.INTERPOLATE, "/I ");
        abrev.put(PdfName.WIDTH, "/W ");
    }

    // constructors

    /**
     * Constructs a new <CODE>PdfContentByte</CODE>-object.
     *
     * @param wr the writer associated to this content
     */

    public PdfContentByte(PdfWriter wr) {
        if (wr != null) {
            writer = wr;
            pdf = writer.getPdfDocument();
        }
    }

    // methods to get the content of this object

    /**
     * Returns the <CODE>String</CODE> representation of this <CODE>PdfContentByte</CODE>-object.
     *
     * @return      a <CODE>String</CODE>
     */

    public String toString() {
        return content.toString();
    }

    /**
     * Gets the internal buffer.
     * @return the internal buffer
     */
    public ByteBuffer getInternalBuffer() {
        return content;
    }

    /** Returns the PDF representation of this <CODE>PdfContentByte</CODE>-object.
     *
     * @return a <CODE>byte</CODE> array with the representation
     */

    public byte[] toPdf() {
        sanityCheck();
        return content.toByteArray();
    }

    // methods to add graphical content

    /**
     * Adds the content of another <CODE>PdfContent</CODE>-object to this object.
     *
     * @param       other       another <CODE>PdfByteContent</CODE>-object
     */

    public void add(PdfContentByte other) {
        if (other.writer != null && writer != other.writer)
            throw new RuntimeException(MessageLocalization.getComposedMessage("inconsistent.writers.are.you.mixing.two.documents"));
        content.append(other.content);
    }

    /**
     * Gets the x position of the text line matrix.
     *
     * @return the x position of the text line matrix
     */
    public float getXTLM() {
        return state.xTLM;
    }

    /**
     * Gets the y position of the text line matrix.
     *
     * @return the y position of the text line matrix
     */
    public float getYTLM() {
        return state.yTLM;
    }

    /**
     * Gets the current text leading.
     *
     * @return the current text leading
     */
    public float getLeading() {
        return state.leading;
    }

    /**
     * Gets the current character spacing.
     *
     * @return the current character spacing
     */
    public float getCharacterSpacing() {
        return state.charSpace;
    }

    /**
     * Changes the <VAR>Line cap style</VAR>.
     * <P>
     * The <VAR>line cap style</VAR> specifies the shape to be used at the end of open subpaths
     * when they are stroked.<BR>
     * Allowed values are LINE_CAP_BUTT, LINE_CAP_ROUND and LINE_CAP_PROJECTING_SQUARE.<BR>
     *
     * @param       style       a value
     */

    public void setLineCap(int style) {
        if (style >= 0 && style <= 2) {
            content.append(style).append(" J").append_i(separator);
        }
    }

    /**
     * Changes the <VAR>Line join style</VAR>.
     * <P>
     * The <VAR>line join style</VAR> specifies the shape to be used at the corners of paths
     * that are stroked.<BR>
     * Allowed values are LINE_JOIN_MITER (Miter joins), LINE_JOIN_ROUND (Round joins) and LINE_JOIN_BEVEL (Bevel joins).<BR>
     *
     * @param       style       a value
     */

    public void setLineJoin(int style) {
        if (style >= 0 && style <= 2) {
            content.append(style).append(" j").append_i(separator);
        }
    }

    /**
     * Changes the <VAR>line width</VAR>.
     * <P>
     * The line width specifies the thickness of the line used to stroke a path and is measured
     * in user space units.<BR>
     *
     * @param       w           a width
     */

    public void setLineWidth(float w) {
        content.append(w).append(" w").append_i(separator);
    }

    /**
     * Modify the current clipping path by intersecting it with the current path, using the
     * nonzero winding number rule to determine which regions lie inside the clipping
     * path.
     */

    public void clip() {
        content.append("W").append_i(separator);
    }

    /**
     * Changes the currentgray tint for filling paths (device dependent colors!).
     * <P>
     * Sets the color space to <B>DeviceGray</B> (or the <B>DefaultGray</B> color space),
     * and sets the gray tint to use for filling paths.</P>
     *
     * @param   gray    a value between 0 (black) and 1 (white)
     */

    public void setGrayFill(float gray) {
        setGrayFill(gray, MAX_FLOAT_COLOR_VALUE);
    }

    public void setGrayFill(float gray, float alpha) {
        saveColorFill(new GrayColor(gray, alpha));
        content.append(gray).append(" g").append_i(separator);
    }

    /**
     * Changes the current gray tint for filling paths to black.
     */

    public void resetGrayFill() {
        saveColorFill(GrayColor.GRAYBLACK);
        content.append("0 g").append_i(separator);
    }

    /**
     * Changes the currentgray tint for stroking paths (device dependent colors!).
     * <P>
     * Sets the color space to <B>DeviceGray</B> (or the <B>DefaultGray</B> color space),
     * and sets the gray tint to use for stroking paths.</P>
     *
     * @param   gray    a value between 0 (black) and 1 (white)
     */

    public void setGrayStroke(float gray) {
        setGrayStroke(gray, MAX_FLOAT_COLOR_VALUE);
    }
    public void setGrayStroke(float gray, float alpha) {
        saveColorStroke(new GrayColor(gray, alpha));
        content.append(gray).append(" G").append_i(separator);
    }

    /**
     * Changes the current gray tint for stroking paths to black.
     */

    public void resetGrayStroke() {
        saveColorStroke(GrayColor.GRAYBLACK);
        content.append("0 G").append_i(separator);
    }

    /**
     * Helper to validate and write the RGB color components
     * @param   red     the intensity of red. A value between 0 and 255
     * @param   green   the intensity of green. A value between 0 and 255
     * @param   blue    the intensity of blue. A value between 0 and 255
     */
    private void HelperRGB(int red, int green, int blue) {
        HelperRGB((float)(red & MAX_COLOR_VALUE) / MAX_INT_COLOR_VALUE, (float)(green & MAX_COLOR_VALUE) / MAX_INT_COLOR_VALUE, (float)(blue & MAX_COLOR_VALUE) / MAX_INT_COLOR_VALUE);
    }

    /**
     * Helper to validate and write the RGB color components
     * @param   red     the intensity of red. A value between 0 and 1
     * @param   green   the intensity of green. A value between 0 and 1
     * @param   blue    the intensity of blue. A value between 0 and 1
     */
    private void HelperRGB(float red, float green, float blue) {
        PdfXConformanceImp.checkPDFXConformance(writer, PdfXConformanceImp.PDFXKEY_RGB, null);
        if (red < 0.0f)
            red = 0.0f;
        else if (red > MAX_FLOAT_COLOR_VALUE)
            red = MAX_FLOAT_COLOR_VALUE;
        if (green < 0.0f)
            green = 0.0f;
        else if (green > MAX_FLOAT_COLOR_VALUE)
            green = MAX_FLOAT_COLOR_VALUE;
        if (blue < 0.0f)
            blue = 0.0f;
        else if (blue > MAX_FLOAT_COLOR_VALUE)
            blue = MAX_FLOAT_COLOR_VALUE;
        content.append(red).append(' ').append(green).append(' ').append(blue);
    }

    /**
     * Changes the current color for filling paths (device dependent colors!).
     * <P>
     * Sets the color space to <B>DeviceRGB</B> (or the <B>DefaultRGB</B> color space),
     * and sets the color to use for filling paths.</P>
     * <P>
     * Following the PDF manual, each operand must be a number between 0 (minimum intensity) and
     * 1 (maximum intensity).</P>
     *
     * @param   red     the intensity of red. A value between 0 and 1
     * @param   green   the intensity of green. A value between 0 and 1
     * @param   blue    the intensity of blue. A value between 0 and 1
     */

    public void setRGBColorFillF(float red, float green, float blue) {
        setRGBColorFillF(red, green, blue, MAX_FLOAT_COLOR_VALUE);
    }
    public void setRGBColorFillF(float red, float green, float blue, float alpha) {
        saveColorFill(new RGBColor(red, green, blue, alpha));
        HelperRGB(red, green, blue);
        content.append(" rg").append_i(separator);
    }

    /**
     * Changes the current color for filling paths to black.
     * Resetting using gray color to keep the backward compatibility.
     */

    public void resetRGBColorFill() {
        resetGrayFill();
    }

    /**
     * Changes the current color for stroking paths (device dependent colors!).
     * <P>
     * Sets the color space to <B>DeviceRGB</B> (or the <B>DefaultRGB</B> color space),
     * and sets the color to use for stroking paths.</P>
     * <P>
     * Following the PDF manual, each operand must be a number between 0 (miniumum intensity) and
     * 1 (maximum intensity).
     *
     * @param   red     the intensity of red. A value between 0 and 1
     * @param   green   the intensity of green. A value between 0 and 1
     * @param   blue    the intensity of blue. A value between 0 and 1
     */

    public void setRGBColorStrokeF(float red, float green, float blue) {
        saveColorStroke(new RGBColor(red, green, blue));
        HelperRGB(red, green, blue);
        content.append(" RG").append_i(separator);
    }

    /**
     * Changes the current color for stroking paths to black.
     * Resetting using gray color to keep the backward compatibility.
     */

    public void resetRGBColorStroke() {
        resetGrayStroke();
    }

    /**
     * Helper to validate and write the CMYK color components.
     *
     * @param   cyan    the intensity of cyan. A value between 0 and 255
     * @param   magenta the intensity of magenta. A value between 0 and 255
     * @param   yellow  the intensity of yellow. A value between 0 and 255
     * @param   black   the intensity of black. A value between 0 and 255
     */
    private void HelperCMYK(int cyan, int magenta, int yellow, int black) {
        HelperCMYK((float)(cyan & MAX_COLOR_VALUE) / MAX_INT_COLOR_VALUE, (float)(magenta & MAX_COLOR_VALUE) / MAX_INT_COLOR_VALUE, (float)(yellow & MAX_COLOR_VALUE) / MAX_INT_COLOR_VALUE, (float)(black & MAX_COLOR_VALUE) / MAX_INT_COLOR_VALUE);
    }

    /**
     * Helper to validate and write the CMYK color components.
     *
     * @param   cyan    the intensity of cyan. A value between 0 and 1
     * @param   magenta the intensity of magenta. A value between 0 and 1
     * @param   yellow  the intensity of yellow. A value between 0 and 1
     * @param   black   the intensity of black. A value between 0 and 1
     */
    private void HelperCMYK(float cyan, float magenta, float yellow, float black) {
        if (cyan < 0.0f)
            cyan = 0.0f;
        else if (cyan > MAX_FLOAT_COLOR_VALUE)
            cyan = MAX_FLOAT_COLOR_VALUE;
        if (magenta < 0.0f)
            magenta = 0.0f;
        else if (magenta > MAX_FLOAT_COLOR_VALUE)
            magenta = MAX_FLOAT_COLOR_VALUE;
        if (yellow < 0.0f)
            yellow = 0.0f;
        else if (yellow > MAX_FLOAT_COLOR_VALUE)
            yellow = MAX_FLOAT_COLOR_VALUE;
        if (black < 0.0f)
            black = 0.0f;
        else if (black > MAX_FLOAT_COLOR_VALUE)
            black = MAX_FLOAT_COLOR_VALUE;
        content.append(cyan).append(' ').append(magenta).append(' ').append(yellow).append(' ').append(black);
    }

    /**
     * Changes the current color for filling paths (device dependent colors!).
     * <P>
     * Sets the color space to <B>DeviceCMYK</B> (or the <B>DefaultCMYK</B> color space),
     * and sets the color to use for filling paths.</P>
     * <P>
     * Following the PDF manual, each operand must be a number between 0 (no ink) and
     * 1 (maximum ink).</P>
     *
     * @param   cyan    the intensity of cyan. A value between 0 and 1
     * @param   magenta the intensity of magenta. A value between 0 and 1
     * @param   yellow  the intensity of yellow. A value between 0 and 1
     * @param   black   the intensity of black. A value between 0 and 1
     */

    public void setCMYKColorFillF(float cyan, float magenta, float yellow, float black) {
        setCMYKColorFillF(cyan, magenta, yellow, black, MAX_FLOAT_COLOR_VALUE);
    }

    public void setCMYKColorFillF(float cyan, float magenta, float yellow, float black, float alpha) {
        saveColorFill(new CMYKColor(cyan, magenta, yellow, black, alpha));
        HelperCMYK(cyan, magenta, yellow, black);
        content.append(" k").append_i(separator);
    }

    /**
     * Changes the current color for filling paths to black.
     *
     */

    public void resetCMYKColorFill() {
        saveColorFill(new CMYKColor(0.0f, 0.0f, 0.0f, MAX_FLOAT_COLOR_VALUE));
        HelperCMYK(0.0f, 0.0f, 0.0f, MAX_FLOAT_COLOR_VALUE);
        content.append(" k").append_i(separator);
    }

    /**
     * Changes the current color for stroking paths (device dependent colors!).
     * <P>
     * Sets the color space to <B>DeviceCMYK</B> (or the <B>DefaultCMYK</B> color space),
     * and sets the color to use for stroking paths.</P>
     * <P>
     * Following the PDF manual, each operand must be a number between 0 (miniumum intensity) and
     * 1 (maximum intensity).
     *
     * @param   cyan    the intensity of cyan. A value between 0 and 1
     * @param   magenta the intensity of magenta. A value between 0 and 1
     * @param   yellow  the intensity of yellow. A value between 0 and 1
     * @param   black   the intensity of black. A value between 0 and 1
     */

    public void setCMYKColorStrokeF(float cyan, float magenta, float yellow, float black) {
        setCMYKColorStrokeF(cyan, magenta, yellow, black, MAX_FLOAT_COLOR_VALUE);
    }
    public void setCMYKColorStrokeF(float cyan, float magenta, float yellow, float black, float alpha) {
        saveColorStroke(new CMYKColor(cyan, magenta, yellow, black, alpha));
        HelperCMYK(cyan, magenta, yellow, black);
        content.append(" K").append_i(separator);
    }

    /**
     * Changes the current color for stroking paths to black.
     *
     */

    public void resetCMYKColorStroke() {
        saveColorStroke(new CMYKColor(0.0f, 0.0f, 0.0f, MAX_FLOAT_COLOR_VALUE));
        HelperCMYK(0.0f, 0.0f, 0.0f, MAX_FLOAT_COLOR_VALUE);
        content.append(" K").append_i(separator);
    }

    /**
     * Move the current point <I>(x, y)</I>, omitting any connecting line segment.
     *
     * @param       x               new x-coordinate
     * @param       y               new y-coordinate
     */

    public void moveTo(float x, float y) {
        content.append(x).append(' ').append(y).append(" m").append_i(separator);
    }

    /**
     * Appends a straight line segment from the current point <I>(x, y)</I>. The new current
     * point is <I>(x, y)</I>.
     *
     * @param       x               new x-coordinate
     * @param       y               new y-coordinate
     */

    public void lineTo(float x, float y) {
        content.append(x).append(' ').append(y).append(" l").append_i(separator);
    }


    /**
     * Adds a rectangle to the current path.
     *
     * @param       x       x-coordinate of the starting point
     * @param       y       y-coordinate of the starting point
     * @param       w       width
     * @param       h       height
     */

    public void rectangle(float x, float y, float w, float h) {
        content.append(x).append(' ').append(y).append(' ').append(w).append(' ').append(h).append(" re").append_i(separator);
    }

    private boolean compareColors(Color c1, Color c2) {
        if (c1 == null && c2 == null)
            return true;
        if (c1 == null || c2 == null)
            return false;
        if (c1 instanceof ExtendedColor)
            return c1.equals(c2);
        return c2.equals(c1);
    }

    /**
     * Adds a variable width border to the current path.
     * Only use if {@link Rectangle#isUseVariableBorders() Rectangle.isUseVariableBorders}
     * = true.
     * @param rect a <CODE>Rectangle</CODE>
     */
    public void variableRectangle(Rectangle rect) {
        float t = rect.getTop();
        float b = rect.getBottom();
        float r = rect.getRight();
        float l = rect.getLeft();
        float wt = rect.getBorderWidthTop();
        float wb = rect.getBorderWidthBottom();
        float wr = rect.getBorderWidthRight();
        float wl = rect.getBorderWidthLeft();
        Color ct = rect.getBorderColorTop();
        Color cb = rect.getBorderColorBottom();
        Color cr = rect.getBorderColorRight();
        Color cl = rect.getBorderColorLeft();
        saveState();
        setLineCap(PdfContentByte.LINE_CAP_BUTT);
        setLineJoin(PdfContentByte.LINE_JOIN_MITER);
        float clw = 0;
        boolean cdef = false;
        Color ccol = null;
        boolean cdefi = false;
        Color cfil = null;
        // draw top
        if (wt > 0) {
            setLineWidth(clw = wt);
            cdef = true;
            if (ct == null)
                resetRGBColorStroke();
            else
                setColorStroke(ct);
            ccol = ct;
            moveTo(l, t - wt / 2f);
            lineTo(r, t - wt / 2f);
            stroke();
        }

        // Draw bottom
        if (wb > 0) {
            if (wb != clw)
                setLineWidth(clw = wb);
            if (!cdef || !compareColors(ccol, cb)) {
                cdef = true;
                if (cb == null)
                    resetRGBColorStroke();
                else
                    setColorStroke(cb);
                ccol = cb;
            }
            moveTo(r, b + wb / 2f);
            lineTo(l, b + wb / 2f);
            stroke();
        }

        // Draw right
        if (wr > 0) {
            if (wr != clw)
                setLineWidth(clw = wr);
            if (!cdef || !compareColors(ccol, cr)) {
                cdef = true;
                if (cr == null)
                    resetRGBColorStroke();
                else
                    setColorStroke(cr);
                ccol = cr;
            }
            boolean bt = compareColors(ct, cr);
            boolean bb = compareColors(cb, cr);
            moveTo(r - wr / 2f, bt ? t : t - wt);
            lineTo(r - wr / 2f, bb ? b : b + wb);
            stroke();
            if (!bt || !bb) {
                cdefi = true;
                if (cr == null)
                    resetRGBColorFill();
                else
                    setColorFill(cr);
                cfil = cr;
                if (!bt) {
                    moveTo(r, t);
                    lineTo(r, t - wt);
                    lineTo(r - wr, t - wt);
                    fill();
                }
                if (!bb) {
                    moveTo(r, b);
                    lineTo(r, b + wb);
                    lineTo(r - wr, b + wb);
                    fill();
                }
            }
        }

        // Draw Left
        if (wl > 0) {
            if (wl != clw)
                setLineWidth(wl);
            if (!cdef || !compareColors(ccol, cl)) {
                if (cl == null)
                    resetRGBColorStroke();
                else
                    setColorStroke(cl);
            }
            boolean bt = compareColors(ct, cl);
            boolean bb = compareColors(cb, cl);
            moveTo(l + wl / 2f, bt ? t : t - wt);
            lineTo(l + wl / 2f, bb ? b : b + wb);
            stroke();
            if (!bt || !bb) {
                if (!cdefi || !compareColors(cfil, cl)) {
                    if (cl == null)
                        resetRGBColorFill();
                    else
                        setColorFill(cl);
                }
                if (!bt) {
                    moveTo(l, t);
                    lineTo(l, t - wt);
                    lineTo(l + wl, t - wt);
                    fill();
                }
                if (!bb) {
                    moveTo(l, b);
                    lineTo(l, b + wb);
                    lineTo(l + wl, b + wb);
                    fill();
                }
            }
        }
        restoreState();
    }

    /**
     * Adds a border (complete or partially) to the current path..
     *
     * @param       rectangle       a <CODE>Rectangle</CODE>
     */

    public void rectangle(Rectangle rectangle) {
        // the coordinates of the border are retrieved
        float x1 = rectangle.getLeft();
        float y1 = rectangle.getBottom();
        float x2 = rectangle.getRight();
        float y2 = rectangle.getTop();

        // the backgroundcolor is set
        Color background = rectangle.getBackgroundColor();
        if (background != null) {
            saveState();
            setColorFill(background);
            rectangle(x1, y1, x2 - x1, y2 - y1);
            fill();
            restoreState();
        }

        // if the element hasn't got any borders, nothing is added
        if (! rectangle.hasBorders()) {
            return;
        }

        // if any of the individual border colors are set
        // we draw the borders all around using the
        // different colors
        if (rectangle.isUseVariableBorders()) {
            variableRectangle(rectangle);
        }
        else {
            // the width is set to the width of the element
            if (rectangle.getBorderWidth() != Rectangle.UNDEFINED) {
                setLineWidth(rectangle.getBorderWidth());
            }

            // the color is set to the color of the element
            Color color = rectangle.getBorderColor();
            if (color != null) {
                setColorStroke(color);
            }

            // if the box is a rectangle, it is added as a rectangle
            if (rectangle.hasBorder(Rectangle.BOX)) {
               rectangle(x1, y1, x2 - x1, y2 - y1);
            }
            // if the border isn't a rectangle, the different sides are added apart
            else {
                if (rectangle.hasBorder(Rectangle.RIGHT)) {
                    moveTo(x2, y1);
                    lineTo(x2, y2);
                }
                if (rectangle.hasBorder(Rectangle.LEFT)) {
                    moveTo(x1, y1);
                    lineTo(x1, y2);
                }
                if (rectangle.hasBorder(Rectangle.BOTTOM)) {
                    moveTo(x1, y1);
                    lineTo(x2, y1);
                }
                if (rectangle.hasBorder(Rectangle.TOP)) {
                    moveTo(x1, y2);
                    lineTo(x2, y2);
                }
            }

            stroke();

            if (color != null) {
                resetRGBColorStroke();
            }
        }
    }

    /**
     * Ends the path without filling or stroking it.
     */

    public void newPath() {
        content.append("n").append_i(separator);
    }

    /**
     * Strokes the path.
     */

    public void stroke() {
        content.append("S").append_i(separator);
    }

    /**
     * Fills the path, using the non-zero winding number rule to determine the region to fill.
     */

    public void fill() {
        content.append("f").append_i(separator);
    }

    /**
     * Makes this <CODE>PdfContentByte</CODE> empty.
     * Calls <code>reset( true )</code>
     */
    public void reset() {
        reset( true );
    }

    /**
     * Makes this <CODE>PdfContentByte</CODE> empty.
     * @param validateContent will call <code>sanityCheck()</code> if true.
     * @since 2.1.6
     */
    public void reset( boolean validateContent ) {
        content.reset();
        if (validateContent) {
            sanityCheck();
        }
        state = new GraphicState();
    }


    /**
     * Starts the writing of text.
     */
    public void beginText() {
        if (inText) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators"));
        }
        inText = true;
        state.xTLM = 0;
        state.yTLM = 0;
        content.append("BT").append_i(separator);
    }

    /**
     * Ends the writing of text and makes the current font invalid.
     */
    public void endText() {
        if (!inText) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators"));
        }
        inText = false;
        content.append("ET").append_i(separator);
    }

    /**
     * Saves the graphic state. <CODE>saveState</CODE> and
     * <CODE>restoreState</CODE> must be balanced.
     */
    public void saveState() {
        content.append("q").append_i(separator);
        GraphicState gstate = new GraphicState(state);
        stateList.add(gstate);
    }

    /**
     * Restores the graphic state. <CODE>saveState</CODE> and
     * <CODE>restoreState</CODE> must be balanced.
     */
    public void restoreState() {
        content.append("Q").append_i(separator);
        int idx = stateList.size() - 1;
        if (idx < 0)
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.save.restore.state.operators"));
        state = stateList.get(idx);
        state.restore(state);
        stateList.remove(idx);
    }

    /**
     * Sets the character spacing parameter.
     *
     * @param       charSpace           a parameter
     */
    public void setCharacterSpacing(float charSpace) {
        state.charSpace = charSpace;
        content.append(charSpace).append(" Tc").append_i(separator);
    }

    /**
     * Sets the word spacing parameter.
     *
     * @param       wordSpace           a parameter
     */
    public void setWordSpacing(float wordSpace) {
        state.wordSpace = wordSpace;
        content.append(wordSpace).append(" Tw").append_i(separator);
    }

    /**
     * Sets the text leading parameter.
     * <P>
     * The leading parameter is measured in text space units. It specifies the vertical distance
     * between the baselines of adjacent lines of text.</P>
     *
     * @param       leading         the new leading
     */
    public void setLeading(float leading) {
        state.leading = leading;
        content.append(leading).append(" TL").append_i(separator);
    }

    /**
     * Set the font and the size for the subsequent text writing.
     *
     * @param bf the font
     * @param size the font size in points
     */
    public void setFontAndSize(BaseFont bf, float size) {
        checkWriter();
        if (size < MIN_FONT_SIZE && size > -MIN_FONT_SIZE) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("font.size.too.small.1", String.valueOf(size)));
        }
        state.size = size;
        state.fontDetails = writer.addSimple(bf);
        PageResources prs = getPageResources();
        PdfName name = state.fontDetails.getFontName();
        name = prs.addFont(name, state.fontDetails.getIndirectReference());
        content.append(name.getBytes()).append(' ').append(size).append(" Tf").append_i(separator);
    }

    /**
     * Sets the text rendering parameter.
     *
     * @param       rendering               a parameter
     */
    public void setTextRenderingMode(int rendering) {
        content.append(rendering).append(" Tr").append_i(separator);
    }

    /**
     * Sets the text rise parameter.
     * <P>
     * This allows to write text in subscript or superscript mode.</P>
     *
     * @param       rise                a parameter
     */
    public void setTextRise(float rise) {
        content.append(rise).append(" Ts").append_i(separator);
    }

    /**
     * A helper to insert into the content stream the <CODE>text</CODE>
     * converted to bytes according to the font's encoding.
     *
     * @param text the text to write
     */
    private void showText2(String text) {
        if (state.fontDetails == null)
            throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text"));
        byte[] b = state.fontDetails.convertToBytes(text);
        escapeString(b, content);
    }

    /**
     * Shows the <CODE>text</CODE>.
     *
     * @param text the text to write
     */
    public void showText(String text) {
        showText2(text);
        content.append("Tj").append_i(separator);
    }

    /**
     * Changes the text matrix.
     * <P>
     * Remark: this operation also initializes the current point position.</P>
     *
     * @param       a           operand 1,1 in the matrix
     * @param       b           operand 1,2 in the matrix
     * @param       c           operand 2,1 in the matrix
     * @param       d           operand 2,2 in the matrix
     * @param       x           operand 3,1 in the matrix
     * @param       y           operand 3,2 in the matrix
     */
    public void setTextMatrix(float a, float b, float c, float d, float x, float y) {
        state.xTLM = x;
        state.yTLM = y;
        content.append(a).append(' ').append(b).append_i(' ')
        .append(c).append_i(' ').append(d).append_i(' ')
        .append(x).append_i(' ').append(y).append(" Tm").append_i(separator);
    }

    /**
     * Changes the text matrix. The first four parameters are {1,0,0,1}.
     * <P>
     * Remark: this operation also initializes the current point position.</P>
     *
     * @param       x           operand 3,1 in the matrix
     * @param       y           operand 3,2 in the matrix
     */
    public void setTextMatrix(float x, float y) {
        setTextMatrix(1, 0, 0, 1, x, y);
    }

    /**
     * Moves to the start of the next line, offset from the start of the current line.
     *
     * @param       x           x-coordinate of the new current point
     * @param       y           y-coordinate of the new current point
     */
    public void moveText(float x, float y) {
        state.xTLM += x;
        state.yTLM += y;
        content.append(x).append(' ').append(y).append(" Td").append_i(separator);
    }

    /**
     * Gets the size of this content.
     *
     * @return the size of the content
     */
    int size() {
        return content.size();
    }

    /**
     * Escapes a <CODE>byte</CODE> array according to the PDF conventions.
     *
     * @param b the <CODE>byte</CODE> array to escape
     * @return an escaped <CODE>byte</CODE> array
     */
    static byte[] escapeString(byte[] b) {
        ByteBuffer content = new ByteBuffer();
        escapeString(b, content);
        return content.toByteArray();
    }

    /**
     * Escapes a <CODE>byte</CODE> array according to the PDF conventions.
     *
     * @param b the <CODE>byte</CODE> array to escape
     * @param content the content
     */
    static void escapeString(byte[] b, ByteBuffer content) {
        content.append_i('(');
        for (byte c : b) {
            switch (c) {
                case '\r':
                    content.append("\\r");
                    break;
                case '\n':
                    content.append("\\n");
                    break;
                case '\t':
                    content.append("\\t");
                    break;
                case '\b':
                    content.append("\\b");
                    break;
                case '\f':
                    content.append("\\f");
                    break;
                case '(':
                case ')':
                case '\\':
                    content.append_i('\\').append_i(c);
                    break;
                default:
                    content.append_i(c);
            }
        }
        content.append(")");
    }

    /**
     * Concatenate a matrix to the current transformation matrix.
     * @param a an element of the transformation matrix
     * @param b an element of the transformation matrix
     * @param c an element of the transformation matrix
     * @param d an element of the transformation matrix
     * @param e an element of the transformation matrix
     * @param f an element of the transformation matrix
     **/
    public void concatCTM(float a, float b, float c, float d, float e, float f) {
        content.append(a).append(' ').append(b).append(' ').append(c).append(' ');
        content.append(d).append(' ').append(e).append(' ').append(f).append(" cm").append_i(separator);
    }

    /**
     * Adds a template to this content.
     *
     * @param template the template
     * @param a an element of the transformation matrix
     * @param b an element of the transformation matrix
     * @param c an element of the transformation matrix
     * @param d an element of the transformation matrix
     * @param e an element of the transformation matrix
     * @param f an element of the transformation matrix
     */
    public void addTemplate(PdfTemplate template, float a, float b, float c, float d, float e, float f) {
        checkWriter();
        checkNoPattern(template);
        PdfName name = writer.addDirectTemplateSimple(template, null);
        PageResources prs = getPageResources();
        name = prs.addXObject(name, template.getIndirectReference());
        content.append("q ");
        content.append(a).append(' ');
        content.append(b).append(' ');
        content.append(c).append(' ');
        content.append(d).append(' ');
        content.append(e).append(' ');
        content.append(f).append(" cm ");
        content.append(name.getBytes()).append(" Do Q").append_i(separator);
    }

    /**
     * Changes the current color for filling paths (device dependent colors!).
     * <P>
     * Sets the color space to <B>DeviceCMYK</B> (or the <B>DefaultCMYK</B> color space),
     * and sets the color to use for filling paths.</P>
     * <P>
     * This method is described in the 'Portable Document Format Reference Manual version 1.3'
     * section 8.5.2.1 (page 331).</P>
     * <P>
     * Following the PDF manual, each operand must be a number between 0 (no ink) and
     * 1 (maximum ink). This method however accepts only integers between 0x00 and 0xFF.</P>
     *
     * @param cyan the intensity of cyan
     * @param magenta the intensity of magenta
     * @param yellow the intensity of yellow
     * @param black the intensity of black
     */

    public void setCMYKColorFill(int cyan, int magenta, int yellow, int black) {
        HelperCMYK(cyan, magenta, yellow, black);
        content.append(" k").append_i(separator);
    }
    /**
     * Changes the current color for stroking paths (device dependent colors!).
     * <P>
     * Sets the color space to <B>DeviceCMYK</B> (or the <B>DefaultCMYK</B> color space),
     * and sets the color to use for stroking paths.</P>
     * <P>
     * This method is described in the 'Portable Document Format Reference Manual version 1.3'
     * section 8.5.2.1 (page 331).</P>
     * Following the PDF manual, each operand must be a number between 0 (minimum intensity) and
     * 1 (maximum intensity). This method however accepts only integers between 0x00 and 0xFF.
     *
     * @param cyan the intensity of red
     * @param magenta the intensity of green
     * @param yellow the intensity of blue
     * @param black the intensity of black
     */

    public void setCMYKColorStroke(int cyan, int magenta, int yellow, int black) {
        HelperCMYK(cyan, magenta, yellow, black);
        content.append(" K").append_i(separator);
    }

    /**
     * Changes the current color for filling paths (device dependent colors!).
     * <P>
     * Sets the color space to <B>DeviceRGB</B> (or the <B>DefaultRGB</B> color space),
     * and sets the color to use for filling paths.</P>
     * <P>
     * This method is described in the 'Portable Document Format Reference Manual version 1.3'
     * section 8.5.2.1 (page 331).</P>
     * <P>
     * Following the PDF manual, each operand must be a number between 0 (minimum intensity) and
     * 1 (maximum intensity). This method however accepts only integers between 0x00 and 0xFF.</P>
     *
     * @param red the intensity of red
     * @param green the intensity of green
     * @param blue the intensity of blue
     */

    public void setRGBColorFill(int red, int green, int blue) {
        setRGBColorFill(red, green, blue, MAX_COLOR_VALUE);
    }
    public void setRGBColorFill(int red, int green, int blue, int alpha) {
        saveColorFill(new RGBColor(red, green, blue, alpha));
        HelperRGB(red, green, blue);
        content.append(" rg").append_i(separator);
    }

    /**
     * Changes the current color for stroking paths (device dependent colors!).
     * <P>
     * Sets the color space to <B>DeviceRGB</B> (or the <B>DefaultRGB</B> color space),
     * and sets the color to use for stroking paths.</P>
     * <P>
     * This method is described in the 'Portable Document Format Reference Manual version 1.3'
     * section 8.5.2.1 (page 331).</P>
     * Following the PDF manual, each operand must be a number between 0 (minimum intensity) and
     * 1 (maximum intensity). This method however accepts only integers between 0x00 and 0xFF.
     *
     * @param red the intensity of red
     * @param green the intensity of green
     * @param blue the intensity of blue
     */

    public void setRGBColorStroke(int red, int green, int blue) {
        setRGBColorStroke(red, green, blue, MAX_COLOR_VALUE);
    }
    public void setRGBColorStroke(int red, int green, int blue, int alpha) {
        saveColorStroke(new RGBColor(red, green, blue, alpha));
        HelperRGB(red, green, blue);
        content.append(" RG").append_i(separator);
    }

    /** Sets the stroke color. <CODE>color</CODE> can be an
     * <CODE>ExtendedColor</CODE>.
     * @param color the color
     */
    public void setColorStroke(Color color) {
        PdfXConformanceImp.checkPDFXConformance(writer, PdfXConformanceImp.PDFXKEY_COLOR, color);
        int type = ExtendedColor.getType(color);
        switch (type) {
            case ExtendedColor.TYPE_GRAY: {
                final GrayColor grayColor = (GrayColor) color;
                setGrayStroke(grayColor.getGray(), grayColor.getAlpha());
                break;
            }
            case ExtendedColor.TYPE_CMYK: {
                CMYKColor cmyk = (CMYKColor)color;
                setCMYKColorStrokeF(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack(), cmyk.getAlpha());
                break;
            }
            case ExtendedColor.TYPE_SEPARATION: {
                SpotColor spot = (SpotColor)color;
                setColorStroke(spot.getPdfSpotColor(), spot.getTint());
                break;
            }
            case ExtendedColor.TYPE_PATTERN: {
                PatternColor pat = (PatternColor) color;
                setPatternStroke(pat.getPainter());
                break;
            }
            case ExtendedColor.TYPE_SHADING: {
                ShadingColor shading = (ShadingColor) color;
                setShadingStroke(shading.getPdfShadingPattern());
                break;
            }
            default:
                setRGBColorStroke(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
    }

    private void saveColorStroke(ExtendedColor extendedColor) {
        PdfGState gState = new PdfGState();
        gState.setStrokeOpacity(extendedColor.getAlpha() / MAX_INT_COLOR_VALUE);
        setGState(gState);

        state.colorStroke = extendedColor;
    }

    /** Sets the fill color. <CODE>color</CODE> can be an
     * <CODE>ExtendedColor</CODE>.
     * @param color the color
     */
    public void setColorFill(Color color) {
        PdfXConformanceImp.checkPDFXConformance(writer, PdfXConformanceImp.PDFXKEY_COLOR, color);
        int type = ExtendedColor.getType(color);
        switch (type) {
            case ExtendedColor.TYPE_GRAY: {
                GrayColor grayColor = (GrayColor) color;
                setGrayFill(grayColor.getGray(), color.getAlpha() / MAX_INT_COLOR_VALUE);
                break;
            }
            case ExtendedColor.TYPE_CMYK: {
                CMYKColor cmyk = (CMYKColor) color;
                setCMYKColorFillF(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack(), cmyk.getAlpha() / MAX_INT_COLOR_VALUE);
                break;
            }
            case ExtendedColor.TYPE_SEPARATION: {
                SpotColor spot = (SpotColor) color;
                setColorFill(spot.getPdfSpotColor(), spot.getTint());
                break;
            }
            case ExtendedColor.TYPE_PATTERN: {
                PatternColor pat = (PatternColor) color;
                setPatternFill(pat.getPainter());
                break;
            }
            case ExtendedColor.TYPE_SHADING: {
                ShadingColor shading = (ShadingColor) color;
                setShadingFill(shading.getPdfShadingPattern());
                break;
            }
            default:
                setRGBColorFill(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
    }

    private void saveColorFill(ExtendedColor extendedColor) {
        PdfGState gState = new PdfGState();
        gState.setFillOpacity(extendedColor.getAlpha() / MAX_INT_COLOR_VALUE);
        setGState(gState);

        state.colorFill = extendedColor;
    }

    /** Sets the fill color to a spot color.
     * @param sp the spot color
     * @param tint the tint for the spot color. 0 is no color and 1
     * is 100% color
     */
    public void setColorFill(PdfSpotColor sp, float tint) {
        checkWriter();
        saveColorFill(new SpotColor(sp,tint));
        state.colorDetails = writer.addSimple(sp);
        PageResources prs = getPageResources();
        PdfName name = state.colorDetails.getColorName();
        name = prs.addColor(name, state.colorDetails.getIndirectReference());
        content.append(name.getBytes()).append(" cs ").append(tint).append(" scn").append_i(separator);
    }

    /** Sets the stroke color to a spot color.
     * @param sp the spot color
     * @param tint the tint for the spot color. 0 is no color and 1
     * is 100% color
     */
    public void setColorStroke(PdfSpotColor sp, float tint) {
        checkWriter();
        saveColorStroke(new SpotColor(sp,tint));
        state.colorDetails = writer.addSimple(sp);
        PageResources prs = getPageResources();
        PdfName name = state.colorDetails.getColorName();
        name = prs.addColor(name, state.colorDetails.getIndirectReference());
        content.append(name.getBytes()).append(" CS ").append(tint).append(" SCN").append_i(separator);
    }

    /** Sets the fill color to a pattern. The pattern can be
     * colored or uncolored.
     * @param p the pattern
     */
    public void setPatternFill(PdfPatternPainter p) {
        if (p.isStencil()) {
            setPatternFill(p, p.getDefaultColor());
            return;
        }
        checkWriter();
        saveColorFill(new PatternColor(p));
        PageResources prs = getPageResources();
        PdfName name = writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(name.getBytes()).append(" scn").append_i(separator);
    }

    /** Outputs the color values to the content.
     * @param color The color
     * @param tint the tint if it is a spot color, ignored otherwise
     */
    void outputColorNumbers(Color color, float tint) {
        PdfXConformanceImp.checkPDFXConformance(writer, PdfXConformanceImp.PDFXKEY_COLOR, color);
        int type = ExtendedColor.getType(color);
        switch (type) {
            case ExtendedColor.TYPE_RGB:
                content.append((float)(color.getRed()) / MAX_INT_COLOR_VALUE);
                content.append(' ');
                content.append((float)(color.getGreen()) / MAX_INT_COLOR_VALUE);
                content.append(' ');
                content.append((float)(color.getBlue()) / MAX_INT_COLOR_VALUE);
                break;
            case ExtendedColor.TYPE_GRAY:
                content.append(((GrayColor)color).getGray());
                break;
            case ExtendedColor.TYPE_CMYK: {
                CMYKColor cmyk = (CMYKColor)color;
                content.append(cmyk.getCyan()).append(' ').append(cmyk.getMagenta());
                content.append(' ').append(cmyk.getYellow()).append(' ').append(cmyk.getBlack());
                break;
            }
            case ExtendedColor.TYPE_SEPARATION:
                content.append(tint);
                break;
            default:
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.color.type"));
        }
    }

    /** Sets the fill color to an uncolored pattern.
     * @param p the pattern
     * @param color the color of the pattern
     */
    public void setPatternFill(PdfPatternPainter p, Color color) {
        if (ExtendedColor.getType(color) == ExtendedColor.TYPE_SEPARATION)
            setPatternFill(p, color, ((SpotColor)color).getTint());
        else
            setPatternFill(p, color, 0);
    }

    /** Sets the fill color to an uncolored pattern.
     * @param p the pattern
     * @param color the color of the pattern
     * @param tint the tint if the color is a spot color, ignored otherwise
     */
    public void setPatternFill(PdfPatternPainter p, Color color, float tint) {
        checkWriter();
        if (!p.isStencil()) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.pattern.was.expected"));
        }
        saveColorFill(new RGBColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
        PageResources prs = getPageResources();
        PdfName name = writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        ColorDetails csDetail = writer.addSimplePatternColorspace(color);
        PdfName cName = prs.addColor(csDetail.getColorName(), csDetail.getIndirectReference());
        content.append(cName.getBytes()).append(" cs").append_i(separator);
        outputColorNumbers(color, tint);
        content.append(' ').append(name.getBytes()).append(" scn").append_i(separator);
    }

    /** Sets the stroke color to an uncolored pattern.
     * @param p the pattern
     * @param color the color of the pattern
     */
    public void setPatternStroke(PdfPatternPainter p, Color color) {
        if (ExtendedColor.getType(color) == ExtendedColor.TYPE_SEPARATION)
            setPatternStroke(p, color, ((SpotColor)color).getTint());
        else
            setPatternStroke(p, color, 0);
    }

    /** Sets the stroke color to an uncolored pattern.
     * @param p the pattern
     * @param color the color of the pattern
     * @param tint the tint if the color is a spot color, ignored otherwise
     */
    public void setPatternStroke(PdfPatternPainter p, Color color, float tint) {
        checkWriter();
        if (!p.isStencil())
            throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.pattern.was.expected"));
        saveColorStroke(new PatternColor(p));
        PageResources prs = getPageResources();
        PdfName name = writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        ColorDetails csDetail = writer.addSimplePatternColorspace(color);
        PdfName cName = prs.addColor(csDetail.getColorName(), csDetail.getIndirectReference());
        content.append(cName.getBytes()).append(" CS").append_i(separator);
        outputColorNumbers(color, tint);
        content.append(' ').append(name.getBytes()).append(" SCN").append_i(separator);
    }

    /** Sets the stroke color to a pattern. The pattern can be
     * colored or uncolored.
     * @param p the pattern
     */
    public void setPatternStroke(PdfPatternPainter p) {
        if (p.isStencil()) {
            setPatternStroke(p, p.getDefaultColor());
            return;
        }
        checkWriter();
        saveColorStroke(new PatternColor(p));
        PageResources prs = getPageResources();
        PdfName name = writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(name.getBytes()).append(" SCN").append_i(separator);
    }

    /**
     * Sets the shading fill pattern.
     * @param shading the shading pattern
     */
    public void setShadingFill(PdfShadingPattern shading) {
        checkWriter();
        saveColorFill(new ShadingColor(shading));
        writer.addSimpleShadingPattern(shading);
        PageResources prs = getPageResources();
        PdfName name = prs.addPattern(shading.getPatternName(), shading.getPatternReference());
        content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(name.getBytes()).append(" scn").append_i(separator);
        ColorDetails details = shading.getColorDetails();
        if (details != null)
            prs.addColor(details.getColorName(), details.getIndirectReference());
    }

    /**
     * Sets the shading stroke pattern
     * @param shading the shading pattern
     */
    public void setShadingStroke(PdfShadingPattern shading) {
        checkWriter();
        saveColorStroke(new ShadingColor(shading));
        writer.addSimpleShadingPattern(shading);
        PageResources prs = getPageResources();
        PdfName name = prs.addPattern(shading.getPatternName(), shading.getPatternReference());
        content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(name.getBytes()).append(" SCN").append_i(separator);
        ColorDetails details = shading.getColorDetails();
        if (details != null)
            prs.addColor(details.getColorName(), details.getIndirectReference());
    }

    /** Check if we have a valid PdfWriter.
     *
     */
    protected void checkWriter() {
        if (writer == null)
            throw new NullPointerException(MessageLocalization.getComposedMessage("the.writer.in.pdfcontentbyte.is.null"));
    }

    /**
     * Show an array of text.
     * @param text array of text
     */
    public void showText(PdfTextArray text) {
        if (state.fontDetails == null)
            throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text"));
        content.append("[");
        List<Object> arrayList = text.getArrayList();
        boolean lastWasNumber = false;
        for (Object obj : arrayList) {
            if (obj instanceof String) {
                showText2((String) obj);
                lastWasNumber = false;
            } else {
                if (lastWasNumber)
                    content.append(' ');
                else
                    lastWasNumber = true;
                content.append((Float) obj);
            }
        }
        content.append("]TJ").append_i(separator);
    }

    /**
     * Gets the <CODE>PdfWriter</CODE> in use by this object.
     * @return the <CODE>PdfWriter</CODE> in use by this object
     */
    public PdfWriter getPdfWriter() {
        return writer;
    }

    /**
     * Gets the <CODE>PdfDocument</CODE> in use by this object.
     * @return the <CODE>PdfDocument</CODE> in use by this object
     */
    public PdfDocument getPdfDocument() {
        return pdf;
    }



    /**
     * Gets a duplicate of this <CODE>PdfContentByte</CODE>. All
     * the members are copied by reference but the buffer stays different.
     *
     * @return a copy of this <CODE>PdfContentByte</CODE>
     */
    public PdfContentByte getDuplicate() {
        return new PdfContentByte(writer);
    }


    /** Throws an error if it is a pattern.
     * @param t the object to check
     */
    void checkNoPattern(PdfTemplate t) {
        if (t.getType() == PdfTemplate.TYPE_PATTERN)
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.use.of.a.pattern.a.template.was.expected"));
    }


    PageResources getPageResources() {
        return pdf.getPageResources();
    }

    /** Sets the graphic state
     * @param gstate the graphic state
     */
    public void setGState(PdfGState gstate) {
        PdfObject[] obj = writer.addSimpleExtGState(gstate);
        PageResources prs = getPageResources();
        PdfName name = prs.addExtGState((PdfName)obj[0], (PdfIndirectReference)obj[1]);
        state.extGState = gstate;
        content.append(name.getBytes()).append(" gs").append_i(separator);
    }

    /**
     * Begins a graphic block whose visibility is controlled by the <CODE>layer</CODE>.
     * Blocks can be nested. Each block must be terminated by an {@link #endLayer()}.<p>
     * Note that nested layers with {@link PdfLayer#addChild(PdfLayer)} only require a single
     * call to this method and a single call to {@link #endLayer()}; all the nesting control
     * is built in.
     * @param layer the layer
     */
    public void beginLayer(PdfOCG layer) {
        if ((layer instanceof PdfLayer) && ((PdfLayer)layer).getTitle() != null)
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("a.title.is.not.a.layer"));
        if (layerDepth == null)
            layerDepth = new ArrayList<>();
        if (layer instanceof PdfLayerMembership) {
            layerDepth.add(1);
            beginLayer2(layer);
        } else if (layer instanceof PdfLayer) {
            PdfLayer la = (PdfLayer) layer;
            int n = 0;
            while (la != null) {
                if (la.getTitle() == null) {
                    beginLayer2(la);
                    ++n;
                }
                la = la.getParent();
            }
            layerDepth.add(n);
        }
    }

    private void beginLayer2(PdfOCG layer) {
        PdfName name = (PdfName)writer.addSimpleProperty(layer, layer.getRef())[0];
        PageResources prs = getPageResources();
        name = prs.addProperty(name, layer.getRef());
        content.append("/OC ").append(name.getBytes()).append(" BDC").append_i(separator);
    }

    /**
     * Ends a layer controlled graphic block. It will end the most recent open block.
     */
    public void endLayer() {
        int n;
        if (layerDepth != null && !layerDepth.isEmpty()) {
            n = layerDepth.get(layerDepth.size() - 1);
            layerDepth.remove(layerDepth.size() - 1);
        } else {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.layer.operators"));
        }
        while (n-- > 0)
            content.append("EMC").append_i(separator);
    }

    /**
     * Checks for any dangling state: Mismatched save/restore state, begin/end text,
     * begin/end layer, or begin/end marked content sequence.
     * If found, this function will throw.  This function is called automatically
     * during a reset() (from Document.newPage() for example), and before writing 
     * itself out in toPdf().
     * One possible cause: not calling myPdfGraphics2D.dispose() will leave dangling
     *                     saveState() calls.
     * @since 2.1.6
     * @throws IllegalPdfSyntaxException (a runtime exception)
     */
    public void sanityCheck() {
        if (inText) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators"));
        }
        if (layerDepth != null && !layerDepth.isEmpty()) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.layer.operators"));
        }
        if (!stateList.isEmpty()) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.save.restore.state.operators"));
        }
    }
}

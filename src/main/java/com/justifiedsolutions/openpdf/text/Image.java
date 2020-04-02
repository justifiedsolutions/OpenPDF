/*
 * $Id: Image.java 4065 2009-09-16 23:09:11Z psoares33 $
 *
 * Copyright 1999, 2000, 2001, 2002 by Bruno Lowagie.
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

package com.justifiedsolutions.openpdf.text;

import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import com.justifiedsolutions.openpdf.text.pdf.PdfArray;
import com.justifiedsolutions.openpdf.text.pdf.PdfDictionary;
import com.justifiedsolutions.openpdf.text.pdf.PdfIndirectReference;
import com.justifiedsolutions.openpdf.text.pdf.PdfName;
import com.justifiedsolutions.openpdf.text.pdf.PdfOCG;
import com.justifiedsolutions.openpdf.text.pdf.PdfObject;
import com.justifiedsolutions.openpdf.text.pdf.PdfStream;
import com.justifiedsolutions.openpdf.text.pdf.PdfTemplate;
import java.awt.color.ICC_Profile;
import java.lang.reflect.Constructor;
import java.net.URL;


/**
 * An <CODE>Image</CODE> is the representation of a graphic element (JPEG, PNG
 * or GIF) that has to be inserted into the document
 *
 * @see Element
 * @see Rectangle
 */

public abstract class Image extends Rectangle {

    // static final membervariables

    /** this is a kind of image alignment. */
    public static final int DEFAULT = 0;

    /** this is a kind of image alignment. */
    public static final int RIGHT = 2;

    /** this is a kind of image alignment. */
    public static final int LEFT = 0;

    /** this is a kind of image alignment. */
    public static final int MIDDLE = 1;

    /** this is a kind of image alignment. */
    public static final int TEXTWRAP = 4;

    /** this is a kind of image alignment. */
    public static final int UNDERLYING = 8;

    /** This represents a coordinate in the transformation matrix. */
    public static final int AX = 0;

    /** This represents a coordinate in the transformation matrix. */
    public static final int AY = 1;

    /** This represents a coordinate in the transformation matrix. */
    public static final int BX = 2;

    /** This represents a coordinate in the transformation matrix. */
    public static final int BY = 3;

    /** This represents a coordinate in the transformation matrix. */
    public static final int CX = 4;

    /** This represents a coordinate in the transformation matrix. */
    public static final int CY = 5;

    /** This represents a coordinate in the transformation matrix. */
    public static final int DX = 6;

    /** This represents a coordinate in the transformation matrix. */
    public static final int DY = 7;

    /** type of image */
    public static final int ORIGINAL_NONE = 0;

    /**
     * type of image
     * @since    2.1.5
     */
    public static final int ORIGINAL_JBIG2 = 9;

    // member variables

    /** The image type. */
    protected int type;

    /** The URL of the image. */
    protected URL url;

    /**
     * The raw data of the image.
     */
    protected byte[] rawData;

    /** The bits per component of the raw image. It also flags a CCITT image. */
    protected int bpc = 1;

    /**
     * The template to be treated as an image.
     */
    protected PdfTemplate[] template = new PdfTemplate[1];

    /** The alignment of the Image. */
    protected int alignment;

    /** Text that can be shown instead of the image. */
    protected String alt;

    /** This is the absolute X-position of the image. */
    protected float absoluteX = Float.NaN;

    /** This is the absolute Y-position of the image. */
    protected float absoluteY = Float.NaN;

    /** This is the width of the image without rotation. */
    protected float plainWidth;

    /** This is the width of the image without rotation. */
    protected float plainHeight;

    /** This is the scaled width of the image taking rotation into account. */
    protected float scaledWidth;

    /** This is the original height of the image taking rotation into account. */
    protected float scaledHeight;

    /**
     * The compression level of the content streams.
     * @since    2.1.3
     */
    protected int compressionLevel = PdfStream.DEFAULT_COMPRESSION;

    /** an iText attributed unique id for this image. */
    protected Long mySerialId = getSerialId();

    public static final int[] PNGID = {137, 80, 78, 71, 13, 10, 26, 10};


    // image from file or URL

    /**
     * Constructs an <CODE>Image</CODE> -object, using an <VAR>url </VAR>.
     *
     * @param url
     *            the <CODE>URL</CODE> where the image can be found.
     */
    public Image(URL url) {
        super(0, 0);
        this.url = url;
        this.alignment = DEFAULT;
        rotationRadians = 0;
    }


    /**
     * Holds value of property directReference.
     * An image is embedded into a PDF as an Image XObject.
     * This object is referenced by a PdfIndirectReference object.
     */
    private PdfIndirectReference directReference;

    /**
     * Getter for property directReference.
     * @return Value of property directReference.
     */
    public PdfIndirectReference getDirectReference() {
        return this.directReference;
    }

    /**
     * Setter for property directReference.
     * @param directReference New value of property directReference.
     */
    public void setDirectReference(PdfIndirectReference directReference) {
        this.directReference = directReference;
    }


    // copy constructor

    /**
     * Constructs an <CODE>Image</CODE> -object, using an <VAR>url </VAR>.
     *
     * @param image
     *            another Image object.
     */
    protected Image(Image image) {
        super(image);
        this.type = image.type;
        this.url = image.url;
        this.rawData = image.rawData;
        this.bpc = image.bpc;
        this.template = image.template;
        this.alignment = image.alignment;
        this.alt = image.alt;
        this.absoluteX = image.absoluteX;
        this.absoluteY = image.absoluteY;
        this.plainWidth = image.plainWidth;
        this.plainHeight = image.plainHeight;
        this.scaledWidth = image.scaledWidth;
        this.scaledHeight = image.scaledHeight;
        this.mySerialId = image.mySerialId;

        this.directReference = image.directReference;

        this.rotationRadians = image.rotationRadians;
        this.initialRotation = image.initialRotation;
        this.indentationLeft = image.indentationLeft;
        this.indentationRight = image.indentationRight;
        this.spacingBefore = image.spacingBefore;
        this.spacingAfter = image.spacingAfter;

        this.widthPercentage = image.widthPercentage;
        this.layer = image.layer;
        this.interpolation = image.interpolation;
        this.originalType = image.originalType;
        this.originalData = image.originalData;
        this.deflated = image.deflated;
        this.dpiX = image.dpiX;
        this.dpiY = image.dpiY;
        this.XYRatio = image.XYRatio;

        this.colorspace = image.colorspace;
        this.invert = image.invert;
        this.profile = image.profile;
        this.additional = image.additional;
        this.mask = image.mask;
        this.imageMask = image.imageMask;
        this.smask = image.smask;
        this.transparency = image.transparency;
    }

    /**
     * gets an instance of an Image
     *
     * @param image
     *            an Image object
     * @return a new Image object
     */
    public static Image getInstance(Image image) {
        if (image == null)
            return null;
        try {
            Class<? extends Image> cs = image.getClass();
            Constructor<? extends Image> constructor = cs.getDeclaredConstructor(Image.class);
            return constructor.newInstance(image);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    // implementation of the Element interface

    /**
     * Returns the type.
     *
     * @return a type
     */

    public int type() {
        return type;
    }

    /**
     * @see Element#isNestable()
     * @since    iText 2.0.8
     */
    public boolean isNestable() {
        return true;
    }

    // checking the type of Image

    /**
     * Returns <CODE>true</CODE> if the image is a <CODE>Jpeg</CODE>
     * -object.
     *
     * @return a <CODE>boolean</CODE>
     */

    public boolean isJpeg() {
        return type == JPEG;
    }

    /**
     * Returns <CODE>true</CODE> if the image is a <CODE>ImgRaw</CODE>
     * -object.
     *
     * @return a <CODE>boolean</CODE>
     */

    public boolean isImgRaw() {
        return type == IMGRAW;
    }

    /**
     * Returns <CODE>true</CODE> if the image is an <CODE>ImgTemplate</CODE>
     * -object.
     *
     * @return a <CODE>boolean</CODE>
     */

    public boolean isImgTemplate() {
        return type == IMGTEMPLATE;
    }

    // getters and setters

    /**
     * Gets the <CODE>String</CODE> -representation of the reference to the
     * image.
     *
     * @return a <CODE>String</CODE>
     */

    public URL getUrl() {
        return url;
    }

    /**
     * Sets the url of the image
     *
     * @param url
     *            the url of the image
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * Gets the raw data for the image.
     * <P>
     * Remark: this only makes sense for Images of the type <CODE>RawImage
     * </CODE>.
     *
     * @return the raw data
     */
    public byte[] getRawData() {
        return rawData;
    }

    /**
     * Gets the bpc for the image.
     * <P>
     * Remark: this only makes sense for Images of the type <CODE>RawImage
     * </CODE>.
     *
     * @return a bpc value
     */
    public int getBpc() {
        return bpc;
    }

    /**
     * Gets the template to be used as an image.
     * <P>
     * Remark: this only makes sense for Images of the type <CODE>ImgTemplate
     * </CODE>.
     *
     * @return the template
     */
    public PdfTemplate getTemplateData() {
        return template[0];
    }

    /**
     * Sets data from a PdfTemplate
     *
     * @param template
     *            the template with the content
     */
    public void setTemplateData(PdfTemplate template) {
        this.template[0] = template;
    }

    /**
     * Gets the alignment for the image.
     *
     * @return a value
     */
    public int getAlignment() {
        return alignment;
    }

    /**
     * Sets the alignment for the image.
     *
     * @param alignment
     *            the alignment
     */

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    /**
     * Gets the alternative text for the image.
     *
     * @return a <CODE>String</CODE>
     */

    public String getAlt() {
        return alt;
    }

    /**
     * Sets the alternative information for the image.
     *
     * @param alt
     *            the alternative information
     */

    public void setAlt(String alt) {
        this.alt = alt;
    }

    /**
     * Sets the absolute position of the <CODE>Image</CODE>.
     *
     * @param absoluteX
     * @param absoluteY
     */

    public void setAbsolutePosition(float absoluteX, float absoluteY) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
    }

    /**
     * Checks if the <CODE>Images</CODE> has to be added at an absolute X
     * position.
     *
     * @return a boolean
     */
    public boolean hasAbsoluteX() {
        return !Float.isNaN(absoluteX);
    }

    /**
     * Returns the absolute X position.
     *
     * @return a position
     */
    public float getAbsoluteX() {
        return absoluteX;
    }

    /**
     * Checks if the <CODE>Images</CODE> has to be added at an absolute
     * position.
     *
     * @return a boolean
     */
    public boolean hasAbsoluteY() {
        return !Float.isNaN(absoluteY);
    }

    /**
     * Returns the absolute Y position.
     *
     * @return a position
     */
    public float getAbsoluteY() {
        return absoluteY;
    }

    // width and height

    /**
     * Gets the scaled width of the image.
     *
     * @return a value
     */
    public float getScaledWidth() {
        return scaledWidth;
    }

    /**
     * Gets the scaled height of the image.
     *
     * @return a value
     */
    public float getScaledHeight() {
        return scaledHeight;
    }

    /**
     * Gets the plain width of the image.
     *
     * @return a value
     */
    public float getPlainWidth() {
        return plainWidth;
    }

    /**
     * Gets the plain height of the image.
     *
     * @return a value
     */
    public float getPlainHeight() {
        return plainHeight;
    }

    /**
     * Scale the image to an absolute width and an absolute height.
     *
     * @param newWidth
     *            the new width
     * @param newHeight
     *            the new height
     */
    public void scaleAbsolute(float newWidth, float newHeight) {
        plainWidth = newWidth;
        plainHeight = newHeight;
        float[] matrix = matrix();
        scaledWidth = matrix[DX] - matrix[CX];
        scaledHeight = matrix[DY] - matrix[CY];
        setWidthPercentage(0);
    }

    /**
     * Scale the image to an absolute width.
     *
     * @param newWidth
     *            the new width
     */
    public void scaleAbsoluteWidth(float newWidth) {
        plainWidth = newWidth;
        float[] matrix = matrix();
        scaledWidth = matrix[DX] - matrix[CX];
        scaledHeight = matrix[DY] - matrix[CY];
        setWidthPercentage(0);
    }

    /**
     * Scale the image to an absolute height.
     *
     * @param newHeight
     *            the new height
     */
    public void scaleAbsoluteHeight(float newHeight) {
        plainHeight = newHeight;
        float[] matrix = matrix();
        scaledWidth = matrix[DX] - matrix[CX];
        scaledHeight = matrix[DY] - matrix[CY];
        setWidthPercentage(0);
    }

    /**
     * Scale the image to a certain percentage.
     *
     * @param percent
     *            the scaling percentage
     */
    public void scalePercent(float percent) {
        scalePercent(percent, percent);
    }

    /**
     * Scale the width and height of an image to a certain percentage.
     *
     * @param percentX
     *            the scaling percentage of the width
     * @param percentY
     *            the scaling percentage of the height
     */
    public void scalePercent(float percentX, float percentY) {
        plainWidth = (getWidth() * percentX) / 100f;
        plainHeight = (getHeight() * percentY) / 100f;
        float[] matrix = matrix();
        scaledWidth = matrix[DX] - matrix[CX];
        scaledHeight = matrix[DY] - matrix[CY];
        setWidthPercentage(0);
    }

    /**
     * Scales the image so that it fits a certain width and height.
     *
     * @param fitWidth
     *            the width to fit
     * @param fitHeight
     *            the height to fit
     */
    public void scaleToFit(float fitWidth, float fitHeight) {
        scalePercent(100);
        float percentX = (fitWidth * 100) / getScaledWidth();
        float percentY = (fitHeight * 100) / getScaledHeight();
        scalePercent(percentX < percentY ? percentX : percentY);
        setWidthPercentage(0);
    }

    /**
     * Returns the transformation matrix of the image.
     *
     * @return an array [AX, AY, BX, BY, CX, CY, DX, DY]
     */
    public float[] matrix() {
        float[] matrix = new float[8];
        float cosX = (float) Math.cos(rotationRadians);
        float sinX = (float) Math.sin(rotationRadians);
        matrix[AX] = plainWidth * cosX;
        matrix[AY] = plainWidth * sinX;
        matrix[BX] = (-plainHeight) * sinX;
        matrix[BY] = plainHeight * cosX;
        if (rotationRadians < Math.PI / 2f) {
            matrix[CX] = matrix[BX];
            matrix[CY] = 0;
            matrix[DX] = matrix[AX];
            matrix[DY] = matrix[AY] + matrix[BY];
        } else if (rotationRadians < Math.PI) {
            matrix[CX] = matrix[AX] + matrix[BX];
            matrix[CY] = matrix[BY];
            matrix[DX] = 0;
            matrix[DY] = matrix[AY];
        } else if (rotationRadians < Math.PI * 1.5f) {
            matrix[CX] = matrix[AX];
            matrix[CY] = matrix[AY] + matrix[BY];
            matrix[DX] = matrix[BX];
            matrix[DY] = 0;
        } else {
            matrix[CX] = 0;
            matrix[CY] = matrix[AY];
            matrix[DX] = matrix[AX] + matrix[BX];
            matrix[DY] = matrix[BY];
        }
        return matrix;
    }

    // serial stamping

    /** a static that is used for attributing a unique id to each image. */
    static long serialId = 0;

    /** Creates a new serial id. */
    static protected synchronized Long getSerialId() {
        ++serialId;
        return serialId;
    }

    /**
     * Returns a serial id for the Image (reuse the same image more than once)
     *
     * @return a serialId
     */
    public Long getMySerialId() {
        return mySerialId;
    }

    // rotation, note that the superclass also has a rotation value.

    /** This is the rotation of the image in radians. */
    protected float rotationRadians;

    /** Holds value of property initialRotation. */
    private float initialRotation;

    /**
     * Gets the current image rotation in radians.
     * @return the current image rotation in radians
     */
    public float getImageRotation() {
        float d = 2.0F * (float) Math.PI;
        float rot = ((rotationRadians - initialRotation) % d);
        if (rot < 0) {
            rot += d;
        }
        return rot;
    }

    /**
     * Sets the rotation of the image in radians.
     *
     * @param r
     *            rotation in radians
     */
    public void setRotation(float r) {
        float d = 2.0F * (float) Math.PI;
        rotationRadians = ((r + initialRotation) % d);
        if (rotationRadians < 0) {
            rotationRadians += d;
        }
        float[] matrix = matrix();
        scaledWidth = matrix[DX] - matrix[CX];
        scaledHeight = matrix[DY] - matrix[CY];
    }

    /**
     * Sets the rotation of the image in degrees.
     *
     * @param deg
     *            rotation in degrees
     */
    public void setRotationDegrees(float deg) {
        float d = (float) Math.PI;
        setRotation(deg / 180 * d);
    }

    /**
     * Getter for property initialRotation.
     * @return Value of property initialRotation.
     */
    public float getInitialRotation() {
        return this.initialRotation;
    }

    /**
     * Some image formats, like TIFF may present the images rotated that have
     * to be compensated.
     * @param initialRotation New value of property initialRotation.
     */
    public void setInitialRotation(float initialRotation) {
        float old_rot = rotationRadians - this.initialRotation;
        this.initialRotation = initialRotation;
        setRotation(old_rot);
    }

    // indentations

    /** the indentation to the left. */
    protected float indentationLeft = 0;

    /** the indentation to the right. */
    protected float indentationRight = 0;

    /** The spacing before the image. */
    protected float spacingBefore;

    /** The spacing after the image. */
    protected float spacingAfter;

    /**
     * Gets the left indentation.
     *
     * @return the left indentation
     */
    public float getIndentationLeft() {
        return indentationLeft;
    }

    /**
     * Sets the left indentation.
     *
     * @param f
     */
    public void setIndentationLeft(float f) {
        indentationLeft = f;
    }

    /**
     * Gets the right indentation.
     *
     * @return the right indentation
     */
    public float getIndentationRight() {
        return indentationRight;
    }

    /**
     * Sets the right indentation.
     *
     * @param f
     */
    public void setIndentationRight(float f) {
        indentationRight = f;
    }

    /**
     * Gets the spacing before this image.
     *
     * @return the spacing
     */
    public float getSpacingBefore() {
        return spacingBefore;
    }

    /**
     * Sets the spacing before this image.
     *
     * @param spacing
     *            the new spacing
     */

    public void setSpacingBefore(float spacing) {
        this.spacingBefore = spacing;
    }

    /**
     * Gets the spacing before this image.
     *
     * @return the spacing
     */
    public float getSpacingAfter() {
        return spacingAfter;
    }

    /**
     * Sets the spacing after this image.
     *
     * @param spacing
     *            the new spacing
     */

    public void setSpacingAfter(float spacing) {
        this.spacingAfter = spacing;
    }

    // widthpercentage (for the moment only used in ColumnText)

    /**
     * Holds value of property widthPercentage.
     */
    private float widthPercentage = 100;

    /**
     * Getter for property widthPercentage.
     *
     * @return Value of property widthPercentage.
     */
    public float getWidthPercentage() {
        return this.widthPercentage;
    }

    /**
     * Setter for property widthPercentage.
     *
     * @param widthPercentage
     *            New value of property widthPercentage.
     */
    public void setWidthPercentage(float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }

    // Optional Content

    /** Optional Content layer to which we want this Image to belong. */
    protected PdfOCG layer;

    /**
     * Gets the layer this image belongs to.
     *
     * @return the layer this image belongs to or <code>null</code> for no
     *         layer defined
     */
    public PdfOCG getLayer() {
        return layer;
    }

    /**
     * Sets the layer this image belongs to.
     *
     * @param layer
     *            the layer this image belongs to
     */
    public void setLayer(PdfOCG layer) {
        this.layer = layer;
    }

    // interpolation

    /** Holds value of property interpolation. */
    protected boolean interpolation;

    /**
     * Getter for property interpolation.
     *
     * @return Value of property interpolation.
     */
    public boolean isInterpolation() {
        return interpolation;
    }

    // original type and data

    /** Holds value of property originalType. */
    protected int originalType = ORIGINAL_NONE;

    /** Holds value of property originalData. */
    protected byte[] originalData;

    // the following values are only set for specific types of images.

    /** Holds value of property deflated. */
    protected boolean deflated = false;

    /**
     * Getter for property deflated.
     *
     * @return Value of property deflated.
     *
     */
    public boolean isDeflated() {
        return this.deflated;
    }

    // DPI info

    /** Holds value of property dpiX. */
    protected int dpiX = 0;

    /** Holds value of property dpiY. */
    protected int dpiY = 0;

    // XY Ratio

    /** Holds value of property XYRatio. */
    private float XYRatio = 0;

    // color, colorspaces and transparency

    /** this is the colorspace of a jpeg-image. */
    protected int colorspace = -1;

    /**
     * Gets the colorspace for the image.
     * <P>
     * Remark: this only makes sense for Images of the type <CODE>Jpeg</CODE>.
     *
     * @return a colorspace value
     */
    public int getColorspace() {
        return colorspace;
    }

    /** Image color inversion */
    protected boolean invert = false;

    /**
     * Getter for the inverted value
     *
     * @return true if the image is inverted
     */
    public boolean isInverted() {
        return invert;
    }

    /**
     * Sets inverted true or false
     *
     * @param invert
     *            true or false
     */
    public void setInverted(boolean invert) {
        this.invert = invert;
    }

    /** ICC Profile attached */
    protected ICC_Profile profile = null;

    /**
     * Checks is the image has an ICC profile.
     *
     * @return the ICC profile or <CODE>null</CODE>
     */
    public boolean hasICCProfile() {
        return (this.profile != null);
    }

    /**
     * Gets the images ICC profile.
     *
     * @return the ICC profile
     */
    public ICC_Profile getICCProfile() {
        return profile;
    }

    /** a dictionary with additional information */
    private PdfDictionary additional = null;

    /**
     * Getter for the dictionary with additional information.
     *
     * @return a PdfDictionary with additional information.
     */
    public PdfDictionary getAdditional() {
        return this.additional;
    }

    /**
     * Sets the /Colorspace key.
     *
     * @param additional
     *            a PdfDictionary with additional information.
     */
    public void setAdditional(PdfDictionary additional) {
        this.additional = additional;
    }

    /**
     * Replaces CalRGB and CalGray colorspaces with DeviceRGB and DeviceGray.
     */
    public void simplifyColorspace() {
        if (additional == null)
            return;
        PdfArray value = additional.getAsArray(PdfName.COLORSPACE);
        if (value == null)
            return;
        PdfObject cs = simplifyColorspace(value);
        PdfObject newValue;
        if (cs.isName())
            newValue = cs;
        else {
            newValue = value;
            PdfName first = value.getAsName(0);
            if (PdfName.INDEXED.equals(first)) {
                if (value.size() >= 2) {
                    PdfArray second = value.getAsArray(1);
                    if (second != null) {
                        value.set(1, simplifyColorspace(second));
                    }
                }
            }
        }
        additional.put(PdfName.COLORSPACE, newValue);
    }

    /**
     * Gets a PDF Name from an array or returns the object that was passed.
     */
    private PdfObject simplifyColorspace(PdfArray obj) {
        if (obj == null)
            return obj;
        PdfName first = obj.getAsName(0);
        if (PdfName.CALGRAY.equals(first))
            return PdfName.DEVICEGRAY;
        else if (PdfName.CALRGB.equals(first))
            return PdfName.DEVICERGB;
        else
            return obj;
    }

    /** Is this image a mask? */
    protected boolean mask = false;

    /** The image that serves as a mask for this image. */
    protected Image imageMask;

    /** Holds value of property smask. */
    private boolean smask;

    /**
     * Returns <CODE>true</CODE> if this <CODE>Image</CODE> is a mask.
     *
     * @return <CODE>true</CODE> if this <CODE>Image</CODE> is a mask
     */
    public boolean isMask() {
        return mask;
    }

    /**
     * Make this <CODE>Image</CODE> a mask.
     *
     * @throws DocumentException
     *             if this <CODE>Image</CODE> can not be a mask
     */
    public void makeMask() throws DocumentException {
        if (!isMaskCandidate())
            throw new DocumentException(MessageLocalization.getComposedMessage("this.image.can.not.be.an.image.mask"));
        mask = true;
    }

    /**
     * Returns <CODE>true</CODE> if this <CODE>Image</CODE> has the
     * requisites to be a mask.
     *
     * @return <CODE>true</CODE> if this <CODE>Image</CODE> can be a mask
     */
    public boolean isMaskCandidate() {
        if (type == IMGRAW) {
            if (bpc > 0xff)
                return true;
        }
        return colorspace == 1;
    }

    /**
     * Gets the explicit masking.
     *
     * @return the explicit masking
     */
    public Image getImageMask() {
        return imageMask;
    }

    /**
     * Sets the explicit masking.
     *
     * @param mask
     *            the mask to be applied
     * @throws DocumentException
     *             on error
     */
    public void setImageMask(Image mask) throws DocumentException {
        if (this.mask)
            throw new DocumentException(MessageLocalization.getComposedMessage("an.image.mask.cannot.contain.another.image.mask"));
        if (!mask.mask)
            throw new DocumentException(MessageLocalization.getComposedMessage("the.image.mask.is.not.a.mask.did.you.do.makemask"));
        imageMask = mask;
        smask = (mask.bpc > 1 && mask.bpc <= 8);
    }

    /**
     * Getter for property smask.
     *
     * @return Value of property smask.
     *
     */
    public boolean isSmask() {
        return this.smask;
    }

    /**
     * Setter for property smask.
     *
     * @param smask
     *            New value of property smask.
     */
    public void setSmask(boolean smask) {
        this.smask = smask;
    }

    /**
     * this is the transparency information of the raw image
     */
    protected int[] transparency;

    /**
     * Returns the transparency.
     *
     * @return the transparency values
     */

    public int[] getTransparency() {
        return transparency;
    }

    /**
     * Sets the transparency values
     *
     * @param transparency
     *            the transparency values
     */
    public void setTransparency(int[] transparency) {
        this.transparency = transparency;
    }


    /**
     * Returns the compression level used for images written as a compressed stream.
     * @return the compression level (0 = best speed, 9 = best compression, -1 is default)
     * @since    2.1.3
     */
    public int getCompressionLevel() {
        return compressionLevel;
    }

    /**
     * Sets the compression level to be used if the image is written as a compressed stream.
     * @param compressionLevel a value between 0 (best speed) and 9 (best compression)
     * @since    2.1.3
     */
    public void setCompressionLevel(int compressionLevel) {
        if (compressionLevel < PdfStream.NO_COMPRESSION || compressionLevel > PdfStream.BEST_COMPRESSION)
            this.compressionLevel = PdfStream.DEFAULT_COMPRESSION;
        else
            this.compressionLevel = compressionLevel;
    }
}

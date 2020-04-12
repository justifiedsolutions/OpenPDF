/*
 * $Id: FontFactory.java 4065 2009-09-16 23:09:11Z psoares33 $
 * $Name$
 *
 * Copyright 2002 by Bruno Lowagie.
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

import com.justifiedsolutions.openpdf.pdf.font.PDFFont;
import com.justifiedsolutions.openpdf.text.pdf.BaseFont;

import java.awt.*;

/**
 * If you are using True Type fonts, you can declare the paths of the different ttf- and ttc-files
 * to this static class first and then create fonts in your code using one of the static getFont-method
 * without having to enter a path as parameter.
 *
 * @author  Bruno Lowagie
 */

public final class FontFactory {
    
/** This is a possible value of a base 14 type 1 font */
    public static final String COURIER = BaseFont.COURIER;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String COURIER_BOLD = BaseFont.COURIER_BOLD;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String COURIER_OBLIQUE = BaseFont.COURIER_OBLIQUE;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String COURIER_BOLDOBLIQUE = BaseFont.COURIER_BOLDOBLIQUE;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String HELVETICA = BaseFont.HELVETICA;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String HELVETICA_BOLD = BaseFont.HELVETICA_BOLD;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String HELVETICA_OBLIQUE = BaseFont.HELVETICA_OBLIQUE;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String HELVETICA_BOLDOBLIQUE = BaseFont.HELVETICA_BOLDOBLIQUE;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String SYMBOL = BaseFont.SYMBOL;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String TIMES = "Times";
    
/** This is a possible value of a base 14 type 1 font */
    public static final String TIMES_ROMAN = BaseFont.TIMES_ROMAN;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String TIMES_BOLD = BaseFont.TIMES_BOLD;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String TIMES_ITALIC = BaseFont.TIMES_ITALIC;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String TIMES_BOLDITALIC = BaseFont.TIMES_BOLDITALIC;
    
/** This is a possible value of a base 14 type 1 font */
    public static final String ZAPFDINGBATS = BaseFont.ZAPFDINGBATS;
    
    private static FontFactoryImp fontImp = new FontFactoryImp();
    
/** This is the default encoding to use. */
    public static String defaultEncoding = BaseFont.WINANSI;
    
/** This is the default value of the <VAR>embedded</VAR> variable. */
    public static boolean defaultEmbedding = BaseFont.NOT_EMBEDDED;
    
/** Creates new FontFactory */
    private FontFactory() {
    }
    
/**
 * Constructs a <CODE>Font</CODE>-object.
 *
 * @param    fontname    the name of the font
 * @param    encoding    the encoding of the font
 * @param       embedded    true if the font is to be embedded in the PDF
 * @param    size        the size of this font
 * @param    style        the style of this font
 * @param    color        the <CODE>Color</CODE> of this font.
 * @return the Font constructed based on the parameters
 */
    
    public static Font getFont(String fontname, String encoding, boolean embedded, float size, int style, Color color) {
        return fontImp.getFont(fontname, encoding, embedded, size, style, color);
    }

    /**
 * Constructs a <CODE>Font</CODE>-object.
 *
 * @param    fontname    the name of the font
 * @param    size        the size of this font
 * @param    style        the style of this font
 * @param    color        the <CODE>Color</CODE> of this font.
 * @return the Font constructed based on the parameters
 */
    
    public static Font getFont(String fontname, float size, int style, Color color) {
        return getFont(fontname, defaultEncoding, defaultEmbedding, size, style, color);
    }
    
/**
 * Constructs a <CODE>Font</CODE>-object.
 *
 * @param    fontname    the name of the font
 * @param    size        the size of this font
 * @param    color        the <CODE>Color</CODE> of this font.
 * @return the Font constructed based on the parameters
 * @since 2.1.0
 */
    
    public static Font getFont(String fontname, float size, Color color) {
        return getFont(fontname, defaultEncoding, defaultEmbedding, size, Font.UNDEFINED, color);
    }

    /**
 * Checks if a certain font is registered.
 *
 * @param   fontname    the name of the font that has to be checked.
 * @return  true if the font is found
 */
    
    public static boolean isRegistered(String fontname) {
        return fontImp.isRegistered(fontname);
    }
    
    /**
     * Gets the font factory implementation.
     * @return the font factory implementation
     */    
    public static FontFactoryImp getFontImp() {
        return fontImp;
    }

    public static Font getFont(com.justifiedsolutions.openpdf.pdf.font.Font font) {
        if (font instanceof PDFFont) {
            PDFFont pdfFont = (PDFFont) font;
            return getFont(getFontName(pdfFont.getName()), pdfFont.getSize(), pdfFont.getColor());
        }
        return new Font();
    }

    private static String getFontName(PDFFont.FontName fontName) {
        String result;
        switch (fontName) {
            case COURIER:
                result = BaseFont.COURIER;
                break;
            case COURIER_BOLD:
                result = BaseFont.COURIER_BOLD;
                break;
            case COURIER_OBLIQUE:
                result = BaseFont.COURIER_OBLIQUE;
                break;
            case COURIER_BOLD_OBLIQUE:
                result = BaseFont.COURIER_BOLDOBLIQUE;
                break;
            case HELVETICA:
                result = BaseFont.HELVETICA;
                break;
            case HELVETICA_BOLD:
                result = BaseFont.HELVETICA_BOLD;
                break;
            case HELVETICA_OBLIQUE:
                result = BaseFont.HELVETICA_OBLIQUE;
                break;
            case HELVETICA_BOLD_OBLIQUE:
                result = BaseFont.HELVETICA_BOLDOBLIQUE;
                break;
            case TIMES_ROMAN:
                result = BaseFont.TIMES_ROMAN;
                break;
            case TIMES_BOLD:
                result = BaseFont.TIMES_BOLD;
                break;
            case TIMES_ITALIC:
                result = BaseFont.TIMES_ITALIC;
                break;
            case TIMES_BOLD_ITALIC:
                result = BaseFont.TIMES_BOLDITALIC;
                break;
            case SYMBOL:
                result = BaseFont.SYMBOL;
                break;
            case ZAPFDINGBATS:
                result = BaseFont.ZAPFDINGBATS;
                break;
            default:
                result = null;
        }
        return result;
    }
}

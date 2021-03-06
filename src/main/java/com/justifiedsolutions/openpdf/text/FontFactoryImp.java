/*
 * $Id: FontFactoryImp.java 4063 2009-09-13 19:02:46Z psoares33 $
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

import com.justifiedsolutions.openpdf.text.pdf.BaseFont;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;


/**
 * If you are using True Type fonts, you can declare the paths of the different ttf- and ttc-files
 * to this class first and then create fonts in your code using one of the getFont method without
 * having to enter a path as parameter.
 *
 * @author Bruno Lowagie
 */

public class FontFactoryImp {

    private static final String[] TTFamilyOrder = {
            "3", "1", "1033",
            "3", "0", "1033",
            "1", "0", "0",
            "0", "3", "0"
    };
    /**
     * This is a map of postscriptfontnames of True Type fonts and the path of their ttf- or
     * ttc-file.
     */
    private final Map<String, String> trueTypeFonts = new HashMap<>();
    /**
     * This is a map of fontfamilies.
     */
    private final Map<String, List<String>> fontFamilies = new HashMap<>();

    /**
     * Creates new FontFactory
     */
    public FontFactoryImp() {
        trueTypeFonts.put(FontFactory.COURIER.toLowerCase(Locale.ROOT), FontFactory.COURIER);
        trueTypeFonts
                .put(FontFactory.COURIER_BOLD.toLowerCase(Locale.ROOT), FontFactory.COURIER_BOLD);
        trueTypeFonts.put(FontFactory.COURIER_OBLIQUE.toLowerCase(Locale.ROOT),
                FontFactory.COURIER_OBLIQUE);
        trueTypeFonts.put(FontFactory.COURIER_BOLDOBLIQUE.toLowerCase(Locale.ROOT),
                FontFactory.COURIER_BOLDOBLIQUE);
        trueTypeFonts.put(FontFactory.HELVETICA.toLowerCase(Locale.ROOT), FontFactory.HELVETICA);
        trueTypeFonts.put(FontFactory.HELVETICA_BOLD.toLowerCase(Locale.ROOT),
                FontFactory.HELVETICA_BOLD);
        trueTypeFonts.put(FontFactory.HELVETICA_OBLIQUE.toLowerCase(Locale.ROOT),
                FontFactory.HELVETICA_OBLIQUE);
        trueTypeFonts.put(FontFactory.HELVETICA_BOLDOBLIQUE.toLowerCase(Locale.ROOT),
                FontFactory.HELVETICA_BOLDOBLIQUE);
        trueTypeFonts.put(FontFactory.SYMBOL.toLowerCase(Locale.ROOT), FontFactory.SYMBOL);
        trueTypeFonts
                .put(FontFactory.TIMES_ROMAN.toLowerCase(Locale.ROOT), FontFactory.TIMES_ROMAN);
        trueTypeFonts.put(FontFactory.TIMES_BOLD.toLowerCase(Locale.ROOT), FontFactory.TIMES_BOLD);
        trueTypeFonts
                .put(FontFactory.TIMES_ITALIC.toLowerCase(Locale.ROOT), FontFactory.TIMES_ITALIC);
        trueTypeFonts.put(FontFactory.TIMES_BOLDITALIC.toLowerCase(Locale.ROOT),
                FontFactory.TIMES_BOLDITALIC);
        trueTypeFonts
                .put(FontFactory.ZAPFDINGBATS.toLowerCase(Locale.ROOT), FontFactory.ZAPFDINGBATS);

        java.util.List<String> tmp = new ArrayList<>();
        tmp.add(FontFactory.COURIER);
        tmp.add(FontFactory.COURIER_BOLD);
        tmp.add(FontFactory.COURIER_OBLIQUE);
        tmp.add(FontFactory.COURIER_BOLDOBLIQUE);
        fontFamilies.put(FontFactory.COURIER.toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList<>();
        tmp.add(FontFactory.HELVETICA);
        tmp.add(FontFactory.HELVETICA_BOLD);
        tmp.add(FontFactory.HELVETICA_OBLIQUE);
        tmp.add(FontFactory.HELVETICA_BOLDOBLIQUE);
        fontFamilies.put(FontFactory.HELVETICA.toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList<>();
        tmp.add(FontFactory.SYMBOL);
        fontFamilies.put(FontFactory.SYMBOL.toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList<>();
        tmp.add(FontFactory.TIMES_ROMAN);
        tmp.add(FontFactory.TIMES_BOLD);
        tmp.add(FontFactory.TIMES_ITALIC);
        tmp.add(FontFactory.TIMES_BOLDITALIC);
        fontFamilies.put(FontFactory.TIMES.toLowerCase(Locale.ROOT), tmp);
        fontFamilies.put(FontFactory.TIMES_ROMAN.toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList<>();
        tmp.add(FontFactory.ZAPFDINGBATS);
        fontFamilies.put(FontFactory.ZAPFDINGBATS.toLowerCase(Locale.ROOT), tmp);
    }

    /**
     * Constructs a <CODE>Font</CODE>-object.
     *
     * @param fontName the name of the font
     * @param encoding the encoding of the font
     * @param embedded true if the font is to be embedded in the PDF
     * @param size     the size of this font
     * @param style    the style of this font
     * @param color    the <CODE>Color</CODE> of this font.
     * @return the Font constructed based on the parameters
     */
    public Font getFont(String fontName, String encoding, boolean embedded, float size, int style,
            Color color) {
        return getFont(fontName, encoding, embedded, size, style, color, true);
    }


    /**
     * Constructs a <CODE>Font</CODE>-object.
     *
     * @param fontname the name of the font
     * @param encoding the encoding of the font
     * @param embedded true if the font is to be embedded in the PDF
     * @param size     the size of this font
     * @param style    the style of this font
     * @param color    the <CODE>Color</CODE> of this font.
     * @param cached   true if the font comes from the cache or is added to the cache if new, false
     *                 if the font is always created new
     * @return the Font constructed based on the parameters
     */
    public Font getFont(String fontname, String encoding, boolean embedded, float size, int style,
            Color color, boolean cached) {
        if (fontname == null) {
            return new Font(Font.UNDEFINED, size, style, color);
        }
        String lowerCaseFontname = fontname.toLowerCase(Locale.ROOT);
        List<String> tmp = fontFamilies.get(lowerCaseFontname);
        if (tmp != null) {
            // some bugs were fixed here by Daniel Marczisovszky
            int s = style == Font.UNDEFINED ? Font.NORMAL : style;
            for (String f : tmp) {
                int fs = getFontStyle(f);
                if ((s & Font.BOLDITALIC) == fs) {
                    fontname = f;
                    lowerCaseFontname = fontname.toLowerCase(Locale.ROOT);
                    // If a styled font already exists, we don't want to use the separate style-Attribut.
                    // For example: Helvetica-Bold should have a normal style, because it's already bold.
                    style = s == fs ? Font.NORMAL : s;
                    break;
                }
            }
        }
        BaseFont basefont = null;
        try {
            try {
                // the font is a type 1 font or CJK font
                basefont = BaseFont
                        .createFont(fontname, encoding, embedded, cached, null, null);
            } catch (DocumentException ignored) {
            }
            if (basefont == null) {
                // the font is a true type font or an unknown font
                fontname = trueTypeFonts.get(lowerCaseFontname);
                // the font is not registered as truetype font
                if (fontname == null) {
                    return new Font(Font.UNDEFINED, size, style, color);
                }
                // the font is registered as truetype font
                basefont = BaseFont.createFont(fontname, encoding, embedded, cached, null, null);
            }
        } catch (DocumentException de) {
            // this shouldn't happen
            throw new ExceptionConverter(de);
        } catch (IOException | NullPointerException ioe) {
            // the font is registered as a true type font, but the path was wrong
            // or null was entered as fontname and/or encoding
            return new Font(Font.UNDEFINED, size, style, color);
        }

        return new Font(basefont, size, style, color);
    }

    private int getFontStyle(final String fontname) {
        String lcf = fontname.toLowerCase(Locale.ROOT);

        int fontStyle = Font.NORMAL;
        if (lcf.contains("bold")) {
            fontStyle |= Font.BOLD;
        }
        if (lcf.contains("italic") || lcf.contains("oblique")) {
            fontStyle |= Font.ITALIC;
        }
        return fontStyle;
    }


    /**
     * Checks if a certain font is registered.
     *
     * @param fontName the name of the font that has to be checked.
     * @return true if the font is found
     */
    public boolean isRegistered(String fontName) {
        return trueTypeFonts.containsKey(fontName.toLowerCase());
    }

    /**
     * Get a registered font path.
     *
     * @param fontname the name of the font to get.
     * @return the font path if found or null
     */
    public Object getFontPath(String fontname) {
        return trueTypeFonts.get(fontname.toLowerCase());
    }
}

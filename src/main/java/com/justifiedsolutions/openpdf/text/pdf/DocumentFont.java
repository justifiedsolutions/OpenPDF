/*
 * Copyright 2004 by Paulo Soares.
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

import com.justifiedsolutions.openpdf.text.DocumentException;
import java.util.HashMap;


/**
 * @author psoares
 */
public class DocumentFont extends BaseFont {

  // code, [glyph, width]
  private final HashMap<Integer, int[]> metrics = new HashMap<>();
  private String fontName;
  private PRIndirectReference refFont;
  private final IntHashtable uni2byte = new IntHashtable();
  private final boolean isType0 = false;

  private BaseFont cjkMirror;

  public DocumentFont() {
  }


  /**
   * Gets the family name of the font. If it is a True Type font each array element will have {Platform ID, Platform Encoding ID, Language
   * ID, font name}. The interpretation of this values can be found in the Open Type specification, chapter 2, in the 'name' table.<br> For
   * the other fonts the array has a single element with {"", "", "", font name}.
   *
   * @return the family name of the font
   */
  public String[][] getFamilyFontName() {
    return getFullFontName();
  }

  /**
   * Gets the font parameter identified by <CODE>key</CODE>. Valid values for <CODE>key</CODE> are <CODE>ASCENT</CODE>,
   * <CODE>CAPHEIGHT</CODE>, <CODE>DESCENT</CODE>,
   * <CODE>ITALICANGLE</CODE>, <CODE>BBOXLLX</CODE>, <CODE>BBOXLLY</CODE>, <CODE>BBOXURX</CODE>
   * and <CODE>BBOXURY</CODE>.
   *
   * @param key the parameter to be extracted
   * @param fontSize the font size in points
   * @return the parameter in points
   */
  public float getFontDescriptor(int key, float fontSize) {
    if (cjkMirror != null) {
      return cjkMirror.getFontDescriptor(key, fontSize);
    }
    float ascender = 800;
    float capHeight = 700;
    float descender = -200;
    float italicAngle = 0;
    float llx = -50;
    float lly = -200;
    float urx = 100;
    float ury = 900;
    switch (key) {
      case AWT_ASCENT:
      case ASCENT:
        return ascender * fontSize / 1000;
      case CAPHEIGHT:
        return capHeight * fontSize / 1000;
      case AWT_DESCENT:
      case DESCENT:
        return descender * fontSize / 1000;
      case ITALICANGLE:
        return italicAngle;
      case BBOXLLX:
        return llx * fontSize / 1000;
      case BBOXLLY:
        return lly * fontSize / 1000;
      case BBOXURX:
        return urx * fontSize / 1000;
      case BBOXURY:
        return ury * fontSize / 1000;
      case AWT_LEADING:
        return 0;
      case AWT_MAXADVANCE:
        return (urx - llx) * fontSize / 1000;
    }
    return 0;
  }

  /**
   * Gets the full name of the font. If it is a True Type font each array element will have {Platform ID, Platform Encoding ID, Language ID,
   * font name}. The interpretation of this values can be found in the Open Type specification, chapter 2, in the 'name' table.<br> For the
   * other fonts the array has a single element with {"", "", "", font name}.
   *
   * @return the full name of the font
   */
  public String[][] getFullFontName() {
    return new String[][]{{"", "", "", fontName}};
  }

  /**
   * Gets the kerning between two Unicode chars.
   *
   * @param char1 the first char
   * @param char2 the second char
   * @return the kerning to be applied
   */
  public int getKerning(int char1, int char2) {
    return 0;
  }

  /**
   * Gets the postscript font name.
   *
   * @return the postscript font name
   */
  public String getPostscriptFontName() {
    return fontName;
  }

  /**
   * Gets the width from the font according to the Unicode char <CODE>c</CODE> or the <CODE>name</CODE>. If the <CODE>name</CODE> is null
   * it's a symbolic font.
   *
   * @param c the unicode char
   * @param name the glyph name
   * @return the width of the char
   */
  int getRawWidth(int c, String name) {
    return 0;
  }

  /**
   * Checks if the font has any kerning pairs.
   *
   * @return <CODE>true</CODE> if the font has any kerning pairs
   */
  public boolean hasKernPairs() {
    return false;
  }

  /**
   * Outputs to the writer the font dictionaries and streams.
   *
   * @param writer the writer for this document
   * @param ref the font indirect reference
   * @param params several parameters that depend on the font type
   * @throws DocumentException error in generating the object
   */
  void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws DocumentException {
  }

  /**
   * Always returns null.
   *
   * @return null
   * @since 2.1.3
   */
  public PdfStream getFullFontStream() {
    return null;
  }

  /**
   * Gets the width of a <CODE>char</CODE> in normalized 1000 units.
   *
   * @param char1 the unicode <CODE>char</CODE> to get the width of
   * @return the width in normalized 1000 units
   */
  public int getWidth(int char1) {
    if (cjkMirror != null) {
      return cjkMirror.getWidth(char1);
    } else if (isType0) {
      int[] ws = metrics.get(char1);
      if (ws != null) {
        return ws[1];
      } else {
        return 0;
      }
    } else {
      return super.getWidth(char1);
    }
  }

  public int getWidth(String text) {
    if (cjkMirror != null) {
      return cjkMirror.getWidth(text);
    } else if (isType0) {
      char[] chars = text.toCharArray();
      int total = 0;
      for (char aChar : chars) {
        int[] ws = metrics.get(Character.getNumericValue(aChar));
        if (ws != null) {
          total += ws[1];
        }
      }
      return total;
    } else {
      return super.getWidth(text);
    }
  }

  byte[] convertToBytes(String text) {
    if (cjkMirror != null) {
      return PdfEncodings.convertToBytes(text, CJKFont.CJK_ENCODING);
    } else if (isType0) {
      char[] chars = text.toCharArray();
      int len = chars.length;
      byte[] b = new byte[len * 2];
      int bptr = 0;
      for (char aChar : chars) {
        int[] ws = metrics.get(Character.getNumericValue(aChar));
        if (ws != null) {
          int g = ws[0];
          b[bptr++] = (byte) (g / 256);
          b[bptr++] = (byte) (g);
        }
      }
      if (bptr == b.length) {
        return b;
      } else {
        byte[] nb = new byte[bptr];
        System.arraycopy(b, 0, nb, 0, bptr);
        return nb;
      }
    } else {
      char[] cc = text.toCharArray();
      byte[] b = new byte[cc.length];
      int ptr = 0;
      for (char c : cc) {
        if (uni2byte.containsKey(c)) {
          b[ptr++] = (byte) uni2byte.get(c);
        }
      }
      if (ptr == b.length) {
        return b;
      } else {
        byte[] b2 = new byte[ptr];
        System.arraycopy(b, 0, b2, 0, ptr);
        return b2;
      }
    }
  }

  byte[] convertToBytes(int char1) {
    if (cjkMirror != null) {
      return PdfEncodings.convertToBytes((char) char1, CJKFont.CJK_ENCODING);
    } else if (isType0) {
      int[] ws = metrics.get(char1);
      if (ws != null) {
        int g = ws[0];
        return new byte[]{(byte) (g / 256), (byte) (g)};
      } else {
        return new byte[0];
      }
    } else {
      if (uni2byte.containsKey(char1)) {
        return new byte[]{(byte) uni2byte.get(char1)};
      } else {
        return new byte[0];
      }
    }
  }

  PdfIndirectReference getIndirectReference() {
    return refFont;
  }

  public boolean charExists(int c) {
    if (cjkMirror != null) {
      return cjkMirror.charExists(c);
    } else if (isType0) {
      return metrics.containsKey(c);
    } else {
      return super.charExists(c);
    }
  }

  protected int[] getRawCharBBox(int c, String name) {
    return null;
  }

}

/*
 * $Id: PdfName.java 4082 2009-10-25 14:18:28Z psoares33 $
 *
 * Copyright 1999-2006 Bruno Lowagie
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

import com.justifiedsolutions.openpdf.text.MessageLocalization;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * <CODE>PdfName</CODE> is an object that can be used as a name in a PDF-file.
 * <P>
 * A name, like a string, is a sequence of characters.
 * It must begin with a slash followed by a sequence of ASCII characters in
 * the range 32 through 136 except %, (, ), [, ], &lt;, &gt;, {, }, / and #.
 * Any character except 0x00 may be included in a name by writing its
 * two character hex code, preceded by #. The maximum number of characters
 * in a name is 127.<BR>
 * This object is described in the 'Portable Document Format Reference Manual
 * version 1.7' section 3.2.4 (page 56-58).
 * <P>
 *
 * @see        PdfObject
 * @see        PdfDictionary
 * @see        BadPdfFormatException
 */

public class PdfName extends PdfObject implements Comparable<PdfName> {

    // CLASS CONSTANTS (a variety of standard names used in PDF))
    /** A name */
    public static final PdfName AA = new PdfName("AA");
    /** A name */
    public static final PdfName ANTIALIAS = new PdfName("AntiAlias");
    /** A name */
    public static final PdfName ARTBOX = new PdfName("ArtBox");
    /** A name */
    public static final PdfName ASCENT = new PdfName("Ascent");
    /** A name */
    public static final PdfName AS = new PdfName("AS");
    /** A name */
    public static final PdfName AUTHOR = new PdfName("Author");
    /** A name */
    public static final PdfName BASEFONT = new PdfName("BaseFont");
    /**
     * A name
     * @since    2.1.6
     */
    public static final PdfName BASEVERSION = new PdfName("BaseVersion");
    /** A name */
    public static final PdfName BBOX = new PdfName("BBox");
    /** A name */
    public static final PdfName BITSPERCOMPONENT = new PdfName("BitsPerComponent");
    /** A name */
    public static final PdfName BLEEDBOX = new PdfName("BleedBox");
    /** A name */
    public static final PdfName BLINDS = new PdfName("Blinds");
    /** A name */
    public static final PdfName BM = new PdfName("BM");
    /** A name */
    public static final PdfName BOX = new PdfName("Box");
    /** A name */
    public static final PdfName C = new PdfName("C");
    /** A name */
    public static final PdfName C0 = new PdfName("C0");
    /** A name */
    public static final PdfName C1 = new PdfName("C1");
    /** A name */
    public static final PdfName CA = new PdfName("CA");
    /** A name */
    public static final PdfName ca = new PdfName("ca");
    /** A name */
    public static final PdfName CALRGB = new PdfName("CalRGB");
    /** A name */
    public static final PdfName CAPHEIGHT = new PdfName("CapHeight");
    /** A name */
    public static final PdfName CATALOG = new PdfName("Catalog");
    /** A name */
    public static final PdfName CATEGORY = new PdfName("Category");
    /** A name */
    public static final PdfName CIDFONTTYPE0 = new PdfName("CIDFontType0");
    /** A name */
    public static final PdfName CIDFONTTYPE2 = new PdfName("CIDFontType2");
    /**
     * A name
     * @since 2.0.7
     */
    public static final PdfName CIDSET = new PdfName("CIDSet");
    /** A name */
    public static final PdfName CIDSYSTEMINFO = new PdfName("CIDSystemInfo");
    /** A name */
    public static final PdfName CIDTOGIDMAP = new PdfName("CIDToGIDMap");

    /** A name */
    public static final PdfName COLORS = new PdfName("Colors");
    /** A name */
    public static final PdfName COLORSPACE = new PdfName("ColorSpace");
    /** A name */
    public static final PdfName COLUMNS = new PdfName("Columns");
    /** A name */
    public static final PdfName CONTENTS = new PdfName("Contents");
    /** A name */
    public static final PdfName COUNT = new PdfName("Count");
    /** A name of a base 14 type 1 font */
    public static final PdfName COURIER = new PdfName("Courier");
    /** A name of a base 14 type 1 font */
    public static final PdfName COURIER_BOLD = new PdfName("Courier-Bold");
    /** A name of a base 14 type 1 font */
    public static final PdfName COURIER_OBLIQUE = new PdfName("Courier-Oblique");
    /** A name of a base 14 type 1 font */
    public static final PdfName COURIER_BOLDOBLIQUE = new PdfName("Courier-BoldOblique");
    /** A name */
    public static final PdfName CREATIONDATE = new PdfName("CreationDate");
    /** A name */
    public static final PdfName CREATOR = new PdfName("Creator");
    /** A name */
    public static final PdfName CROPBOX = new PdfName("CropBox");
    /** A name */
    public static final PdfName D = new PdfName("D");
    /** A name */
    public static final PdfName DECODE = new PdfName("Decode");
    /** A name */
    public static final PdfName DECODEPARMS = new PdfName("DecodeParms");
    /** A name */
    public static final PdfName DEFAULTRGB = new PdfName("DefaultRGB");
    /** A name */
    public static final PdfName DESCENDANTFONTS = new PdfName("DescendantFonts");
    /** A name */
    public static final PdfName DESCENT = new PdfName("Descent");
    /** A name */
    public static final PdfName DEST = new PdfName("Dest");
    /** A name */
    public static final PdfName DEVICEGRAY = new PdfName("DeviceGray");
    /** A name */
    public static final PdfName DEVICERGB = new PdfName("DeviceRGB");
    /** A name */
    public static final PdfName DEVICECMYK = new PdfName("DeviceCMYK");
    /** A name */
    public static final PdfName DI = new PdfName("Di");
    /** A name */
    public static final PdfName DIFFERENCES = new PdfName("Differences");
    /** A name */
    public static final PdfName DISSOLVE = new PdfName("Dissolve");
    /** A name */
    public static final PdfName DM = new PdfName("Dm");
    /** A name */
    public static final PdfName DOMAIN = new PdfName("Domain");
    /** A name */
    public static final PdfName DP = new PdfName("DP");
    /** A name */
    public static final PdfName DUR = new PdfName("Dur");
    /** A name */
    public static final PdfName DW = new PdfName("DW");
    /** A name */
    public static final PdfName ENCODING = new PdfName("Encoding");
    /**
     * A name
     * @since    2.1.6
     */
    public static final PdfName EXTENSIONS = new PdfName("Extensions");
    /**
     * A name
     * @since    2.1.6
     */
    public static final PdfName EXTENSIONLEVEL = new PdfName("ExtensionLevel");
    /** A name */
    public static final PdfName EXTGSTATE = new PdfName("ExtGState");
    /** A name */
    public static final PdfName EXPORT = new PdfName("Export");
    /** A name */
    public static final PdfName EVENT = new PdfName("Event");
    /** A name */
    public static final PdfName F = new PdfName("F");
    /** A name */
    public static final PdfName FILTER = new PdfName("Filter");
    /** A name */
    public static final PdfName FIRST = new PdfName("First");
    /** A name */
    public static final PdfName FIRSTCHAR = new PdfName("FirstChar");
    /** A name */
    public static final PdfName FITH = new PdfName("FitH");
    /** A name */
    public static final PdfName FITV = new PdfName("FitV");
    /** A name */
    public static final PdfName FITBH = new PdfName("FitBH");
    /** A name */
    public static final PdfName FITBV = new PdfName("FitBV");
    /** A name */
    public static final PdfName FLAGS = new PdfName("Flags");
    /** A name */
    public static final PdfName FLATEDECODE = new PdfName("FlateDecode");
    /** A name */
    public static final PdfName FONT = new PdfName("Font");
    /** A name */
    public static final PdfName FONTBBOX = new PdfName("FontBBox");
    /** A name */
    public static final PdfName FONTDESCRIPTOR = new PdfName("FontDescriptor");
    /** A name */
    public static final PdfName FONTFILE = new PdfName("FontFile");
    /** A name */
    public static final PdfName FONTFILE2 = new PdfName("FontFile2");
    /** A name */
    public static final PdfName FONTFILE3 = new PdfName("FontFile3");
    /** A name */
    public static final PdfName FONTNAME = new PdfName("FontName");
    /** A name */
    public static final PdfName FORM = new PdfName("Form");
    /** A name */
    public static final PdfName FORMTYPE = new PdfName("FormType");
    /** A name */
    public static final PdfName FUNCTIONTYPE = new PdfName("FunctionType");
    /** A name of an attribute. */
    public static final PdfName GAMMA = new PdfName("Gamma");
    /** A name of an attribute. */
    public static final PdfName GLITTER = new PdfName("Glitter");
    /** A name of an attribute. */
    public static final PdfName GTS_PDFX = new PdfName("GTS_PDFX");
    /** A name of an attribute. */
    public static final PdfName GTS_PDFXVERSION = new PdfName("GTS_PDFXVersion");
    /** A name of an attribute. */
    public static final PdfName H = new PdfName("H");
    /** A name of an attribute. */
    public static final PdfName HEIGHT = new PdfName("Height");
    /** A name of a base 14 type 1 font */
    public static final PdfName HELVETICA = new PdfName("Helvetica");
    /** A name of a base 14 type 1 font */
    public static final PdfName HELVETICA_BOLD = new PdfName("Helvetica-Bold");
    /** A name of a base 14 type 1 font */
    public static final PdfName HELVETICA_OBLIQUE = new PdfName("Helvetica-Oblique");
    /** A name of a base 14 type 1 font */
    public static final PdfName HELVETICA_BOLDOBLIQUE = new PdfName("Helvetica-BoldOblique");
    /** A name */
    public static final PdfName I = new PdfName("I");
    /** A name */
    public static final PdfName IDENTITY = new PdfName("Identity");
    /** A name */
    public static final PdfName IMAGEMASK = new PdfName("ImageMask");
    /** A name */
    public static final PdfName INDEX = new PdfName("Index");
    /** A name */
    public static final PdfName INFO = new PdfName("Info");
    /** A name */
    public static final PdfName INTENT = new PdfName("Intent");
    /** A name */
    public static final PdfName INTERPOLATE = new PdfName("Interpolate");
    /** A name */
    public static final PdfName ITALICANGLE = new PdfName("ItalicAngle");
    /** A name */
    public static final PdfName KEYWORDS = new PdfName("Keywords");
    /** A name */
    public static final PdfName KIDS = new PdfName("Kids");
    /** A name */
    public static final PdfName LAST = new PdfName("Last");
    /** A name */
    public static final PdfName LASTCHAR = new PdfName("LastChar");
    /** A name */
    public static final PdfName LENGTH = new PdfName("Length");
    /** A name */
    public static final PdfName LIMITS = new PdfName("Limits");
    /** A name */
    public static final PdfName LISTMODE = new PdfName("ListMode");
    /**
     * A name
     * @since    2.1.2
     */
    public static final PdfName LOCKED = new PdfName("Locked");
    /** A name */
    public static final PdfName M = new PdfName("M");
    /** A name */
    public static final PdfName MATRIX = new PdfName("Matrix");
    /** A name of an encoding */
    public static final PdfName MAC_ROMAN_ENCODING = new PdfName("MacRomanEncoding");
    /** A name */
    public static final PdfName MEDIABOX = new PdfName("MediaBox");
    /** A name */
    public static final PdfName MODDATE = new PdfName("ModDate");
    /** A name */
    public static final PdfName N = new PdfName("N");
    /** A name */
    public static final PdfName NAME = new PdfName("Name");
    /** A name */
    public static final PdfName NEXT = new PdfName("Next");
    /** A name */
    public static final PdfName NUMS = new PdfName("Nums");
    /** A name */
    public static final PdfName O = new PdfName("O");
    /** A name */
    public static final PdfName OBJSTM = new PdfName("ObjStm");
    /** A name */
    public static final PdfName OC = new PdfName("OC");
    /** A name */
    public static final PdfName OCGS = new PdfName("OCGs");
    /** A name */
    public static final PdfName OCPROPERTIES = new PdfName("OCProperties");
    /** A name */
    public static final PdfName OFF = new PdfName("OFF");
    /** A name */
    public static final PdfName ORDER = new PdfName("Order");
    /** A name */
    public static final PdfName ORDERING = new PdfName("Ordering");
    /** A name */
    public static final PdfName OUTLINES = new PdfName("Outlines");
    /** A name */
    public static final PdfName OUTPUTCONDITION = new PdfName("OutputCondition");
    /** A name */
    public static final PdfName OUTPUTCONDITIONIDENTIFIER = new PdfName("OutputConditionIdentifier");
    /** A name */
    public static final PdfName OUTPUTINTENT = new PdfName("OutputIntent");
    /** A name */
    public static final PdfName OUTPUTINTENTS = new PdfName("OutputIntents");
    /** A name */
    public static final PdfName PAGE = new PdfName("Page");
    /** A name */
    public static final PdfName PAGEMODE = new PdfName("PageMode");
    /** A name */
    public static final PdfName PAGES = new PdfName("Pages");
    /** A name */
    public static final PdfName PAINTTYPE = new PdfName("PaintType");
    /** A name */
    public static final PdfName PANOSE = new PdfName("Panose");
    /** A name */
    public static final PdfName PARENT = new PdfName("Parent");
    /** A name */
    public static final PdfName PATTERN = new PdfName("Pattern");
    /** A name */
    public static final PdfName PATTERNTYPE = new PdfName("PatternType");
    /** A name */
    public static final PdfName PREDICTOR = new PdfName("Predictor");
    /** A name */
    public static final PdfName PREV = new PdfName("Prev");
    /** A name */
    public static final PdfName PRINT = new PdfName("Print");
    /** A name */
    public static final PdfName PROCSET = new PdfName("ProcSet");
    /** A name */
    public static final PdfName PRODUCER = new PdfName("Producer");
    /** A name */
    public static final PdfName PROPERTIES = new PdfName("Properties");
    /** A name */
    public static final PdfName PS = new PdfName("PS");
    /** A name */
    public static final PdfName RANGE = new PdfName("Range");
    /** A name */
    public static final PdfName RBGROUPS = new PdfName("RBGroups");
    /** A name */
    public static final PdfName REGISTRY = new PdfName("Registry");
    /** A name */
    public static final PdfName REGISTRYNAME = new PdfName("RegistryName");
    /** A name */
    public static final PdfName RESOURCES = new PdfName("Resources");
    /** A name */
    public static final PdfName ROOT = new PdfName("Root");
    /** A name */
    public static final PdfName ROTATE = new PdfName("Rotate");
    /** A name */
    public static final PdfName S = new PdfName("S");
    /** A name */
    public static final PdfName SEPARATION = new PdfName("Separation");
    /** A name */
    public static final PdfName SHADING = new PdfName("Shading");
    /** A name */
    public static final PdfName SIZE = new PdfName("Size");
    /** A name */
    public static final PdfName SPLIT = new PdfName("Split");
    /** A name */
    public static final PdfName STEMV = new PdfName("StemV");
    /** A name */
    public static final PdfName STYLE = new PdfName("Style");
    /** A name */
    public static final PdfName SUBJECT = new PdfName("Subject");
    /** A name */
    public static final PdfName SUBTYPE = new PdfName("Subtype");
    /** A name */
    public static final PdfName SUPPLEMENT = new PdfName("Supplement");
    /** A name of a base 14 type 1 font */
    public static final PdfName SYMBOL = new PdfName("Symbol");
    /**
     * A name
     * @since    2.1.5
     */
    public static final PdfName TABS = new PdfName("Tabs");
    /** A name */
    public static final PdfName THUMB = new PdfName("Thumb");
    /** A name */
    public static final PdfName TILINGTYPE = new PdfName("TilingType");
    /** A name of a base 14 type 1 font */
    public static final PdfName TIMES_ROMAN = new PdfName("Times-Roman");
    /** A name of a base 14 type 1 font */
    public static final PdfName TIMES_BOLD = new PdfName("Times-Bold");
    /** A name of a base 14 type 1 font */
    public static final PdfName TIMES_ITALIC = new PdfName("Times-Italic");
    /** A name of a base 14 type 1 font */
    public static final PdfName TIMES_BOLDITALIC = new PdfName("Times-BoldItalic");
    /** A name */
    public static final PdfName TITLE = new PdfName("Title");
    /** A name */
    public static final PdfName TOUNICODE = new PdfName("ToUnicode");
    /** A name */
    public static final PdfName TRANS = new PdfName("Trans");
    /** A name */
    public static final PdfName TRAPPED = new PdfName("Trapped");
    /** A name */
    public static final PdfName TRIMBOX = new PdfName("TrimBox");
    /** A name */
    public static final PdfName TRUETYPE = new PdfName("TrueType");
    /** A name */
    public static final PdfName TYPE = new PdfName("Type");
    /** A name */
    public static final PdfName TYPE0 = new PdfName("Type0");
    /** A name */
    public static final PdfName TYPE1 = new PdfName("Type1");
    /** A name */
    public static final PdfName USAGE = new PdfName("Usage");
    /** A name */
    public static final PdfName USEOUTLINES = new PdfName("UseOutlines");
    /** A name */
    public static final PdfName USERUNIT = new PdfName("UserUnit");
    /** A name */
    public static final PdfName V = new PdfName("V");
    /** A name */
    public static final PdfName VERSION = new PdfName("Version");
    /** A name */
    public static final PdfName VIEW = new PdfName("View");
    /** A name */
    public static final PdfName VISIBLEPAGES = new PdfName("VisiblePages");
    /** A name of an attribute. */
    public static final PdfName W = new PdfName("W");
    /** A name of an attribute. */
    public static final PdfName W2 = new PdfName("W2");
    /** A name of an attribute. */
    public static final PdfName WIDTH = new PdfName("Width");
    /** A name */
    public static final PdfName WIDTHS = new PdfName("Widths");
    /** A name of an encoding */
    public static final PdfName WIN_ANSI_ENCODING = new PdfName("WinAnsiEncoding");
    /** A name of an encoding */
    public static final PdfName WIPE = new PdfName("Wipe");
    /** A name */
    public static final PdfName WHITEPOINT = new PdfName("WhitePoint");
    /** A name */
    public static final PdfName XOBJECT = new PdfName("XObject");
    /** A name */
    public static final PdfName XSTEP = new PdfName("XStep");
    /** A name */
    public static final PdfName XREF = new PdfName("XRef");
    /** A name */
    public static final PdfName YSTEP = new PdfName("YStep");
    /** A name of a base 14 type 1 font */
    public static final PdfName ZAPFDINGBATS = new PdfName("ZapfDingbats");
    /** A name */
    public static final PdfName ZOOM = new PdfName("Zoom");

    /**
     * map strings to all known static names
     * @since 2.1.6
     */
    public static Map<String, PdfName> staticNames;

    /*
     * Use reflection to cache all the static public final names so
     * future <code>PdfName</code> additions don't have to be "added twice".
     * A bit less efficient (around 50ms spent here on a 2.2ghz machine),
     *  but Much Less error prone.
     * @since 2.1.6
     */
    static {
        Field[] fields = PdfName.class.getDeclaredFields();
        staticNames = new HashMap<>(fields.length);
        final int flags = Modifier.STATIC | Modifier.PUBLIC | Modifier.FINAL;
        try {
            for (Field curFld : fields) {
                if ((curFld.getModifiers() & flags) == flags &&
                        curFld.getType().equals(PdfName.class)) {
                    PdfName name = (PdfName) curFld.get(null);
                    staticNames.put(decodeName(name.toString()), name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a new <CODE>PdfName</CODE>. The name length will be checked.
     *
     * @param name the new name
     */
    public PdfName(String name) {
        this(name, true);
    }

    /**
     * Constructs a new <CODE>PdfName</CODE>.
     * @param name the new name
     * @param lengthCheck if <CODE>true</CODE> check the length validity,
     * if <CODE>false</CODE> the name can have any length
     */
    public PdfName(String name, boolean lengthCheck) {
        super(PdfObject.NAME);
        // The minimum number of characters in a name is 0, the maximum is 127 (the '/' not included)
        int length = name.length();
        if (lengthCheck && length > 127)
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.name.1.is.too.long.2.characters", name, String.valueOf(length)));
        bytes = encodeName(name);
    }

    /**
     * Constructs a PdfName.
     *
     * @param bytes the byte representation of the name
     */
    public PdfName(byte[] bytes) {
        super(PdfObject.NAME, bytes);
    }

    // CLASS METHODS

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.<p>
     *
     * @param object the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     * from being compared to this Object.
     */
    public int compareTo(PdfName object) {
        byte[] myBytes = bytes;
        byte[] objBytes = object.bytes;
        int len = Math.min(myBytes.length, objBytes.length);
        for(int i = 0; i < len; i++) {
            if (myBytes[i] > objBytes[i])
                return 1;
            if (myBytes[i] < objBytes[i])
                return -1;
        }
        return Integer.compare(myBytes.length, objBytes.length);
    }

    /**
     * Encodes a plain name given in the unescaped form "AB CD" into "/AB#20CD".
     *
     * @param name the name to encode
     * @return the encoded name
     * @since    2.1.5
     */
    public static byte[] encodeName(String name) {
        int length = name.length();
        ByteBuffer buf = new ByteBuffer(length + 20);
        buf.append('/');
        char c;
        char[] chars = name.toCharArray();
        for (int k = 0; k < length; k++) {
            c = (char)(chars[k] & 0xff);
            // Escape special characters
            switch (c) {
                case ' ':
                case '%':
                case '(':
                case ')':
                case '<':
                case '>':
                case '[':
                case ']':
                case '{':
                case '}':
                case '/':
                case '#':
                    buf.append('#');
                    buf.append(Integer.toString(c, 16));
                    break;
                default:
                    if (c >= 32 && c <= 126)
                        buf.append(c);
                    else {
                        buf.append('#');
                        if (c < 16)
                            buf.append('0');
                        buf.append(Integer.toString(c, 16));
                    }
                    break;
                }
            }
        return buf.toByteArray();
    }

    /**
     * Decodes an escaped name given in the form "/AB#20CD" into "AB CD".
     *
     * @param name the name to decode
     * @return the decoded name
     */
    public static String decodeName(String name) {
        StringBuilder buf = new StringBuilder();
        try {
            int len = name.length();
            for (int k = 1; k < len; ++k) {
                char c = name.charAt(k);
                if (c == '#') {
                    char c1 = name.charAt(k + 1);
                    char c2 = name.charAt(k + 2);
                    c = (char)((PRTokeniser.getHex(c1) << 4) + PRTokeniser.getHex(c2));
                    k += 2;
                }
                buf.append(c);
            }
        }
        catch (IndexOutOfBoundsException e) {
            // empty on purpose
        }
        return buf.toString();
    }
}

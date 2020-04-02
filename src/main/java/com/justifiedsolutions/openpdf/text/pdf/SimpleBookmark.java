/*
 * Copyright 2003 by Paulo Soares.
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

import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import com.justifiedsolutions.openpdf.text.xml.simpleparser.SimpleXMLDocHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Bookmark processing in a simple way. It has some limitations, mainly the only
 * action types supported are GoTo, GoToR, URI and Launch.
 * <p>
 * The list structure is composed by a number of HashMap, keyed by strings, one HashMap
 * for each bookmark.
 * The element values are all strings with the exception of the key "Kids" that has
 * another list for the child bookmarks.
 * <p>
 * All the bookmarks have a "Title" with the
 * bookmark title and optionally a "Style" that can be "bold", "italic" or a
 * combination of both. They can also have a "Color" key with a value of three
 * floats separated by spaces. The key "Open" can have the values "true" or "false" and
 * signals the open status of the children. It's "true" by default.
 * <p>
 * The actions and the parameters can be:
 * <ul>
 * <li>"Action" = "GoTo" - "Page" | "Named"
 * <ul>
 * <li>"Page" = "3 XYZ 70 400 null" - page number followed by a destination (/XYZ is also accepted)
 * <li>"Named" = "named_destination"
 * </ul>
 * <li>"Action" = "GoToR" - "Page" | "Named" | "NamedN", "File", ["NewWindow"]
 * <ul>
 * <li>"Page" = "3 XYZ 70 400 null" - page number followed by a destination (/XYZ is also accepted)
 * <li>"Named" = "named_destination_as_a_string"
 * <li>"NamedN" = "named_destination_as_a_name"
 * <li>"File" - "the_file_to_open"
 * <li>"NewWindow" - "true" or "false"
 * </ul>
 * <li>"Action" = "URI" - "URI"
 * <ul>
 * <li>"URI" = "http://sf.net" - URI to jump to
 * </ul>
 * <li>"Action" = "Launch" - "File"
 * <ul>
 * <li>"File" - "the_file_to_open_or_execute"
 * </ul>
 * @author Paulo Soares (psoares@consiste.pt)
 */
public final class SimpleBookmark implements SimpleXMLDocHandler {
    
    private List<Map<String, Object>> topList;
    private final Stack<Map<String, Object>> attr = new Stack<>();
    
    /** Creates a new instance of SimpleBookmark */
    private SimpleBookmark() {
    }


    static void createOutlineAction(PdfDictionary outline, HashMap map, PdfWriter writer, boolean namedAsNames) {
        try {
            String action = (String)map.get("Action");
            if ("GoTo".equals(action)) {
                String p;
                if ((p = (String)map.get("Named")) != null) {
                    if (namedAsNames)
                        outline.put(PdfName.DEST, new PdfName(p));
                    else
                        outline.put(PdfName.DEST, new PdfString(p, null));
                }
                else if ((p = (String)map.get("Page")) != null) {
                    PdfArray ar = new PdfArray();
                    StringTokenizer tk = new StringTokenizer(p);
                    int n = Integer.parseInt(tk.nextToken());
                    ar.add(writer.getPageReference(n));
                    if (!tk.hasMoreTokens()) {
                        ar.add(PdfName.XYZ);
                        ar.add(new float[]{0, 10000, 0});
                    }
                    else {
                        String fn = tk.nextToken();
                        if (fn.startsWith("/"))
                            fn = fn.substring(1);
                        ar.add(new PdfName(fn));
                        for (int k = 0; k < 4 && tk.hasMoreTokens(); ++k) {
                            fn = tk.nextToken();
                            if (fn.equals("null"))
                                ar.add(PdfNull.PDFNULL);
                            else
                                ar.add(new PdfNumber(fn));
                        }
                    }
                    outline.put(PdfName.DEST, ar);
                }
            }
            else if ("GoToR".equals(action)) {
                String p;
                PdfDictionary dic = new PdfDictionary();
                if ((p = (String)map.get("Named")) != null)
                    dic.put(PdfName.D, new PdfString(p, null));
                else if ((p = (String)map.get("NamedN")) != null)
                    dic.put(PdfName.D, new PdfName(p));
                else if ((p = (String)map.get("Page")) != null){
                    PdfArray ar = new PdfArray();
                    StringTokenizer tk = new StringTokenizer(p);
                    ar.add(new PdfNumber(tk.nextToken()));
                    if (!tk.hasMoreTokens()) {
                        ar.add(PdfName.XYZ);
                        ar.add(new float[]{0, 10000, 0});
                    }
                    else {
                        String fn = tk.nextToken();
                        if (fn.startsWith("/"))
                            fn = fn.substring(1);
                        ar.add(new PdfName(fn));
                        for (int k = 0; k < 4 && tk.hasMoreTokens(); ++k) {
                            fn = tk.nextToken();
                            if (fn.equals("null"))
                                ar.add(PdfNull.PDFNULL);
                            else
                                ar.add(new PdfNumber(fn));
                        }
                    }
                    dic.put(PdfName.D, ar);
                }
                String file = (String)map.get("File");
                if (dic.size() > 0 && file != null) {
                    dic.put(PdfName.S,  PdfName.GOTOR);
                    dic.put(PdfName.F, new PdfString(file));
                    String nw = (String)map.get("NewWindow");
                    if (nw != null) {
                        if (nw.equals("true"))
                            dic.put(PdfName.NEWWINDOW, PdfBoolean.PDFTRUE);
                        else if (nw.equals("false"))
                            dic.put(PdfName.NEWWINDOW, PdfBoolean.PDFFALSE);
                    }
                    outline.put(PdfName.A, dic);
                }
            }
            else if ("URI".equals(action)) {
                String uri = (String)map.get("URI");
                if (uri != null) {
                    PdfDictionary dic = new PdfDictionary();
                    dic.put(PdfName.S, PdfName.URI);
                    dic.put(PdfName.URI, new PdfString(uri));
                    outline.put(PdfName.A, dic);
                }
            }
            else if ("Launch".equals(action)) {
                String file = (String)map.get("File");
                if (file != null) {
                    PdfDictionary dic = new PdfDictionary();
                    dic.put(PdfName.S, PdfName.LAUNCH);
                    dic.put(PdfName.F, new PdfString(file));
                    outline.put(PdfName.A, dic);
                }
            }
        }
        catch (Exception e) {
            // empty on purpose
        }
    }

    public static Object[] iterateOutlines(PdfWriter writer, PdfIndirectReference parent, List kids, boolean namedAsNames) throws IOException {
        PdfIndirectReference[] refs = new PdfIndirectReference[kids.size()];
        for (int k = 0; k < refs.length; ++k)
            refs[k] = writer.getPdfIndirectReference();
        int ptr = 0;
        int count = 0;
        for (Iterator it = kids.listIterator(); it.hasNext(); ++ptr) {
            HashMap map = (HashMap)it.next();
            Object[] lower = null;
            List subKid = (List)map.get("Kids");
            if (subKid != null && !subKid.isEmpty())
                lower = iterateOutlines(writer, refs[ptr], subKid, namedAsNames);
            PdfDictionary outline = new PdfDictionary();
            ++count;
            if (lower != null) {
                outline.put(PdfName.FIRST, (PdfIndirectReference)lower[0]);
                outline.put(PdfName.LAST, (PdfIndirectReference)lower[1]);
                int n = (Integer) lower[2];
                if ("false".equals(map.get("Open"))) {
                    outline.put(PdfName.COUNT, new PdfNumber(-n));
                }
                else {
                    outline.put(PdfName.COUNT, new PdfNumber(n));
                    count += n;
                }
            }
            outline.put(PdfName.PARENT, parent);
            if (ptr > 0)
                outline.put(PdfName.PREV, refs[ptr - 1]);
            if (ptr < refs.length - 1)
                outline.put(PdfName.NEXT, refs[ptr + 1]);
            outline.put(PdfName.TITLE, new PdfString((String)map.get("Title"), PdfObject.TEXT_UNICODE));
            String color = (String)map.get("Color");
            if (color != null) {
                try {
                    PdfArray arr = new PdfArray();
                    StringTokenizer tk = new StringTokenizer(color);
                    for (int k = 0; k < 3; ++k) {
                        float f = Float.parseFloat(tk.nextToken());
                        if (f < 0) f = 0;
                        if (f > 1) f = 1;
                        arr.add(new PdfNumber(f));
                    }
                    outline.put(PdfName.C, arr);
                } catch(Exception e){} //in case it's malformed
            }
            String style = (String)map.get("Style");
            if (style != null) {
                style = style.toLowerCase();
                int bits = 0;
                if (style.contains("italic"))
                    bits |= 1;
                if (style.contains("bold"))
                    bits |= 2;
                if (bits != 0)
                    outline.put(PdfName.F, new PdfNumber(bits));
            }
            createOutlineAction(outline, map, writer, namedAsNames);
            writer.addToBody(outline, refs[ptr]);
        }
        return new Object[]{refs[0], refs[refs.length - 1], count};
    }

    public void endDocument() {
    }

    public void endElement(String tag) {
        if (tag.equals("Bookmark")) {
            if (attr.isEmpty())
                return;
            else
                throw new RuntimeException(MessageLocalization.getComposedMessage("bookmark.end.tag.out.of.place"));
        }
        if (!tag.equals("Title"))
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.end.tag.1", tag));
        Map<String, Object> attributes = attr.pop();
        String title = (String) attributes.get("Title");
        attributes.put("Title",  title.trim());
        String named = (String) attributes.get("Named");
        if (named != null)
            attributes.put("Named", unEscapeBinaryString(named));
        named = (String) attributes.get("NamedN");
        if (named != null)
            attributes.put("NamedN", unEscapeBinaryString(named));
        if (attr.isEmpty())
            topList.add(attributes);
        else {
            Map<String, Object> parent = attr.peek();
            List<Map<String, Object>> kids = (List<Map<String, Object>>) parent.get("Kids");
            if (kids == null) {
                kids = new ArrayList<>();
                parent.put("Kids", kids);
            }
            kids.add(attributes);
        }
    }

    public void startDocument() {
    }

    public void startElement(String tag, Map<String, String> h) {
        if (topList == null) {
            if (tag.equals("Bookmark")) {
                topList = new ArrayList<>();
                return;
            }
            else
                throw new RuntimeException(MessageLocalization.getComposedMessage("root.element.is.not.bookmark.1", tag));
        }
        if (!tag.equals("Title"))
            throw new RuntimeException(MessageLocalization.getComposedMessage("tag.1.not.allowed", tag));
        Map<String, Object> attributes = new HashMap<>(h);
        attributes.put("Title", "");
        attributes.remove("Kids");
        attr.push(attributes);
    }

    public void text(String str) {
        if (attr.isEmpty())
            return;
        Map<String, Object> attributes = attr.peek();
        String title = (String) attributes.get("Title");
        title += str;
        attributes.put("Title", title);
    }

    public static String unEscapeBinaryString(String s) {
        StringBuilder buf = new StringBuilder();
        char[] cc = s.toCharArray();
        int len = cc.length;
        for (int k = 0; k < len; ++k) {
            char c = cc[k];
            if (c == '\\') {
                if (++k >= len) {
                    buf.append('\\');
                    break;
                }
                c = cc[k];
                if (c >= '0' && c <= '7') {
                    int n = c - '0';
                    ++k;
                    for (int j = 0; j < 2 && k < len; ++j) {
                        c = cc[k];
                        if (c >= '0' && c <= '7') {
                            ++k;
                            n = n * 8 + c - '0';
                        }
                        else {
                            break;
                        }
                    }
                    --k;
                    buf.append((char)n);
                }
                else
                    buf.append(c);
            }
            else
                buf.append(c);
        }
        return buf.toString();
    }

}

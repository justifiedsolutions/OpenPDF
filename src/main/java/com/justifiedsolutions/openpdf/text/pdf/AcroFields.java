/*
 * Copyright 2003-2005 by Paulo Soares.
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

import com.justifiedsolutions.openpdf.text.ExceptionConverter;
import com.justifiedsolutions.openpdf.text.Font;
import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Query and change fields in existing documents either by method calls or by FDF merging.
 *
 * @author Paulo Soares (psoares@consiste.pt)
 */
public class AcroFields {

  PdfReader reader;
  PdfWriter writer;
  private Map<String, Item> fields;
  private int topFirst;
  private Map<String, int[]> sigNames;
  private boolean append;
  public static final int DA_FONT = 0;
  public static final int DA_SIZE = 1;
  public static final int DA_COLOR = 2;

  /**
   * A field type invalid or not found.
   */
  public static final int FIELD_TYPE_NONE = 0;

  /**
   * A field type.
   */
  public static final int FIELD_TYPE_PUSHBUTTON = 1;

  /**
   * A field type.
   */
  public static final int FIELD_TYPE_CHECKBOX = 2;

  /**
   * A field type.
   */
  public static final int FIELD_TYPE_RADIOBUTTON = 3;

  /**
   * A field type.
   */
  public static final int FIELD_TYPE_TEXT = 4;

  /**
   * A field type.
   */
  public static final int FIELD_TYPE_LIST = 5;

  /**
   * A field type.
   */
  public static final int FIELD_TYPE_COMBO = 6;

  /**
   * A field type.
   */
  public static final int FIELD_TYPE_SIGNATURE = 7;

  private boolean lastWasString;

  private Map<String, BaseFont> localFonts = new HashMap<>();

  private float extraMarginLeft;
  private float extraMarginTop;
  private List<BaseFont> substitutionFonts;

  AcroFields(PdfReader reader, PdfWriter writer) {
    this.reader = reader;
    this.writer = writer;
    if (writer instanceof PdfStamperImp) {
      append = ((PdfStamperImp) writer).isAppend();
    }
    fill();
  }

  void fill() {
    fields = new HashMap<>();
    PdfDictionary top = (PdfDictionary) PdfReader.getPdfObjectRelease(reader.getCatalog().get(PdfName.ACROFORM));
    if (top == null) {
      return;
    }
    PdfArray arrfds = (PdfArray) PdfReader.getPdfObjectRelease(top.get(PdfName.FIELDS));
    if (arrfds == null || arrfds.size() == 0) {
      return;
    }
    for (int k = 1; k <= reader.getNumberOfPages(); ++k) {
      PdfDictionary page = reader.getPageNRelease(k);
      PdfArray annots = (PdfArray) PdfReader.getPdfObjectRelease(page.get(PdfName.ANNOTS), page);
      if (annots == null) {
        continue;
      }
      for (int j = 0; j < annots.size(); ++j) {
        PdfDictionary annot = annots.getAsDict(j);
        if (annot == null) {
          PdfReader.releaseLastXrefPartial(annots.getAsIndirectObject(j));
          continue;
        }
        if (!PdfName.WIDGET.equals(annot.getAsName(PdfName.SUBTYPE))) {
          PdfReader.releaseLastXrefPartial(annots.getAsIndirectObject(j));
          continue;
        }
        PdfDictionary widget = annot;
        PdfDictionary dic = new PdfDictionary();
        dic.putAll(annot);
        String name = "";
        PdfDictionary value = null;
        PdfObject lastV = null;
        int lastIDRNumber = -1;
        PdfIndirectReference indirectObject = annots.getAsIndirectObject(j);
        if (indirectObject != null) {
        	lastIDRNumber = indirectObject.getNumber();
        }
        while (annot != null) {
          dic.mergeDifferent(annot);
          PdfString t = annot.getAsString(PdfName.T);
          if (t != null) {
            name = t.toUnicodeString() + "." + name;
          }
          if (lastV == null && annot.get(PdfName.V) != null) {
            lastV = PdfReader.getPdfObjectRelease(annot.get(PdfName.V));
          }
          if (value == null && t != null) {
            value = annot;
            if (annot.get(PdfName.V) == null && lastV != null) {
              value.put(PdfName.V, lastV);
            }
          }
          int parentIDRNumber = -1;
          PdfIndirectReference asIndirectObject = annot.getAsIndirectObject(PdfName.PARENT);
          if (asIndirectObject != null) {
        	  parentIDRNumber = asIndirectObject.getNumber();
          }
          if (parentIDRNumber != -1 && lastIDRNumber != parentIDRNumber) {
              annot = annot.getAsDict(PdfName.PARENT);
              lastIDRNumber = parentIDRNumber;
          } else {
        	  annot = null;
          }
        }
        if (name.length() > 0) {
          name = name.substring(0, name.length() - 1);
        }
        Item item = fields.get(name);
        if (item == null) {
          item = new Item();
          fields.put(name, item);
        }
        if (value == null) {
          item.addValue(widget);
        } else {
          item.addValue(value);
        }
        item.addWidget(widget);
        item.addWidgetRef(annots.getAsIndirectObject(j)); // must be a reference
        dic.mergeDifferent(top);
        item.addMerged(dic);
        item.addPage(k);
        item.addTabOrder(j);
      }
    }
    // some tools produce invisible signatures without an entry in the page annotation array
    // look for a single level annotation
    PdfNumber sigFlags = top.getAsNumber(PdfName.SIGFLAGS);
    if (sigFlags == null || (sigFlags.intValue() & 1) != 1) {
      return;
    }
    for (int j = 0; j < arrfds.size(); ++j) {
      PdfDictionary annot = arrfds.getAsDict(j);
      if (annot == null) {
        PdfReader.releaseLastXrefPartial(arrfds.getAsIndirectObject(j));
        continue;
      }
      if (!PdfName.WIDGET.equals(annot.getAsName(PdfName.SUBTYPE))) {
        PdfReader.releaseLastXrefPartial(arrfds.getAsIndirectObject(j));
        continue;
      }
      PdfArray kids = (PdfArray) PdfReader.getPdfObjectRelease(annot.get(PdfName.KIDS));
      if (kids != null) {
        continue;
      }
      PdfDictionary dic = new PdfDictionary();
      dic.putAll(annot);
      PdfString t = annot.getAsString(PdfName.T);
      if (t == null) {
        continue;
      }
      String name = t.toUnicodeString();
      if (fields.containsKey(name)) {
        continue;
      }
      Item item = new Item();
      fields.put(name, item);
      item.addValue(dic);
      item.addWidget(dic);
      item.addWidgetRef(arrfds.getAsIndirectObject(j)); // must be a reference
      item.addMerged(dic);
      item.addPage(-1);
      item.addTabOrder(-1);
    }
  }

  /**
   * Gets the list of appearance names. Use it to get the names allowed with radio and checkbox fields. If the /Opt key exists the values
   * will also be included. The name 'Off' may also be valid even if not returned in the list.
   *
   * @param fieldName the fully qualified field name
   * @return the list of names or <CODE>null</CODE> if the field does not exist
   */
  public String[] getAppearanceStates(String fieldName) {
    Item fd = fields.get(fieldName);
    if (fd == null) {
      return null;
    }
    Map<String, ?> names = new HashMap<>();
    PdfDictionary vals = fd.getValue(0);
    PdfString stringOpt = vals.getAsString(PdfName.OPT);
    if (stringOpt != null) {
      names.put(stringOpt.toUnicodeString(), null);
    } else {
      PdfArray arrayOpt = vals.getAsArray(PdfName.OPT);
      if (arrayOpt != null) {
        for (int k = 0; k < arrayOpt.size(); ++k) {
          PdfString valStr = arrayOpt.getAsString(k);
          if (valStr != null) {
            names.put(valStr.toUnicodeString(), null);
          }
        }
      }
    }
    for (int k = 0; k < fd.size(); ++k) {
      PdfDictionary dic = fd.getWidget(k);
      dic = dic.getAsDict(PdfName.AP);
      if (dic == null) {
        continue;
      }
      dic = dic.getAsDict(PdfName.N);
      if (dic == null) {
        continue;
      }
      for (Object o : dic.getKeys()) {
        String name = PdfName.decodeName(o.toString());
        names.put(name, null);
      }
    }
    String[] out = new String[names.size()];
    return names.keySet().toArray(out);
  }

  public static Object[] splitDAelements(String da) {
    try {
      PRTokeniser tk = new PRTokeniser(PdfEncodings.convertToBytes(da, null));
      List<String> stack = new ArrayList<>();
      Object[] ret = new Object[3];
      while (tk.nextToken()) {
        if (tk.getTokenType() == PRTokeniser.TK_COMMENT) {
          continue;
        }
        if (tk.getTokenType() == PRTokeniser.TK_OTHER) {
          String operator = tk.getStringValue();
          switch (operator) {
            case "Tf":
              if (stack.size() >= 2) {
                ret[DA_FONT] = stack.get(stack.size() - 2);
                ret[DA_SIZE] = Float.parseFloat(stack.get(stack.size() - 1));
              }
              break;
            case "g":
              if (stack.size() >= 1) {
                float gray = Float.parseFloat(stack.get(stack.size() - 1));
                if (gray != 0) {
                  ret[DA_COLOR] = new GrayColor(gray);
                }
              }
              break;
            case "rg":
              if (stack.size() >= 3) {
                float red = Float.parseFloat(stack.get(stack.size() - 3));
                float green = Float.parseFloat(stack.get(stack.size() - 2));
                float blue = Float.parseFloat(stack.get(stack.size() - 1));
                ret[DA_COLOR] = new Color(red, green, blue);
              }
              break;
            case "k":
              if (stack.size() >= 4) {
                float cyan = Float.parseFloat(stack.get(stack.size() - 4));
                float magenta = Float.parseFloat(stack.get(stack.size() - 3));
                float yellow = Float.parseFloat(stack.get(stack.size() - 2));
                float black = Float.parseFloat(stack.get(stack.size() - 1));
                ret[DA_COLOR] = new CMYKColor(cyan, magenta, yellow, black);
              }
              break;
          }
          stack.clear();
        } else {
          stack.add(tk.getStringValue());
        }
      }
      return ret;
    } catch (IOException ioe) {
      throw new ExceptionConverter(ioe);
    }
  }

  Color getMKColor(PdfArray ar) {
    if (ar == null) {
      return null;
    }
    switch (ar.size()) {
      case 1:
        return new GrayColor(ar.getAsNumber(0).floatValue());
      case 3:
        return new Color(ExtendedColor.normalize(ar.getAsNumber(0).floatValue()), ExtendedColor.normalize(ar.getAsNumber(1).floatValue()),
            ExtendedColor.normalize(ar.getAsNumber(2).floatValue()));
      case 4:
        return new CMYKColor(ar.getAsNumber(0).floatValue(), ar.getAsNumber(1).floatValue(), ar.getAsNumber(2).floatValue(),
            ar.getAsNumber(3).floatValue());
      default:
        return null;
    }
  }


  /**
   * Sets a field property. Valid property names are:
   * <p>
   * <ul>
   * <li>flags - a set of flags specifying various characteristics of the field's widget annotation.
   * The value of this entry replaces that of the F entry in the form's corresponding annotation dictionary.<br>
   * <li>setflags - a set of flags to be set (turned on) in the F entry of the form's corresponding
   * widget annotation dictionary. Bits equal to 1 cause the corresponding bits in F to be set to 1.<br>
   * <li>clrflags - a set of flags to be cleared (turned off) in the F entry of the form's corresponding
   * widget annotation dictionary. Bits equal to 1 cause the corresponding bits in F to be set to 0.<br>
   * <li>fflags - a set of flags specifying various characteristics of the field. The value
   * of this entry replaces that of the Ff entry in the form's corresponding field dictionary.<br>
   * <li>setfflags - a set of flags to be set (turned on) in the Ff entry of the form's corresponding
   * field dictionary. Bits equal to 1 cause the corresponding bits in Ff to be set to 1.<br>
   * <li>clrfflags - a set of flags to be cleared (turned off) in the Ff entry of the form's corresponding
   * field dictionary. Bits equal to 1 cause the corresponding bits in Ff to be set to 0.<br>
   * </ul>
   *
   * @param field the field name
   * @param name the property name
   * @param value the property value
   * @param inst an array of <CODE>int</CODE> indexing into <CODE>AcroField.Item.merged</CODE> elements to process. Set to <CODE>null</CODE>
   * to process all
   * @return <CODE>true</CODE> if the property exists, <CODE>false</CODE> otherwise
   */
  public boolean setFieldProperty(String field, String name, int value, int[] inst) {
    if (writer == null) {
      throw new RuntimeException(MessageLocalization.getComposedMessage("this.acrofields.instance.is.read.only"));
    }
    Item item = fields.get(field);
    if (item == null) {
      return false;
    }
    InstHit hit = new InstHit(inst);
    if (name.equalsIgnoreCase("flags")) {
      PdfNumber num = new PdfNumber(value);
      for (int k = 0; k < item.size(); ++k) {
        if (hit.isHit(k)) {
          item.getMerged(k).put(PdfName.F, num);
          item.getWidget(k).put(PdfName.F, num);
          markUsed(item.getWidget(k));
        }
      }
    } else if (name.equalsIgnoreCase("setflags")) {
      for (int k = 0; k < item.size(); ++k) {
        if (hit.isHit(k)) {
          PdfNumber num = item.getWidget(k).getAsNumber(PdfName.F);
          int val = 0;
          if (num != null) {
            val = num.intValue();
          }
          num = new PdfNumber(val | value);
          item.getMerged(k).put(PdfName.F, num);
          item.getWidget(k).put(PdfName.F, num);
          markUsed(item.getWidget(k));
        }
      }
    } else if (name.equalsIgnoreCase("clrflags")) {
      for (int k = 0; k < item.size(); ++k) {
        if (hit.isHit(k)) {
          PdfDictionary widget = item.getWidget(k);
          PdfNumber num = widget.getAsNumber(PdfName.F);
          int val = 0;
          if (num != null) {
            val = num.intValue();
          }
          num = new PdfNumber(val & (~value));
          item.getMerged(k).put(PdfName.F, num);
          widget.put(PdfName.F, num);
          markUsed(widget);
        }
      }
    } else if (name.equalsIgnoreCase("fflags")) {
      PdfNumber num = new PdfNumber(value);
      for (int k = 0; k < item.size(); ++k) {
        if (hit.isHit(k)) {
          item.getMerged(k).put(PdfName.FF, num);
          item.getValue(k).put(PdfName.FF, num);
          markUsed(item.getValue(k));
        }
      }
    } else if (name.equalsIgnoreCase("setfflags")) {
      for (int k = 0; k < item.size(); ++k) {
        if (hit.isHit(k)) {
          PdfDictionary valDict = item.getValue(k);
          PdfNumber num = valDict.getAsNumber(PdfName.FF);
          int val = 0;
          if (num != null) {
            val = num.intValue();
          }
          num = new PdfNumber(val | value);
          item.getMerged(k).put(PdfName.FF, num);
          valDict.put(PdfName.FF, num);
          markUsed(valDict);
        }
      }
    } else if (name.equalsIgnoreCase("clrfflags")) {
      for (int k = 0; k < item.size(); ++k) {
        if (hit.isHit(k)) {
          PdfDictionary valDict = item.getValue(k);
          PdfNumber num = valDict.getAsNumber(PdfName.FF);
          int val = 0;
          if (num != null) {
            val = num.intValue();
          }
          num = new PdfNumber(val & (~value));
          item.getMerged(k).put(PdfName.FF, num);
          valDict.put(PdfName.FF, num);
          markUsed(valDict);
        }
      }
    } else {
      return false;
    }
    return true;
  }

  /**
   * Gets all the fields. The fields are keyed by the fully qualified field name and the value is an instance of
   * <CODE>AcroFields.Item</CODE>.
   *
   * @deprecated use {@link AcroFields#getAllFields()}
   *
   * @return all the fields
   */
  @Deprecated
  public HashMap getFields() {
    return (HashMap) fields;
  }

  /**
   * Gets all the fields. The fields are keyed by the fully qualified field name and the value is an instance of
   * <CODE>AcroFields.Item</CODE>.
   *
   * @return all the fields
   */
  public Map<String, Item> getAllFields() {
    return fields;
  }

  /**
   * The field representations for retrieval and modification.
   */
  public static class Item {

    /**
     * <CODE>writeToAll</CODE> constant.
     *
     * @since 2.1.5
     */
    public static final int WRITE_MERGED = 1;

    /**
     * <CODE>writeToAll</CODE> and <CODE>markUsed</CODE> constant.
     *
     * @since 2.1.5
     */
    public static final int WRITE_WIDGET = 2;

    /**
     * <CODE>writeToAll</CODE> and <CODE>markUsed</CODE> constant.
     *
     * @since 2.1.5
     */
    public static final int WRITE_VALUE = 4;

    /**
     * This function writes the given key/value pair to all the instances of merged, widget, and/or value, depending on the
     * <code>writeFlags</code> setting
     *
     * @param key you'll never guess what this is for.
     * @param value if value is null, the key will be removed
     * @param writeFlags ORed together WRITE_* flags
     * @since 2.1.5
     */
    public void writeToAll(PdfName key, PdfObject value, int writeFlags) {
      int i;
      PdfDictionary curDict = null;
      if ((writeFlags & WRITE_MERGED) != 0) {
        for (i = 0; i < merged.size(); ++i) {
          curDict = getMerged(i);
          curDict.put(key, value);
        }
      }
      if ((writeFlags & WRITE_WIDGET) != 0) {
        for (i = 0; i < widgets.size(); ++i) {
          curDict = getWidget(i);
          curDict.put(key, value);
        }
      }
      if ((writeFlags & WRITE_VALUE) != 0) {
        for (i = 0; i < values.size(); ++i) {
          curDict = getValue(i);
          curDict.put(key, value);
        }
      }
    }

    /**
     * Mark all the item dictionaries used matching the given flags
     *
     * @param writeFlags WRITE_MERGED is ignored
     * @since 2.1.5
     */
    public void markUsed(AcroFields parentFields, int writeFlags) {
      if ((writeFlags & WRITE_VALUE) != 0) {
        for (int i = 0; i < size(); ++i) {
          parentFields.markUsed(getValue(i));
        }
      }
      if ((writeFlags & WRITE_WIDGET) != 0) {
        for (int i = 0; i < size(); ++i) {
          parentFields.markUsed(getWidget(i));
        }
      }
    }

    /**
     * An array of <CODE>PdfDictionary</CODE> where the value tag /V is present.
     **/
    private ArrayList<PdfDictionary> values = new ArrayList<>();

    /**
     * An array of <CODE>PdfDictionary</CODE> with the widgets.
     */
    private ArrayList<PdfDictionary> widgets = new ArrayList<>();

    /**
     * An array of <CODE>PdfDictionary</CODE> with the widget references.
     *
     */
    private ArrayList<PdfIndirectReference> widgetRefs = new ArrayList<>();

    /**
     * An array of <CODE>PdfDictionary</CODE> with all the field and widget tags merged.
     *
     */
    private ArrayList<PdfDictionary> merged = new ArrayList<>();

    /**
     * An array of <CODE>Integer</CODE> with the page numbers where the widgets are displayed.
     *
     */
    private ArrayList<Integer> page = new ArrayList<>();

    /**
     * An array of <CODE>Integer</CODE> with the tab order of the field in the page.
     *
     */
    private ArrayList<Integer> tabOrder = new ArrayList<>();

    /**
     * Preferred method of determining the number of instances of a given field.
     *
     * @return number of instances
     * @since 2.1.5
     */
    public int size() {
      return values.size();
    }

    /**
     * Remove the given instance from this item.  It is possible to remove all instances using this function.
     *
     * @since 2.1.5
     */
    void remove(int killIdx) {
      values.remove(killIdx);
      widgets.remove(killIdx);
      widgetRefs.remove(killIdx);
      merged.remove(killIdx);
      page.remove(killIdx);
      tabOrder.remove(killIdx);
    }

    /**
     * Retrieve the value dictionary of the given instance
     *
     * @param idx instance index
     * @return dictionary storing this instance's value.  It may be shared across instances.
     * @since 2.1.5
     */
    public PdfDictionary getValue(int idx) {
      return values.get(idx);
    }

    /**
     * Add a value dict to this Item
     *
     * @param value new value dictionary
     * @since 2.1.5
     */
    void addValue(PdfDictionary value) {
      values.add(value);
    }

    /**
     * Retrieve the widget dictionary of the given instance
     *
     * @param idx instance index
     * @return The dictionary found in the appropriate page's Annot array.
     * @since 2.1.5
     */
    public PdfDictionary getWidget(int idx) {
      return widgets.get(idx);
    }

    /**
     * Add a widget dict to this Item
     *
     * @since 2.1.5
     */
    void addWidget(PdfDictionary widget) {
      widgets.add(widget);
    }

    /**
     * Retrieve the reference to the given instance
     *
     * @param idx instance index
     * @return reference to the given field instance
     * @since 2.1.5
     */
    public PdfIndirectReference getWidgetRef(int idx) {
      return widgetRefs.get(idx);
    }

    /**
     * Add a widget ref to this Item
     *
     * @since 2.1.5
     */
    void addWidgetRef(PdfIndirectReference widgRef) {
      widgetRefs.add(widgRef);
    }

    /**
     * Retrieve the merged dictionary for the given instance.  The merged dictionary contains all the keys present in parent fields, though
     * they may have been overwritten (or modified?) by children. Example: a merged radio field dict will contain /V
     *
     * @param idx instance index
     * @return the merged dictionary for the given instance
     * @since 2.1.5
     */
    public PdfDictionary getMerged(int idx) {
      return merged.get(idx);
    }

    /**
     * Adds a merged dictionary to this Item.
     *
     * @since 2.1.5
     */
    void addMerged(PdfDictionary mergeDict) {
      merged.add(mergeDict);
    }

    /**
     * Retrieve the page number of the given instance
     *
     * @return remember, pages are "1-indexed", not "0-indexed" like field instances.
     * @since 2.1.5
     */
    public Integer getPage(int idx) {
      return page.get(idx);
    }

    /**
     * Adds a page to the current Item.
     *
     * @since 2.1.5
     */
    void addPage(int pg) {
      page.add(pg);
    }

    /**
     * forces a page value into the Item.
     *
     * @since 2.1.5
     */
    void forcePage(int idx, int pg) {
      page.set(idx, pg);
    }

    /**
     * Gets the tabOrder.
     *
     * @return tab index of the given field instance
     * @since 2.1.5
     */
    public Integer getTabOrder(int idx) {
      return tabOrder.get(idx);
    }

    /**
     * Adds a tab order value to this Item.
     *
     * @since 2.1.5
     */
    void addTabOrder(int order) {
      tabOrder.add(order);
    }
  }

  private static class InstHit {

    IntHashtable hits;

    public InstHit(int[] inst) {
      if (inst == null) {
        return;
      }
      hits = new IntHashtable();
      for (int i : inst) {
        hits.put(i, 1);
      }
    }

    public boolean isHit(int n) {
      if (hits == null) {
        return true;
      }
      return hits.containsKey(n);
    }
  }

  /**
   * Gets the field names that have signatures and are signed.
   *
   * @deprecated user {@link AcroFields#getSignedFieldNames()}
   *
   * @return the field names that have signatures and are signed
   */
  @Deprecated
  @SuppressWarnings("unchecked")
  public ArrayList<String> getSignatureNames() {
      return (ArrayList<String>) getSignedFieldNames();
  }

  /**
   * Gets the field names that have signatures and are signed.
   *
   * @return the field names that have signatures and are signed
   */
  public List<String> getSignedFieldNames() {
    if (sigNames != null) {
      return new ArrayList<>(sigNames.keySet());
    }
    sigNames = new HashMap<>();
    List<Object[]> sorter = new ArrayList<>();
    for (Map.Entry<String, Item> entry : fields.entrySet()) {
      Item item = entry.getValue();
      PdfDictionary merged = item.getMerged(0);
      if (!PdfName.SIG.equals(merged.get(PdfName.FT))) {
        continue;
      }
      PdfDictionary v = merged.getAsDict(PdfName.V);
      if (v == null) {
        continue;
      }
      PdfString contents = v.getAsString(PdfName.CONTENTS);
      if (contents == null) {
        continue;
      }
      PdfArray ro = v.getAsArray(PdfName.BYTERANGE);
      if (ro == null) {
        continue;
      }
      int rangeSize = ro.size();
      if (rangeSize < 2) {
        continue;
      }
      /*
       * From the PDF32000_2008 spec: Byterange: An array of pairs of integers
       * (starting byte offset, length in bytes) Also see:
       * https://pdf-insecurity.org/download/paper.pdf
       */
      int lengthOfSignedBlocks = 0;
      for (int i = rangeSize - 1; i > 0; i = i - 2) {
        lengthOfSignedBlocks += ro.getAsNumber(i).intValue();
      }
      int unsignedBlock = contents.getOriginalBytes().length * 2 + 2;
      int length = lengthOfSignedBlocks + unsignedBlock;
      sorter.add(new Object[]{entry.getKey(), new int[]{length, 0}});
    }
    sorter.sort(new SorterComparator());
    if (!sorter.isEmpty()) {
      if (((int[]) sorter.get(sorter.size() - 1)[1])[0] == reader.getFileLength()) {
        totalRevisions = sorter.size();
      } else {
        totalRevisions = sorter.size() + 1;
      }
      for (int k = 0; k < sorter.size(); ++k) {
        Object[] objs = sorter.get(k);
        String name = (String) objs[0];
        int[] p = (int[]) objs[1];
        p[1] = k + 1;
        sigNames.put(name, p);
      }
    }
    return new ArrayList<>(sigNames.keySet());
  }

  /**
   * Gets the field names that have blank signatures.
   *
   * @deprecated use {@link AcroFields#getFieldNamesWithBlankSignatures()}
   *
   * @return the field names that have blank signatures
   */
  @Deprecated
  public ArrayList getBlankSignatureNames() {
    return (ArrayList) getFieldNamesWithBlankSignatures();
  }

    /**
     * Gets the field names that have blank signatures.
     *
     * @return the field names that have blank signatures
     */
  public List<String> getFieldNamesWithBlankSignatures() {
    getSignedFieldNames();
    List<String> sigs = new ArrayList<>();
    for (Map.Entry<String, Item> entry : fields.entrySet()) {
      Item item = entry.getValue();
      PdfDictionary merged = item.getMerged(0);
      if (!PdfName.SIG.equals(merged.getAsName(PdfName.FT))) {
        continue;
      }
      if (sigNames.containsKey(entry.getKey())) {
        continue;
      }
      sigs.add(entry.getKey());
    }
    return sigs;
  }

  private void markUsed(PdfObject obj) {
    if (!append) {
      return;
    }
    ((PdfStamperImp) writer).markUsed(obj);
  }


  /**
   * Adds a substitution font to the list. The fonts in this list will be used if the original font doesn't contain the needed glyphs.
   *
   * @param font the font
   */
  public void addSubstitutionFont(BaseFont font) {
    if (substitutionFonts == null) {
      substitutionFonts = new ArrayList<>();
    }
    substitutionFonts.add(font);
  }

  private static final HashMap<String, String[]> stdFieldFontNames = new HashMap<>();

  /**
   * Holds value of property totalRevisions.
   */
  private int totalRevisions;


  static {
    stdFieldFontNames.put("CoBO", new String[]{"Courier-BoldOblique"});
    stdFieldFontNames.put("CoBo", new String[]{"Courier-Bold"});
    stdFieldFontNames.put("CoOb", new String[]{"Courier-Oblique"});
    stdFieldFontNames.put("Cour", new String[]{"Courier"});
    stdFieldFontNames.put("HeBO", new String[]{"Helvetica-BoldOblique"});
    stdFieldFontNames.put("HeBo", new String[]{"Helvetica-Bold"});
    stdFieldFontNames.put("HeOb", new String[]{"Helvetica-Oblique"});
    stdFieldFontNames.put("Helv", new String[]{"Helvetica"});
    stdFieldFontNames.put("Symb", new String[]{"Symbol"});
    stdFieldFontNames.put("TiBI", new String[]{"Times-BoldItalic"});
    stdFieldFontNames.put("TiBo", new String[]{"Times-Bold"});
    stdFieldFontNames.put("TiIt", new String[]{"Times-Italic"});
    stdFieldFontNames.put("TiRo", new String[]{"Times-Roman"});
    stdFieldFontNames.put("ZaDb", new String[]{"ZapfDingbats"});
    stdFieldFontNames.put("HySm", new String[]{"HYSMyeongJo-Medium", "UniKS-UCS2-H"});
    stdFieldFontNames.put("HyGo", new String[]{"HYGoThic-Medium", "UniKS-UCS2-H"});
    stdFieldFontNames.put("KaGo", new String[]{"HeiseiKakuGo-W5", "UniKS-UCS2-H"});
    stdFieldFontNames.put("KaMi", new String[]{"HeiseiMin-W3", "UniJIS-UCS2-H"});
    stdFieldFontNames.put("MHei", new String[]{"MHei-Medium", "UniCNS-UCS2-H"});
    stdFieldFontNames.put("MSun", new String[]{"MSung-Light", "UniCNS-UCS2-H"});
    stdFieldFontNames.put("STSo", new String[]{"STSong-Light", "UniGB-UCS2-H"});
  }

  private static class RevisionStream extends InputStream {

    private byte[] b = new byte[1];
    private RandomAccessFileOrArray raf;
    private int length;
    private int rangePosition = 0;
    private boolean closed;

    private RevisionStream(RandomAccessFileOrArray raf, int length) {
      this.raf = raf;
      this.length = length;
    }

    public int read() throws IOException {
      int n = read(b);
      if (n != 1) {
        return -1;
      }
      return b[0] & 0xff;
    }

    public int read(byte[] b, int off, int len) throws IOException {
      if (b == null) {
        throw new NullPointerException();
      } else if ((off < 0) || (off > b.length) || (len < 0) ||
          ((off + len) > b.length) || ((off + len) < 0)) {
        throw new IndexOutOfBoundsException();
      } else if (len == 0) {
        return 0;
      }
      if (rangePosition >= length) {
        close();
        return -1;
      }
      int elen = Math.min(len, length - rangePosition);
      raf.readFully(b, off, elen);
      rangePosition += elen;
      return elen;
    }

    public void close() throws IOException {
      if (!closed) {
        raf.close();
        closed = true;
      }
    }
  }

  private static class SorterComparator implements Comparator<Object[]> {

    public int compare(Object[] o1, Object[] o2) {
      int n1 = ((int[]) o1[1])[0];
      int n2 = ((int[]) o2[1])[0];
      return n1 - n2;
    }
  }

  /**
   * Gets the list of substitution fonts. The list is composed of <CODE>BaseFont</CODE> and can be <CODE>null</CODE>. The fonts in this list
   * will be used if the original font doesn't contain the needed glyphs.
   *
   * @return the list
   */
  @Deprecated
  public ArrayList getSubstitutionFonts() {
    return (ArrayList) substitutionFonts;
  }

  /**
   * Gets the list of substitution fonts. The list is composed of <CODE>BaseFont</CODE> and can be <CODE>null</CODE>. The fonts in this list
   * will be used if the original font doesn't contain the needed glyphs.
   *
   * @return the list
   */
  public List<BaseFont> getAllSubstitutionFonts() {
    return substitutionFonts;
  }

  /**
   * Sets a list of substitution fonts. The list is composed of <CODE>BaseFont</CODE> and can also be <CODE>null</CODE>. The fonts in this
   * list will be used if the original font doesn't contain the needed glyphs.
   *
   * @param substitutionFonts the list
   */
  @Deprecated
  @SuppressWarnings("unchecked")
  public void setSubstitutionFonts(ArrayList substitutionFonts) {
    this.substitutionFonts = substitutionFonts;
  }

  /**
   * Sets a list of substitution fonts. The list is composed of <CODE>BaseFont</CODE> and can also be <CODE>null</CODE>. The fonts in this
   * list will be used if the original font doesn't contain the needed glyphs.
   *
   * @param substitutionFonts the list
   */
  public void setAllSubstitutionFonts(List<BaseFont> substitutionFonts) {
    this.substitutionFonts = substitutionFonts;
  }


}

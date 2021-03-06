/*
 * $Id: PdfReader.java 4096 2009-11-12 15:31:13Z blowagie $
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

import com.justifiedsolutions.openpdf.text.ExceptionConverter;
import com.justifiedsolutions.openpdf.text.MessageLocalization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

/**
 * Reads a PDF document.
 * 
 * @author Paulo Soares (psoares@consiste.pt)
 * @author Kazuya Ujihara
 */
public class PdfReader {

  private static final byte[] endstream = PdfEncodings
          .convertToBytes("endstream", null);
  private static final byte[] endobj = PdfEncodings.convertToBytes("endobj", null);
  protected PRTokeniser tokens;
  // Each xref pair is a position
  // type 0 -> -1, 0
  // type 1 -> offset, 0
  // type 2 -> index, obj num
  protected int[] xref;
  protected IntHashtable objStmToOffset;
  private List<PdfObject> xrefObj;

  protected List<PdfObject> strings = new ArrayList<>();
  private int objNum;
  private int objGen;

  /**
   * Gets a new file instance of the original PDF document.
   * 
   * @return a new file instance of the original PDF document
   */
  public RandomAccessFileOrArray getSafeFile() {
    return tokens.getSafeFile();
  }

  protected PdfReaderInstance getPdfReaderInstance(PdfWriter writer) {
    return new PdfReaderInstance(this, writer);
  }

  /**
   * @return a PdfObject
   */
  public static PdfObject getPdfObjectRelease(PdfObject obj) {
    PdfObject obj2 = getPdfObject(obj);
    return obj2;
  }

  /**
   * Reads a <CODE>PdfObject</CODE> resolving an indirect reference if needed.
   * 
   * @param obj
   *          the <CODE>PdfObject</CODE> to read
   * @return the resolved <CODE>PdfObject</CODE>
   */
  public static PdfObject getPdfObject(PdfObject obj) {
    if (obj == null)
      return null;
    if (!obj.isIndirect())
      return obj;
    if (obj instanceof PRIndirectReference) {
      PRIndirectReference ref = (PRIndirectReference) obj;
      int idx = ref.getNumber();
      return ref.getReader().getPdfObject(idx);
    }
    return null;
  }

  /**
   * @return a PdfObject
   */
  public PdfObject getPdfObjectRelease(int idx) {
    return getPdfObject(idx);
  }

  /**
   * @return aPdfObject
   */
  public PdfObject getPdfObject(int idx) {
    try {
      if (idx < 0 || idx >= xrefObj.size())
        return null;
      PdfObject obj = xrefObj.get(idx);
      if (obj != null)
        return obj;
      if (idx * 2 >= xref.length)
        return null;
      obj = readSingleObject(idx);
      if (obj != null)
        ;
      return obj;
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    }
  }

  protected PdfObject readSingleObject(int k) throws IOException {
    strings.clear();
    int k2 = k * 2;
    int pos = xref[k2];
    if (pos < 0)
      return null;
    if (xref[k2 + 1] > 0)
      pos = objStmToOffset.get(xref[k2 + 1]);
    if (pos == 0)
      return null;
    tokens.seek(pos);
    tokens.nextValidToken();
    if (tokens.getTokenType() != PRTokeniser.TK_NUMBER)
      tokens.throwError(MessageLocalization
          .getComposedMessage("invalid.object.number"));
    objNum = tokens.intValue();
    tokens.nextValidToken();
    if (tokens.getTokenType() != PRTokeniser.TK_NUMBER)
      tokens.throwError(MessageLocalization
          .getComposedMessage("invalid.generation.number"));
    objGen = tokens.intValue();
    tokens.nextValidToken();
    if (!tokens.getStringValue().equals("obj"))
      tokens.throwError(MessageLocalization
          .getComposedMessage("token.obj.expected"));
    PdfObject obj;
    try {
      obj = readPRObject();
      if (obj.isStream()) {
        checkPRStreamLength((PRStream) obj);
      }
    } catch (Exception e) {
      obj = null;
    }
    if (xref[k2 + 1] > 0) {
      obj = readOneObjStm((PRStream) obj, xref[k2]);
    }
    xrefObj.set(k, obj);
    return obj;
  }

  protected PdfObject readOneObjStm(PRStream stream, int idx)
      throws IOException {
    int first = stream.getAsNumber(PdfName.FIRST).intValue();
    byte[] b = getStreamBytes(stream, tokens.getFile());
    PRTokeniser saveTokens = tokens;
    tokens = new PRTokeniser(b);
    try {
      int address = 0;
      boolean ok = true;
      ++idx;
      for (int k = 0; k < idx; ++k) {
        ok = tokens.nextToken();
        if (!ok)
          break;
        if (tokens.getTokenType() != PRTokeniser.TK_NUMBER) {
          ok = false;
          break;
        }
        ok = tokens.nextToken();
        if (!ok)
          break;
        if (tokens.getTokenType() != PRTokeniser.TK_NUMBER) {
          ok = false;
          break;
        }
        address = tokens.intValue() + first;
      }
      if (!ok)
        throw new InvalidPdfException(
            MessageLocalization.getComposedMessage("error.reading.objstm"));
      tokens.seek(address);
      return readPRObject();
    } finally {
      tokens = saveTokens;
    }
  }

  private void checkPRStreamLength(PRStream stream) throws IOException {
    int fileLength = tokens.length();
    int start = stream.getOffset();
    boolean calc = false;
    int streamLength = 0;
    PdfObject obj = getPdfObjectRelease(stream.get(PdfName.LENGTH));
    if (obj != null && obj.type() == PdfObject.NUMBER) {
      streamLength = ((PdfNumber) obj).intValue();
      if (streamLength + start > fileLength - 20)
        calc = true;
      else {
        tokens.seek(start + streamLength);
        String line = tokens.readString(20);
        if (!line.startsWith("\nendstream")
            && !line.startsWith("\r\nendstream")
            && !line.startsWith("\rendstream") && !line.startsWith("endstream"))
          calc = true;
      }
    } else
      calc = true;
    if (calc) {
      byte[] tline = new byte[16];
      tokens.seek(start);
      while (true) {
        int pos = tokens.getFilePointer();
        if (!tokens.readLineSegment(tline))
          break;
        if (equalsn(tline, endstream)) {
          streamLength = pos - start;
          break;
        }
        if (equalsn(tline, endobj)) {
          tokens.seek(pos - 16);
          String s = tokens.readString(16);
          int index = s.indexOf("endstream");
          if (index >= 0)
            pos = pos - 16 + index;
          streamLength = pos - start;
          break;
        }
      }
    }
    stream.setLength(streamLength);
  }

  protected PdfDictionary readDictionary() throws IOException {
    PdfDictionary dic = new PdfDictionary();
    while (true) {
      tokens.nextValidToken();
      if (tokens.getTokenType() == PRTokeniser.TK_END_DIC)
        break;
      if (tokens.getTokenType() != PRTokeniser.TK_NAME)
        tokens.throwError(MessageLocalization
            .getComposedMessage("dictionary.key.is.not.a.name"));
      PdfName name = new PdfName(tokens.getStringValue(), false);
      PdfObject obj = readPRObject();
      int type = obj.type();
      if (-type == PRTokeniser.TK_END_DIC)
        tokens.throwError(MessageLocalization
            .getComposedMessage("unexpected.gt.gt"));
      if (-type == PRTokeniser.TK_END_ARRAY)
        tokens.throwError(MessageLocalization
            .getComposedMessage("unexpected.close.bracket"));
      dic.put(name, obj);
    }
    return dic;
  }

  protected PdfArray readArray() throws IOException {
    PdfArray array = new PdfArray();
    while (true) {
      PdfObject obj = readPRObject();
      int type = obj.type();
      if (-type == PRTokeniser.TK_END_ARRAY)
        break;
      if (-type == PRTokeniser.TK_END_DIC)
        tokens.throwError(MessageLocalization
            .getComposedMessage("unexpected.gt.gt"));
      array.add(obj);
    }
    return array;
  }

  // Track how deeply nested the current object is, so
  // we know when to return an individual null or boolean, or
  // reuse one of the static ones.
  private int readDepth = 0;

  protected PdfObject readPRObject() throws IOException {
    tokens.nextValidToken();
    int type = tokens.getTokenType();
    switch (type) {
    case PRTokeniser.TK_START_DIC: {
      ++readDepth;
      PdfDictionary dic = readDictionary();
      --readDepth;
      int pos = tokens.getFilePointer();
      // be careful in the trailer. May not be a "next" token.
      boolean hasNext;
      do {
        hasNext = tokens.nextToken();
      } while (hasNext && tokens.getTokenType() == PRTokeniser.TK_COMMENT);

      if (hasNext && tokens.getStringValue().equals("stream")) {
        // skip whitespaces
        int ch;
        do {
          ch = tokens.read();
        } while (ch == 32 || ch == 9 || ch == 0 || ch == 12);
        if (ch != '\n')
          ch = tokens.read();
        if (ch != '\n')
          tokens.backOnePosition(ch);
        PRStream stream = new PRStream(this, tokens.getFilePointer());
        stream.putAll(dic);

        return stream;
      } else {
        tokens.seek(pos);
        return dic;
      }
    }
    case PRTokeniser.TK_START_ARRAY: {
      ++readDepth;
      PdfArray arr = readArray();
      --readDepth;
      return arr;
    }
    case PRTokeniser.TK_NUMBER:
      return new PdfNumber(tokens.getStringValue());
    case PRTokeniser.TK_STRING:
      PdfString str = new PdfString(tokens.getStringValue(), null)
          .setHexWriting(tokens.isHexString());
      if (strings != null)
        strings.add(str);

      return str;
    case PRTokeniser.TK_NAME: {
      PdfName cachedName = PdfName.staticNames.get(tokens
          .getStringValue());
      if (readDepth > 0 && cachedName != null) {
        return cachedName;
      } else {
        // an indirect name (how odd...), or a non-standard one
        return new PdfName(tokens.getStringValue(), false);
      }
    }
    case PRTokeniser.TK_REF:
      int num = tokens.getReference();
      return new PRIndirectReference(this, num,
          tokens.getGeneration());
    case PRTokeniser.TK_ENDOFFILE:
      throw new IOException(
          MessageLocalization.getComposedMessage("unexpected.end.of.file"));
    default:
      String sv = tokens.getStringValue();
      if ("null".equals(sv)) {
        if (readDepth == 0) {
          return new PdfNull();
        } // else
        return PdfNull.PDFNULL;
      } else if ("true".equals(sv)) {
        if (readDepth == 0) {
          return new PdfBoolean(true);
        } // else
        return PdfBoolean.PDFTRUE;
      } else if ("false".equals(sv)) {
        if (readDepth == 0) {
          return new PdfBoolean(false);
        } // else
        return PdfBoolean.PDFFALSE;
      }
      return new PdfLiteral(-type, tokens.getStringValue());
    }
  }

  /**
   * Decodes a stream that has the FlateDecode filter.
   * 
   * @param in
   *          the input data
   * @return the decoded data
   */
  public static byte[] FlateDecode(byte[] in) {
    byte[] b = FlateDecode(in, true);
    if (b == null)
      return FlateDecode(in, false);
    return b;
  }

  /**
   * @return a byte array
   */
  public static byte[] decodePredictor(byte[] in, PdfObject dicPar) {
    if (dicPar == null || !dicPar.isDictionary())
      return in;
    PdfDictionary dic = (PdfDictionary) dicPar;
    PdfObject obj = getPdfObject(dic.get(PdfName.PREDICTOR));
    if (obj == null || !obj.isNumber())
      return in;
    int predictor = ((PdfNumber) obj).intValue();
    if (predictor < 10)
      return in;
    int width = 1;
    obj = getPdfObject(dic.get(PdfName.COLUMNS));
    if (obj != null && obj.isNumber())
      width = ((PdfNumber) obj).intValue();
    int colors = 1;
    obj = getPdfObject(dic.get(PdfName.COLORS));
    if (obj != null && obj.isNumber())
      colors = ((PdfNumber) obj).intValue();
    int bpc = 8;
    obj = getPdfObject(dic.get(PdfName.BITSPERCOMPONENT));
    if (obj != null && obj.isNumber())
      bpc = ((PdfNumber) obj).intValue();
    DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(
        in));
    ByteArrayOutputStream fout = new ByteArrayOutputStream(in.length);
    int bytesPerPixel = colors * bpc / 8;
    int bytesPerRow = (colors * width * bpc + 7) / 8;
    byte[] curr = new byte[bytesPerRow];
    byte[] prior = new byte[bytesPerRow];

    // Decode the (sub)image row-by-row
    while (true) {
      // Read the filter type byte and a row of data
      int filter;
      try {
        filter = dataStream.read();
        if (filter < 0) {
          return fout.toByteArray();
        }
        dataStream.readFully(curr, 0, bytesPerRow);
      } catch (Exception e) {
        return fout.toByteArray();
      }

      switch (filter) {
      case 0: // PNG_FILTER_NONE
        break;
      case 1: // PNG_FILTER_SUB
        for (int i = bytesPerPixel; i < bytesPerRow; i++) {
          curr[i] += curr[i - bytesPerPixel];
        }
        break;
      case 2: // PNG_FILTER_UP
        for (int i = 0; i < bytesPerRow; i++) {
          curr[i] += prior[i];
        }
        break;
      case 3: // PNG_FILTER_AVERAGE
        for (int i = 0; i < bytesPerPixel; i++) {
          curr[i] += prior[i] / (byte) 2;
        }
        for (int i = bytesPerPixel; i < bytesPerRow; i++) {
          curr[i] += ((curr[i - bytesPerPixel] & 0xff) + (prior[i] & 0xff)) / (byte) 2;
        }
        break;
      case 4: // PNG_FILTER_PAETH
        for (int i = 0; i < bytesPerPixel; i++) {
          curr[i] += prior[i];
        }

        for (int i = bytesPerPixel; i < bytesPerRow; i++) {
          int a = curr[i - bytesPerPixel] & 0xff;
          int b = prior[i] & 0xff;
          int c = prior[i - bytesPerPixel] & 0xff;

          int p = a + b - c;
          int pa = Math.abs(p - a);
          int pb = Math.abs(p - b);
          int pc = Math.abs(p - c);

          int ret;

          if ((pa <= pb) && (pa <= pc)) {
            ret = a;
          } else if (pb <= pc) {
            ret = b;
          } else {
            ret = c;
          }
          curr[i] += (byte) (ret);
        }
        break;
      default:
        // Error -- unknown filter type
        throw new RuntimeException(
            MessageLocalization.getComposedMessage("png.filter.unknown"));
      }
      try {
        fout.write(curr);
      } catch (IOException ioe) {
        // Never happens
      }

      // Swap curr and prior
      byte[] tmp = prior;
      prior = curr;
      curr = tmp;
    }
  }

  /**
   * A helper to FlateDecode.
   * 
   * @param in
   *          the input data
   * @param strict
   *          <CODE>true</CODE> to read a correct stream. <CODE>false</CODE> to
   *          try to read a corrupted stream
   * @return the decoded data
   */
  public static byte[] FlateDecode(byte[] in, boolean strict) {
    ByteArrayInputStream stream = new ByteArrayInputStream(in);
    InflaterInputStream zip = new InflaterInputStream(stream);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] b = new byte[strict ? 4092 : 1];
    try {
      int n;
      while ((n = zip.read(b)) >= 0) {
        out.write(b, 0, n);
      }
      zip.close();
      out.close();
      return out.toByteArray();
    } catch (Exception e) {
      if (strict)
        return null;
      return out.toByteArray();
    }
  }

  /**
   * Decodes a stream that has the ASCIIHexDecode filter.
   * 
   * @param in
   *          the input data
   * @return the decoded data
   */
  public static byte[] ASCIIHexDecode(byte[] in) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    boolean first = true;
    int n1 = 0;
      for (byte b : in) {
          int ch = b & 0xff;
          if (ch == '>')
              break;
          if (PRTokeniser.isWhitespace(ch))
              continue;
          int n = PRTokeniser.getHex(ch);
          if (n == -1)
              throw new RuntimeException(
                      MessageLocalization
                              .getComposedMessage("illegal.character.in.asciihexdecode"));
          if (first)
              n1 = n;
          else
              out.write((byte) ((n1 << 4) + n));
          first = !first;
      }
    if (!first)
      out.write((byte) (n1 << 4));
    return out.toByteArray();
  }

  /**
   * Decodes a stream that has the ASCII85Decode filter.
   * 
   * @param in
   *          the input data
   * @return the decoded data
   */
  public static byte[] ASCII85Decode(byte[] in) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int state = 0;
    int[] chn = new int[5];
      for (byte b : in) {
          int ch = b & 0xff;
          if (ch == '~')
              break;
          if (PRTokeniser.isWhitespace(ch))
              continue;
          if (ch == 'z' && state == 0) {
              out.write(0);
              out.write(0);
              out.write(0);
              out.write(0);
              continue;
          }
          if (ch < '!' || ch > 'u')
              throw new RuntimeException(
                      MessageLocalization
                              .getComposedMessage("illegal.character.in.ascii85decode"));
          chn[state] = ch - '!';
          ++state;
          if (state == 5) {
              state = 0;
              int r = 0;
              for (int j = 0; j < 5; ++j)
                  r = r * 85 + chn[j];
              out.write((byte) (r >> 24));
              out.write((byte) (r >> 16));
              out.write((byte) (r >> 8));
              out.write((byte) r);
          }
      }
    int r;
    // We'll ignore the next two lines for the sake of perpetuating broken
    // PDFs
    // if (state == 1)
    // throw new
    // RuntimeException(MessageLocalization.getComposedMessage("illegal.length.in.ascii85decode"));
    if (state == 2) {
      r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + 85 * 85 * 85
          + 85 * 85 + 85;
      out.write((byte) (r >> 24));
    } else if (state == 3) {
      r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + chn[2] * 85 * 85
          + 85 * 85 + 85;
      out.write((byte) (r >> 24));
      out.write((byte) (r >> 16));
    } else if (state == 4) {
      r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + chn[2] * 85 * 85
          + chn[3] * 85 + 85;
      out.write((byte) (r >> 24));
      out.write((byte) (r >> 16));
      out.write((byte) (r >> 8));
    }
    return out.toByteArray();
  }

  /**
   * Get the content from a stream applying the required filters.
   * 
   * @param stream
   *          the stream
   * @param file
   *          the location where the stream is
   * @throws IOException
   *           on error
   * @return the stream content
   */
  public static byte[] getStreamBytes(PRStream stream,
      RandomAccessFileOrArray file) throws IOException {
    PdfObject filter = getPdfObjectRelease(stream.get(PdfName.FILTER));
    byte[] b = getStreamBytesRaw(stream, file);
    List<PdfObject> filters = new ArrayList<>();
    filters = addFilters(filters, filter);
    List<PdfObject> dp = new ArrayList<>();
    PdfObject dpo = getPdfObjectRelease(stream.get(PdfName.DECODEPARMS));
    if (dpo == null || (!dpo.isDictionary() && !dpo.isArray()))
      dpo = getPdfObjectRelease(stream.get(PdfName.DP));
    if (dpo != null) {
      if (dpo.isDictionary())
        dp.add(dpo);
      else if (dpo.isArray())
        dp = ((PdfArray) dpo).getElements();
    }
    String name;
    for (int j = 0; j < filters.size(); ++j) {
      name = getPdfObjectRelease(filters.get(j))
          .toString();
        switch (name) {
            case "/FlateDecode":
            case "/Fl": {
                b = FlateDecode(b);
                PdfObject dicParam;
                if (j < dp.size()) {
                    dicParam = dp.get(j);
                    b = decodePredictor(b, dicParam);
                }
                break;
            }
            case "/ASCIIHexDecode":
            case "/AHx":
                b = ASCIIHexDecode(b);
                break;
            case "/ASCII85Decode":
            case "/A85":
                b = ASCII85Decode(b);
                break;
            case "/Crypt":
                break;
            default:
                throw new UnsupportedPdfException(
                        MessageLocalization.getComposedMessage(
                                "the.filter.1.is.not.supported", name));
        }
    }
    return b;
  }

  /**
   * Get the content from a stream as it is without applying any filter.
   * 
   * @param stream
   *          the stream
   * @param file
   *          the location where the stream is
   * @throws IOException
   *           on error
   * @return the stream content
   */
  public static byte[] getStreamBytesRaw(PRStream stream,
      RandomAccessFileOrArray file) throws IOException {
    byte[] b;
    if (stream.getOffset() < 0)
      b = stream.getBytes();
    else {
      b = new byte[stream.getLength()];
      file.seek(stream.getOffset());
      file.readFully(b);
    }
    return b;
  }

  private static List<PdfObject> addFilters(List<PdfObject> filters, PdfObject filter) {
    if (filter != null) {
      if (filter.isName())
        filters.add(filter);
      else if (filter.isArray())
        filters = ((PdfArray) filter).getElements();
    }
    return filters;
  }

  /**
   * Get the content from a stream as it is without applying any filter.
   * 
   * @param stream
   *          the stream
   * @throws IOException
   *           on error
   * @return the stream content
   */
  public static byte[] getStreamBytesRaw(PRStream stream) throws IOException {
    try (RandomAccessFileOrArray rf = stream.getReader().getSafeFile()) {
      rf.reOpen();
      return getStreamBytesRaw(stream, rf);
    }
  }

  /**
   * Gets the number of xref objects.
   * 
   * @return the number of xref objects
   */
  public int getXrefSize() {
    return xrefObj.size();
  }

  private static boolean equalsn(byte[] a1, byte[] a2) {
    int length = a2.length;
    for (int k = 0; k < length; ++k) {
      if (a1[k] != a2[k])
        return false;
    }
    return true;
  }

}
